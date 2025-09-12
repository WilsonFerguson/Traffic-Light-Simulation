import library.core.*;
import java.util.*;

/**
 * Contains all of the lanes from a cardinal direction.
 */
public class Road extends PComponent implements EventIgnorer {

    MovementType[] movementTypes;
    /**
     * Points at the end of the intersection for each movement
     */
    float[] movementStartPointsX;
    PVector[] movementEndpoints;
    Movement[] movements;
    HashMap<MovementType, Movement[]> movementsByType;
    /**
     * The first index in the movements array where the given movement starts
     */
    int indexStraight, indexRight, indexBikes;

    Road left, straight, right;

    /**
     * Based on where this road comes from, the whole road should be rotated by this
     * amount
     */
    float rotationAngle;

    float x;

    public Road(float angle) {
        rotationAngle = angle;

        indexStraight = -1;
        indexRight = -1;
        indexBikes = -1;

        x = width / 2 + Sketch.centerMedian / 2;
    }

    public Road addMovements(MovementType... types) {
        movementTypes = types;
        movementStartPointsX = new float[types.length];
        movementEndpoints = new PVector[types.length];
        movements = new Movement[types.length];

        for (int i = 0; i < types.length; i++) {
            if (types[i] == MovementType.CAR_STRAIGHT && indexStraight == -1)
                indexStraight = i;
            else if (types[i] == MovementType.CAR_RIGHT && indexRight == -1)
                indexRight = i;
            else if ((types[i] == MovementType.BIKE_STRAIGHT || types[i] == MovementType.BIKE_TEGELIJK)
                    && indexBikes == -1)
                indexBikes = i;
        }

        return this;
    }

    public void getOtherRoads(Road[] roads, int myIndex) {
        right = roads[(myIndex + 1) % roads.length];
        straight = roads[(myIndex + 2) % roads.length];
        left = roads[myIndex > 0 ? myIndex - 1 : roads.length - 1];
    }

    public void createStartPoints() {
        MovementType previous = null;
        for (int i = 0; i < movements.length; i++) {
            MovementType type = movementTypes[i];

            // Add padding between cars and bike path
            boolean isBike = type == MovementType.BIKE_STRAIGHT
                    || type == MovementType.BIKE_TEGELIJK;
            if (previous == MovementType.CAR_RIGHT && isBike)
                x += Sketch.laneWidthCar;

            movementStartPointsX[i] = x;
            previous = type;

            // Increment x
            String typeString = type.toString();
            if (typeString.contains("CAR"))
                x += Sketch.laneWidthCar;
            else if (type == MovementType.BIKE_TEGELIJK)
                x += Sketch.laneWidthBike / 2; // lane width / 2 so that the bike path isn't so wide and that the left
                                               // turning bikes are on the left
            else if (type == MovementType.BIKE_STRAIGHT)
                x += Sketch.laneWidthBike;
        }
    }

    public void createEndPoints() {
        for (int i = 0; i < movements.length; i++) {
            MovementType type = movementTypes[i];

            switch (type) {
                case CAR_LEFT:
                    float finalX = width / 2 - abs(width / 2
                            - (straight.movementStartPointsX[straight.movements.length - 1] + Sketch.laneWidthCar));
                    float finalY = height / 2 - abs((width / 2 - right.movementStartPointsX[right.indexStraight]));
                    movementEndpoints[i] = new PVector(finalX, finalY);
                    break;
                case CAR_STRAIGHT:
                    finalX = this.movementStartPointsX[i];
                    finalY = height / 2 - abs((width / 2
                            - (right.movementStartPointsX[right.movements.length - 1] + Sketch.laneWidthCar)));
                    movementEndpoints[i] = new PVector(finalX, finalY);
                    break;
                case CAR_RIGHT:
                    finalX = this.movementStartPointsX[this.movements.length - 1] + Sketch.laneWidthCar;
                    finalY = height / 2 + abs((width / 2 - left.movementStartPointsX[left.indexRight - 1]));
                    movementEndpoints[i] = new PVector(finalX, finalY);
                    break;
                case BIKE_STRAIGHT:
                    finalX = this.movementStartPointsX[i];
                    finalY = height / 2 - abs((width / 2 - right.movementStartPointsX[right.indexBikes]));
                    movementEndpoints[i] = new PVector(finalX, finalY);
                    break;
                case BIKE_TEGELIJK:
                    finalX = width / 2 - abs(
                            width / 2 - (straight.movementStartPointsX[straight.indexBikes] - Sketch.laneWidthCar / 2));
                    finalY = height / 2 - abs((width / 2 - right.movementStartPointsX[right.indexBikes]));
                    movementEndpoints[i] = new PVector(finalX, finalY);
                    break;
                case PEDESTRIAN:
                    // TODO: implement peds
                    break;
            }
        }
    }

