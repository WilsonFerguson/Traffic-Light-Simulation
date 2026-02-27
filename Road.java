import library.core.*;
import java.util.*;

/**
 * Contains all of the lanes from a cardinal direction.
 */
public class Road extends PComponent implements EventIgnorer {

    MovementType[] movementTypes;
    float[] movementStartPointsX;
    /**
     * Points at the end of the intersection for each movement
     */
    PVector[] movementEndpoints;
    HashMap<MovementType, ArrayList<Movement>> movements;
    ArrayList<Movement> movementsAll;
    /**
     * The first index in the movements array where the given movement starts
     */
    int indexLeft, indexStraight, indexRight, indexBikes, indexPeds;
    int numLefts, numStraights, numRights, numBikes, numPeds;

    Road left, straight, right;

    /**
     * Based on where this road comes from, the whole road should be rotated by this
     * amount
     */
    float rotationAngle;
    Origin origin;

    float x;

    public Road(float angle, Origin origin) {
        rotationAngle = angle;
        this.origin = origin;
        x = getInitialX();
    }

    public Road addMovements(MovementType... types) {
        movementTypes = types;
        movementStartPointsX = new float[types.length];
        movementEndpoints = new PVector[types.length];
        movements = new HashMap<>();
        movementsAll = new ArrayList<>();

        indexLeft = -1;
        indexStraight = -1;
        indexRight = -1;
        indexBikes = -1;
        indexPeds = -1;

        numLefts = 0;
        numStraights = 0;
        numRights = 0;
        numBikes = 0;
        numPeds = 0;

        for (MovementType type : types) {
            movements.put(type, new ArrayList<>());
            switch (type) {
                case CAR_LEFT:
                    numLefts++;
                    break;
                case CAR_STRAIGHT:
                    numStraights++;
                    break;
                case CAR_RIGHT:
                    numRights++;
                    break;
                case BIKE_STRAIGHT:
                    numBikes++;
                    break;
                case BIKE_TEGELIJK:
                    numBikes++;
                    break;
                case PEDESTRIAN:
                    numPeds++;
                    break;
            }
        }

        for (int i = 0; i < types.length; i++) {
            if (types[i] == MovementType.CAR_LEFT && indexLeft == -1)
                indexLeft = i;
            else if (types[i] == MovementType.CAR_STRAIGHT && indexStraight == -1)
                indexStraight = i;
            else if (types[i] == MovementType.CAR_RIGHT && indexRight == -1)
                indexRight = i;
            else if ((types[i] == MovementType.BIKE_STRAIGHT || types[i] == MovementType.BIKE_TEGELIJK)
                    && indexBikes == -1)
                indexBikes = i;
            else if (types[i] == MovementType.PEDESTRIAN && indexPeds == -1)
                indexPeds = i;
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
        for (int i = 0; i < movementTypes.length; i++) {
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
            // Don't increment for pedestrians
        }
    }

    public void createEndPoints() {
        for (int i = 0; i < movementTypes.length; i++) {
            MovementType type = movementTypes[i];

            switch (type) {
                case CAR_LEFT:
                    // If num straights of right road < numLefts then:
                    // We allow one left turn to be below the left most straight. the others then
                    // just keep stacking upwards
                    float finalX = width / 2 - abs(width / 2
                            - (straight.movementStartPointsX[straight.movementTypes.length - 1] + Sketch.laneWidthCar));

                    float finalY = 0;
                    // Number of straight movements that the right road has

                    if (right.numStraights != 0 && numLefts <= right.numStraights) {
                        finalY = height / 2 - abs((width / 2 - right.movementStartPointsX[right.indexStraight]));
                    } else {
                        // Determine the left most straight movement (from the right road) and then base
                        // everything off of that.
                        // If the right road has no straight movements, then we just go based on the
                        // center median
                        // TODO: incorporate OV support into this and push this value up to accomodate
                        // for the OV lane
                        float baseY = 0;
                        if (right.numStraights == 0) {
                            baseY = height / 2 - abs(getInitialX() - width / 2);
                        } else {
                            baseY = height / 2 - abs((width / 2 - right.movementStartPointsX[right.indexStraight]));
                        }

                        int currentLeft = i - indexLeft;
                        if (currentLeft == 0) {
                            finalY = baseY + Sketch.laneWidthCar;
                        } else {
                            finalY = baseY - Sketch.laneWidthCar * (currentLeft - 1);
                        }
                    }

                    movementEndpoints[i] = new PVector(finalX, finalY);
                    break;
                case CAR_STRAIGHT:
                    finalX = this.movementStartPointsX[i];
                    finalY = height / 2 - abs((width / 2
                            - (right.movementStartPointsX[right.movementTypes.length - 1] + Sketch.laneWidthCar)));
                    movementEndpoints[i] = new PVector(finalX, finalY);
                    break;
                case CAR_RIGHT:
                    break;
                case BIKE_STRAIGHT:
                    finalX = this.movementStartPointsX[i];
                    int rightLastCarIndex = -1;
                    if (right.indexBikes != -1) {
                        rightLastCarIndex = right.indexBikes;
                    } else if (right.indexRight != -1) {
                        rightLastCarIndex = right.indexRight + right.movements.get(MovementType.CAR_RIGHT).size() - 1;
                    } else if (right.indexStraight != -1) {
                        rightLastCarIndex = right.indexStraight + right.movements.get(MovementType.CAR_STRAIGHT).size()
                                - 1;
                    } else if (right.indexLeft != -1) {
                        rightLastCarIndex = right.indexLeft + right.movements.get(MovementType.CAR_LEFT).size() - 1;
                    } else {
                        // TODO: ov support
                    }
                    finalY = height / 2 - abs((width / 2 - right.movementStartPointsX[rightLastCarIndex]));
                    movementEndpoints[i] = new PVector(finalX, finalY);
                    break;
                case BIKE_TEGELIJK:
                    finalX = width / 2 - abs(
                            width / 2 - (straight.movementStartPointsX[straight.indexBikes] - Sketch.laneWidthCar / 2));
                    finalY = height / 2 - abs((width / 2 - right.movementStartPointsX[right.indexBikes]));
                    movementEndpoints[i] = new PVector(finalX, finalY);
                    break;
                case PEDESTRIAN:
                    break;
            }
        }
    }

    /**
     * Second pass for creating end points where right turns are then created.
     * This pass is needed because these movements depend on the left turn
     * positions.
     */
    public void createEndPoints2() {
        for (int i = 0; i < movementTypes.length; i++) {
            MovementType type = movementTypes[i];
            if (type != MovementType.CAR_RIGHT)
                continue;

            float finalX = this.movementStartPointsX[this.movementTypes.length - 1] + Sketch.laneWidthCar;

            int currentRight = i - indexRight;
            float baseY = -height;
            // we case about left straight, top left
            if (left.numStraights != 0) {
                baseY = height / 2
                        + abs((width / 2 - left.movementStartPointsX[left.indexStraight + left.numStraights - 1]));
            }
            if (straight.numLefts != 0) {
                float contender = height / 2
                        + abs(height / 2 - straight.movementEndpoints[straight.indexLeft + straight.numLefts - 1].y);
                baseY = max(baseY, contender);
            }
            // If baseY is still -height, then we have to come up with some other solution.
            if (baseY == -height) {
                if (left.numBikes != 0) {
                    baseY = height / 2
                            + abs((width / 2 - left.movementStartPointsX[left.indexBikes + left.numBikes - 1]))
                            - Sketch.laneWidthCar;
                } else {
                    baseY = height / 2 + abs(width / 2 - getInitialX()) + Sketch.laneWidthCar * numRights;
                    // TODO: OV support
                }
            }
            float finalY = baseY - Sketch.laneWidthCar * ((numRights - currentRight) - 1);

            movementEndpoints[i] = new PVector(finalX, finalY);
        }
    }

    /**
     * Third pass for creating pedestrians.
     */
    public void createEndPoints3() {
        for (int i = 0; i < movementTypes.length; i++) {
            MovementType type = movementTypes[i];
            if (type != MovementType.PEDESTRIAN)
                continue;

            float finalX = this.movementStartPointsX[i];

            float finalY = height;
            if (i == indexPeds) {
                if (left.numStraights != 0) {
                    finalY = height / 2 + abs(width / 2 - left.movementStartPointsX[left.indexStraight]);
                }
                if (straight.numLefts != 0) {
                    float contender = height / 2 + abs(height / 2 - straight.movementEndpoints[straight.indexLeft].y);
                    finalY = min(finalY, contender);
                }
                if (numRights != 0) {
                    float contender = movementEndpoints[indexRight].y;
                    finalY = min(finalY, contender);
                }
                // TODO: OV support

                finalY -= Sketch.laneWidthCar / 2;
                if (finalY == height - Sketch.laneWidthCar / 2) {
                    finalY = height / 2 + abs(width / 2 - getInitialX());
                }
            } else {
                int fartherIndex = -1;
                if (right.numRights != 0) {
                    fartherIndex = right.indexRight + right.numRights - 1;
                } else if (right.numStraights != 0) {
                    fartherIndex = right.indexStraight + right.numStraights - 1;
                } else if (right.numLefts != 0) {
                    fartherIndex = right.indexLeft + right.numLefts - 1;
                }
                // TODO: OV support?

                finalY = height / 2
                        - abs(width / 2 - (right.movementStartPointsX[fartherIndex] + Sketch.laneWidthCar / 2));
            }
            movementEndpoints[i] = new PVector(finalX, finalY);
        }
    }

    public void createMovements(ArrayList<Movement> movementsAll, ArrayList<Movement> movementsCars,
            ArrayList<Movement> movementsBikes, ArrayList<Movement> movementsPedestrians) {

        float stopLineCars = height / 2
                + abs((width / 2 - (left.movementStartPointsX[left.movementTypes.length - 1] + Sketch.laneWidthBike)));
        // Don't want the intersection to be too squashed so
        if (abs(stopLineCars - height / 2) < abs(width / 2 - getInitialX()) * 3) {
            stopLineCars = height / 2 + abs(width / 2 - getInitialX()) * 3;
        }

        // float stopLineBikes = height / 2
        // + abs((width / 2 - (left.movementStartPointsX[left.indexRight - 1] +
        // Sketch.laneWidthCar)));
        float stopLineBikes = 0;
        float stopLinePeds1 = 0;
        if (left.numStraights != 0) {
            stopLineBikes = height / 2
                    + abs(width / 2 - (left.movementStartPointsX[left.indexStraight + left.numStraights - 1]));
        }
        if (straight.numLefts != 0) {
            float contender = height / 2
                    + abs(height / 2 - straight.movementEndpoints[straight.indexLeft + straight.numLefts - 1].y);
            stopLineBikes = max(stopLineBikes, contender);
        }
        if (numRights != 0) {
            float contender = movementEndpoints[indexRight + numRights - 1].y;
            stopLineBikes = max(stopLineBikes, contender);
        }
        // TODO: OV support (every road but right road)

        if (stopLineBikes == 0) {
            // TODO: OV support (right road)
            if (right.numLefts != 0) {
                stopLineBikes = height / 2 + abs(width / 2 - right.movementStartPointsX[right.indexLeft]);
            } else if (right.numStraights != 0) {
                stopLineBikes = height / 2 + abs(width / 2 - right.movementStartPointsX[right.indexStraight]);
            } else if (right.numRights != 0) {
                stopLineBikes = height / 2 + abs(width / 2 - right.movementStartPointsX[right.indexRight]);
            } else {
                // Subtract lane width because it'll be added back in and I want the stop lines
                // to be at the same y
                stopLineBikes = stopLineCars - Sketch.laneWidthCar;
            }

            // TODO: temp because peds must always be 2 stages right now:
            stopLinePeds1 = stopLineCars;
        }
        stopLineBikes += Sketch.laneWidthCar;
        if (stopLinePeds1 == 0)
            stopLinePeds1 = stopLineBikes;

        float stopLinePeds2 = height / 2 - abs(width / 2 - right.movementStartPointsX[0]) + Sketch.laneWidthCar / 2;

        for (int i = 0; i < movementTypes.length; i++) {
            switch (movementTypes[i]) {
                case CAR_LEFT:
                    createCarLeft(movementsAll, movementsCars, i, stopLineCars, origin, Direction.LEFT);
                    break;
                case CAR_RIGHT:
                    createCarRight(movementsAll, movementsCars, i, stopLineCars, origin, Direction.RIGHT);
                    break;
                case CAR_STRAIGHT:
                    createCarStraight(movementsAll, movementsCars, i, stopLineCars, origin, Direction.STRAIGHT);
                    break;
                case BIKE_STRAIGHT:
                    createBikeStraight(movementsAll, movementsBikes, i, stopLineBikes, origin, Direction.STRAIGHT);
                    break;
                case BIKE_TEGELIJK:
                    createBikeTegelijk(movementsAll, movementsBikes, i, stopLineBikes, origin, Direction.TEGELIJK);
                    break;
                case PEDESTRIAN:
                    createPedestrian(movementsAll, movementsPedestrians, i, stopLinePeds1, stopLinePeds2, origin,
                            Direction.STRAIGHT);
                    break;
            }
        }

        for (ArrayList<Movement> movs : movements.values()) {
            for (Movement movement : movs) {
                if (!Sketch.tegelijkGroen && movement.type == MovementType.BIKE_TEGELIJK)
                    continue;

                movement.rotatePath(rotationAngle);
                movementsAll.add(movement);
                this.movementsAll.add(movement);
            }
        }
    }

    private void createCarLeft(ArrayList<Movement> movementsAll, ArrayList<Movement> movementsToAdd, int index,
            float stopLine, Origin origin, Direction direction) {
        Movement movement = new Movement(this, movementsAll, MovementType.CAR_LEFT, origin, direction, Sketch.greenTime,
                Sketch.yellowCar,
                Sketch.redWaitTime,
                Sketch.speedCarTurn, Sketch.laneWidthCar, Sketch.accelerationCar, Settings.PATH_CAR);

        float x = movementStartPointsX[index];
        generateStraightIntros(movement, 15, x, stopLine);

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

        generateStraightExits(movement, 40, end.x, end.y, 0, end.y);
        movements.get(MovementType.CAR_LEFT).add(movement);
        movementsToAdd.add(movement);
    }

    private void createCarStraight(ArrayList<Movement> movementsAll, ArrayList<Movement> movementsToAdd, int index,
            float stopLine, Origin origin, Direction direction) {
        Movement movement = new Movement(this, movementsAll, MovementType.CAR_STRAIGHT, origin, direction,
                Sketch.greenTime,
                Sketch.yellowCar,
                Sketch.redWaitTime,
                Sketch.speedCar, Sketch.laneWidthCar, Sketch.accelerationCar, Settings.PATH_CAR);

        float x = movementStartPointsX[index];

        generateStraightIntros(movement, 15, x, stopLine);
        int n = 40;
        for (int i = 0; i <= n; i++) {
            movement.addIntersectionNode(x, map(i, 0, n, stopLine, movementEndpoints[index].y));
        }
        generateStraightExits(movement, 40, x, movementEndpoints[index].y, x, 0);

        movements.get(MovementType.CAR_STRAIGHT).add(movement);
        movementsToAdd.add(movement);
    }

    private void createCarRight(ArrayList<Movement> movementsAll, ArrayList<Movement> movementsToAdd, int index,
            float stopLine, Origin origin, Direction direction) {
        Movement movement = new Movement(this, movementsAll, MovementType.CAR_RIGHT, origin, direction,
                Sketch.greenTime, Sketch.yellowCar, Sketch.redWaitTime,
                Sketch.speedCarTurn, Sketch.laneWidthCar, Sketch.accelerationCar, Settings.PATH_CAR);

        float x = movementStartPointsX[index];
        generateStraightIntros(movement, 15, x, stopLine);

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

        generateStraightExits(movement, 40, end.x, end.y, width, end.y);
        movements.get(MovementType.CAR_RIGHT).add(movement);
        movementsToAdd.add(movement);
    }

    private void createBikeStraight(ArrayList<Movement> movementsAll, ArrayList<Movement> movementsToAdd, int index,
            float stopLine, Origin origin, Direction direction) {
        Movement movement = new Movement(this, movementsAll, MovementType.BIKE_STRAIGHT, origin, direction,
                Sketch.greenTime,
                Sketch.yellowBike,
                Sketch.redWaitTime,
                Sketch.speedBike, Sketch.laneWidthBike, Sketch.accelerationBike, Settings.PATH_BIKE);

        float x = movementStartPointsX[index];

        generateStraightIntros(movement, 40, x, stopLine);
        int n = 50;
        for (int i = 0; i <= n; i++) {
            movement.addIntersectionNode(x, map(i, 0, n, stopLine, movementEndpoints[index].y));
        }
        generateStraightExits(movement, 50, x, movementEndpoints[index].y, x, 0);

        movements.get(MovementType.BIKE_STRAIGHT).add(movement);
        movementsToAdd.add(movement);
    }

    private void createBikeTegelijk(ArrayList<Movement> movementsAll, ArrayList<Movement> movementsToAdd, int index,
            float stopLine, Origin origin, Direction direction) {
        Movement movement = new Movement(this, movementsAll, MovementType.BIKE_TEGELIJK, origin, direction,
                Sketch.greenTime,
                Sketch.yellowBike,
                Sketch.redWaitTime,
                Sketch.speedBike, Sketch.laneWidthBike, Sketch.accelerationBike, Settings.PATH_BIKE);

        float x = movementStartPointsX[index];
        generateStraightIntros(movement, 30, x, stopLine);

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

        generateStraightExits(movement, 40, end.x, end.y, 0, end.y);
        movements.get(MovementType.BIKE_TEGELIJK).add(movement);
        movementsToAdd.add(movement);
    }

    private void createPedestrian(ArrayList<Movement> movementsAll, ArrayList<Movement> movementsToAdd, int index,
            float stopLine1, float stopLine2, Origin origin, Direction direction) {
        Movement movement = new Movement(this, movementsAll, MovementType.PEDESTRIAN, origin, direction,
                Sketch.greenTime,
                Sketch.yellowWalk,
                Sketch.redWaitTime,
                Sketch.speedWalk, Sketch.laneWidthPed, Sketch.speedWalk, Settings.PATH_PED);

        float x = movementStartPointsX[index];
        boolean first = index == indexPeds;

        // Bottom
        float y1 = height;
        // First stop line
        float y2 = stopLine1;
        // Start of median
        float y3 = movementEndpoints[indexPeds].y;
        // Second stop line (end of median)
        float y4 = stopLine2;
        // End of intersection
        float y5 = movementEndpoints[indexPeds + 1].y;

        // Intro
        float startY = first ? y1 : y3;
        float endY = first ? y2 : y4;
        int n = first ? 70 : 15;
        for (int i = 0; i < n; i++) {
            movement.addIntroNode(x, map(i, 0, n, startY, endY));
        }

        // Intersection
        startY = first ? y2 : y4;
        endY = first ? y3 : y5;
        n = first ? 20 : 30;
        for (int i = 0; i <= n; i++) {
            movement.addIntersectionNode(x, map(i, 0, n, startY, endY));
        }

        startY = endY;
        endY = first ? startY : 0;
        n = first ? 1 : 70;
        generateStraightExits(movement, n, x, startY, x, endY);

        movements.get(MovementType.PEDESTRIAN).add(movement);
        movementsToAdd.add(movement);
    }

    private void generateStraightIntros(Movement movement, int n, float x, float finalY) {
        for (int i = 0; i < n; i++) {
            movement.addIntroNode(x, map(i, 0, n, height, finalY));
        }
    }

    private void generateStraightExits(Movement movement, int n, float startX, float startY, float finalX,
            float finalY) {
        for (int i = 0; i <= n; i++) {
            movement.addExitNode(map(i, 0, n, startX, finalX), map(i, 0, n, startY, finalY));
        }
    }

    public float getInitialX() {
        return width / 2 + Sketch.centerMedian / 2;
    }

    public Movement getLastMovement() {
        if (numPeds != 0)
            return movements.get(MovementType.PEDESTRIAN).getLast();
        else if (numBikes != 0)
            return movements.get(MovementType.BIKE_STRAIGHT).getLast();
        else if (numRights != 0)
            return movements.get(MovementType.CAR_RIGHT).getLast();
        else if (numStraights != 0)
            return movements.get(MovementType.CAR_STRAIGHT).getLast();
        else if (numLefts != 0)
            return movements.get(MovementType.CAR_LEFT).getLast();
        // TODO: OV support

        return null;
    }

}