    public void createMovements(ArrayList<Movement> movementsAll, ArrayList<Movement> movementsCars,
            ArrayList<Movement> movementsBikes) {
        float stopLineCars = height / 2
                + abs((width / 2 - (left.movementStartPointsX[left.movements.length - 1] + Sketch.laneWidthBike)));
        float stopLineBikes = height / 2
                + abs((width / 2 - (left.movementStartPointsX[left.indexRight - 1] + Sketch.laneWidthCar)));
        for (int i = 0; i < movements.length; i++) {
            switch (movementTypes[i]) {
                case CAR_LEFT:
                    createCarLeft(movementsAll, movementsCars, i, stopLineCars);
                    break;
                case CAR_RIGHT:
                    createCarRight(movementsAll, movementsCars, i, stopLineCars);
                    break;
                case CAR_STRAIGHT:
                    createCarStraight(movementsAll, movementsCars, i, stopLineCars);
                    break;
                case BIKE_STRAIGHT:
                    createBikeStraight(movementsAll, movementsBikes, i, stopLineBikes);
                    break;
                case BIKE_TEGELIJK:
                    createBikeTegelijk(movementsAll, movementsBikes, i, stopLineBikes);
                    break;
                case PEDESTRIAN:
                    createPedestrian(movementsAll, i, stopLineBikes);
                    break;
            }
        }

        for (Movement movement : movements) {
            if (!Sketch.tegelijkGroen && movement.type == MovementType.BIKE_TEGELIJK)
                continue;

            movement.rotatePath(rotationAngle);
            movementsAll.add(movement);
        }
    }

    private void createCarLeft(ArrayList<Movement> movementsAll, ArrayList<Movement> movementsToAdd, int index,
            float stopLine) {
        Movement movement = new Movement(movementsAll, MovementType.CAR_LEFT, Sketch.greenTime, Sketch.yellowCar,
                Sketch.speedCarTurn, Sketch.laneWidthCar, Sketch.accelerationCar, Settings.PATH_CAR);

        float x = movementStartPointsX[index];
        generateStraightIntros(movement, x, stopLine);

        PVector start = new PVector(x, stopLine);
        PVector end = movementEndpoints[index];
        float radiusX = abs(end.x - start.x);
        float radiusY = abs(end.y - start.y);

        int n = 50;
        for (int i = 0; i <= n; i++) {
            float angle = map(i, 0, n, 0, PI / 2);

            float xTemp = end.x + radiusX * cos(angle);
            float yTemp = start.y - radiusY * sin(angle);
            movement.addIntersectionNode(xTemp, yTemp);
        }

        generateStraightExits(movement, end.x, end.y, 0, end.y);
        movements[index] = movement;
        movementsToAdd.add(movement);
    }

    private void createCarStraight(ArrayList<Movement> movementsAll, ArrayList<Movement> movementsToAdd, int index,
            float stopLine) {
        Movement movement = new Movement(movementsAll, MovementType.CAR_STRAIGHT, Sketch.greenTime, Sketch.yellowCar,
                Sketch.speedCar, Sketch.laneWidthCar, Sketch.accelerationCar, Settings.PATH_CAR);

        float x = movementStartPointsX[index];

        generateStraightIntros(movement, x, stopLine);
        int n = 40;
        for (int i = 0; i <= n; i++) {
            movement.addIntersectionNode(x, map(i, 0, n, stopLine, movementEndpoints[index].y));
        }
        generateStraightExits(movement, x, movementEndpoints[index].y, x, 0);

        movements[index] = movement;
        movementsToAdd.add(movement);
    }

    private void createCarRight(ArrayList<Movement> movementsAll, ArrayList<Movement> movementsToAdd, int index,
            float stopLine) {
        Movement movement = new Movement(movementsAll, MovementType.CAR_RIGHT, Sketch.greenTime, Sketch.yellowCar,
                Sketch.speedCarTurn, Sketch.laneWidthCar, Sketch.accelerationCar, Settings.PATH_CAR);

        float x = movementStartPointsX[index];
        generateStraightIntros(movement, x, stopLine);

        PVector start = new PVector(x, stopLine);
        PVector end = movementEndpoints[index];
        float radiusX = abs(end.x - start.x);
        float radiusY = abs(end.y - start.y);

        int n = 22;
        for (int i = 0; i <= n; i++) {
            float angle = map(i, 0, n, PI, PI / 2);

            float xTemp = end.x + radiusX * cos(angle);
            float yTemp = start.y - radiusY * sin(angle);
            movement.addIntersectionNode(xTemp, yTemp);
        }

        generateStraightExits(movement, end.x, end.y, width, end.y);
        movements[index] = movement;
        movementsToAdd.add(movement);
    }

    private void createBikeStraight(ArrayList<Movement> movementsAll, ArrayList<Movement> movementsToAdd, int index,
            float stopLine) {
        Movement movement = new Movement(movementsAll, MovementType.BIKE_STRAIGHT, Sketch.greenTime, Sketch.yellowBike,
                Sketch.speedBike, Sketch.laneWidthBike, Sketch.accelerationBike, Settings.PATH_BIKE);

        float x = movementStartPointsX[index];

        generateStraightIntros(movement, x, stopLine);
        int n = 40;
        for (int i = 0; i <= n; i++) {
            movement.addIntersectionNode(x, map(i, 0, n, stopLine, movementEndpoints[index].y));
        }
        generateStraightExits(movement, x, movementEndpoints[index].y, x, 0);

        movements[index] = movement;
        movementsToAdd.add(movement);
    }

    private void createBikeTegelijk(ArrayList<Movement> movementsAll, ArrayList<Movement> movementsToAdd, int index,
            float stopLine) {
        Movement movement = new Movement(movementsAll, MovementType.BIKE_TEGELIJK, Sketch.greenTime, Sketch.yellowBike,
                Sketch.speedBike, Sketch.laneWidthBike, Sketch.accelerationBike, Settings.PATH_BIKE);

        float x = movementStartPointsX[index];
        generateStraightIntros(movement, x, stopLine);

        PVector start = new PVector(x, stopLine);
        PVector end = movementEndpoints[index];
        float radiusX = abs(end.x - start.x);
        float radiusY = abs(end.y - start.y);

        int n = 48;
        for (int i = 0; i <= n; i++) {
            float theta = map(i, 0, n, 0, PI / 2);
            float xTemp = end.x + radiusX * cos(theta);
            float yTemp = start.y - radiusY * sin(theta);
            movement.addIntersectionNode(xTemp, yTemp);
        }

        generateStraightExits(movement, end.x, end.y, 0, end.y);
        movements[index] = movement;
        movementsToAdd.add(movement);
    }

    private void createPedestrian(ArrayList<Movement> movementsAll, int index, float stopLine) {
        // TODO: create pedestrian
    }

    private void generateStraightIntros(Movement movement, float x, float finalY) {
        int n = 20;
        for (int i = 0; i <= n; i++) {
            movement.addIntroNode(x, map(i, 0, n, height, finalY));
        }
    }

    private void generateStraightExits(Movement movement, float startX, float startY, float finalX, float finalY) {
        int n = 40;
        for (int i = 0; i <= n; i++) {
            movement.addExitNode(map(i, 0, n, startX, finalX), map(i, 0, n, startY, finalY));
        }
    }

}
