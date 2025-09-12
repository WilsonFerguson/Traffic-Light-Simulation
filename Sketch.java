import library.core.*;
import java.util.*;

class Sketch extends Applet {

    ArrayList<Movement> movements;
    ArrayList<Movement> movementsCars;
    ArrayList<Movement> movementsBikes;

    ArrayList<Person> traffic;
    ArrayList<Integer> timeWaitingCars;
    ArrayList<Integer> timeWaitingBikes;

    float centerMedian;

    public static int unitsPerMeter = 10;

    float accelerationBike = 0.231f * unitsPerMeter / 60.0f;
    float accelerationCar = 1.75f * unitsPerMeter / 60.0f;

    float speedBike = 4.16f * unitsPerMeter / 60.0f;
    float speedCar = 8.3f * unitsPerMeter / 60.0f;
    float speedCarTurn = 7 * unitsPerMeter / 60.0f; // 5
    float speedWalk = 1.4f * unitsPerMeter / 60.0f;

    int yellowCarTurn = 3 * 60;
    int yellowCarStraight = ceil(3.5 * 60);
    int yellowBike = 2 * 60;
    int yellowWalk = ceil(2.5 * 60);

    int greenTime = 4 * 60;
    int maximumTypicalGreenTime = 13 * 60;

    /**
     * If true, then bikes will only use tegelijk groen. If false, they will never
     * use it.
     */
    public static boolean tegelijkGroen = false;

    int laneWidthCar = 3 * unitsPerMeter;
    int laneWidthBike = 2 * unitsPerMeter;

    public void setup() {
        size(1200, 1200);
        frameRate(60);

        if (tegelijkGroen)
            maximumTypicalGreenTime = 20 * 60;

        centerMedian = width / 15;

        movements = new ArrayList<Movement>();
        movementsCars = new ArrayList<Movement>();
        movementsBikes = new ArrayList<Movement>();

        traffic = new ArrayList<Person>();
        timeWaitingCars = new ArrayList<Integer>();
        timeWaitingBikes = new ArrayList<Integer>();

        // North
        createMovementDirection(0);
        // South
        createMovementDirection(PI);
        // East
        createMovementDirection(PI / 2);
        // West
        createMovementDirection(-PI / 2);

        for (Movement movement : movements) {
            movement.calculateClearanceTimes(laneWidthCar / 5.0f);
        }

        IntersectionManager.movements = movements;
        if (!tegelijkGroen) {
            Phase phase1 = new Phase(maximumTypicalGreenTime, movements, 1, 2, 4, 6, 7, 9);
            Phase phase2 = new Phase(maximumTypicalGreenTime, movements, 0, 5, 3, 18, 8, 13);
            Phase phase3 = new Phase(maximumTypicalGreenTime, movements, 19, 16, 17, 11, 12, 14);
            Phase phase4 = new Phase(maximumTypicalGreenTime, movements, 10, 15, 3, 18, 8, 13);
            IntersectionManager.addPhases(phase1, phase2, phase3, phase4);
        } else {
            Phase phase1 = new Phase(maximumTypicalGreenTime, movements, 1, 2, 3, 7, 8, 9);
            Phase phase2 = new Phase(maximumTypicalGreenTime, movements, 0, 6, 3, 21, 9, 15);
            Phase phase3 = new Phase(maximumTypicalGreenTime, movements, 13, 14, 19, 20, 15, 21);
            Phase phase4 = new Phase(maximumTypicalGreenTime, movements, 12, 18, 3, 21, 9, 15);
            Phase phase5 = new Phase(maximumTypicalGreenTime, movements, 5, 4, 22, 23, 10, 11, 16, 17);
            IntersectionManager.addPhases(phase1, phase2, phase3, phase4, phase5);
        }

        IntersectionManager.start();
    }

    public void createMovementDirection(float rotation) {
        Movement carLeft = new Movement(movements, MovementType.CAR_LEFT, greenTime, yellowCarTurn, speedCarTurn,
                laneWidthCar,
                accelerationCar,
                Settings.PATH_CAR);
        Movement carStraight1 = new Movement(movements, MovementType.CAR_STRAIGHT, greenTime, yellowCarStraight,
                speedCar,
                laneWidthCar, accelerationCar,
                Settings.PATH_CAR);
        Movement carStraight2 = new Movement(movements, MovementType.CAR_STRAIGHT, greenTime, yellowCarStraight,
                speedCar,
                laneWidthCar, accelerationCar,
                Settings.PATH_CAR);
        Movement carRight = new Movement(movements, MovementType.CAR_RIGHT, greenTime, yellowCarTurn, speedCarTurn,
                laneWidthCar,
                accelerationCar,
                Settings.PATH_CAR);
        Movement bikeStraight = new Movement(movements, MovementType.BIKE_STRAIGHT, greenTime, yellowBike, speedBike,
                laneWidthBike, accelerationBike,
                Settings.PATH_BIKE);
        Movement bikeTegelijk = new Movement(movements, MovementType.BIKE_TEGELIJK, greenTime, yellowBike, speedBike,
                laneWidthBike, accelerationBike,
                Settings.PATH_BIKE);
        // TODO: peds

        ArrayList<Movement> movementsTemp = new ArrayList<Movement>();
        movementsTemp.add(carLeft);
        movementsTemp.add(carStraight1);
        movementsTemp.add(carStraight2);
        movementsTemp.add(carRight);
        movementsTemp.add(bikeStraight);
        if (tegelijkGroen)
            movementsTemp.add(bikeTegelijk);

        float centerX = width / 2 + centerMedian / 2;
        float stopLineCars = height / 2 + centerMedian / 2 + laneWidthCar * 6 + laneWidthBike;
        float stopLineBikes = height / 2 + centerMedian / 2 + laneWidthCar * 3.5f;

        // Left turn
        generateStraightIntros(carLeft, centerX, stopLineCars);
        carLeft.addIntersectionNode(centerX, stopLineCars);
        float finalX = width / 2 - centerMedian / 2 - laneWidthCar * 3.5f;
        float finalY = height / 2 - centerMedian / 2;
        float startY = finalY + (centerX - finalX);
        int n = 6;
        for (int i = 1; i <= n; i++) {
            carLeft.addIntersectionNode(centerX, map(i, 0, n + 1, stopLineCars, startY));
        }
        n = 40;
        for (int i = 0; i <= n; i++) {
            float angle = map(i, 0, n, 0, PI / 2);
            float x = centerX - (cos(angle) - 1) * (finalX - centerX);
            float y = startY + sin(angle) * (finalY - startY);
            carLeft.addIntersectionNode(x, y);
        }
        float xInc = finalX;
        n = 10;
        for (int i = 1; i < n; i++) {
            // xInc = finalX - laneWidthCar * 0.75f * i;
            xInc = map(i, 0, n, finalX, finalX - laneWidthCar * 3.5);
            carLeft.addIntersectionNode(xInc, finalY);
        }
        generateStraightExits(carLeft, xInc, finalY, 0, finalY);

        centerX += laneWidthCar;

        // Straights
        generateStraightIntros(carStraight1, centerX, stopLineCars);
        finalY = height / 2 - centerMedian / 2 - laneWidthCar * 6 - laneWidthBike;
        n = 40;
        for (int i = 0; i <= n; i++) {
            carStraight1.addIntersectionNode(centerX, map(i, 0, n, stopLineCars, finalY));
        }
        generateStraightExits(carStraight1, centerX, finalY, centerX, 0);

        centerX += laneWidthCar;

        generateStraightIntros(carStraight2, centerX, stopLineCars);

        for (int i = 0; i <= n; i++) {
            carStraight2.addIntersectionNode(centerX, map(i, 0, n, stopLineCars, finalY));
        }
        generateStraightExits(carStraight2, centerX, finalY, centerX, 0);

        centerX += laneWidthCar;

        // Right turn
        generateStraightIntros(carRight, centerX, stopLineCars);
        finalX = centerX + laneWidthCar * 3;
        finalY = stopLineBikes - laneWidthCar * 1.5f;
        startY = finalY + (finalX - centerX);
        n = 5;
        for (int i = 0; i < n; i++) {
            carRight.addIntersectionNode(centerX, map(i, 0, n, stopLineCars, startY));
        }
        n = 14;
        for (int i = 0; i <= n; i++) {
            float angle = map(i, 0, n, PI, PI / 2);
            float x = centerX + (cos(angle) + 1) * laneWidthCar * 2;
            float y = startY - sin(angle) * laneWidthCar * 3;
            carRight.addIntersectionNode(x, y);
        }
        float xCurr = carRight.pathIntersection.get(carRight.pathIntersection.size() - 1).x;
        n = 6;
        for (int i = 1; i <= n; i++) {
            // xInc = xCurr + laneWidthCar * 0.5f * i;
            xInc = map(i, 0, n, xCurr, xCurr + laneWidthCar * 1.5);
            carRight.addIntersectionNode(xInc, finalY);
        }
        generateStraightExits(carRight, xInc, finalY, width, finalY);

        centerX += laneWidthCar * 2;

        // Bikes straight
        generateStraightIntros(bikeStraight, centerX, stopLineBikes);
        n = 40;
        finalY = height / 2 - centerMedian / 2 - laneWidthCar * 6 - laneWidthBike;
        for (int i = 0; i <= n; i++) {
            bikeStraight.addIntersectionNode(centerX, map(i, 0, n, stopLineBikes, finalY));
        }
        generateStraightExits(bikeStraight, centerX, finalY, centerX, 0);

        // Bikes tegelijk (so left turn here)
        if (tegelijkGroen) {
            // Move the bikes slightly over so they queue up on the left side of the (now
            // slightly wider) bike path
            centerX -= laneWidthBike / 2;
            generateStraightIntros(bikeTegelijk, centerX, stopLineBikes);
            finalX = width / 2 - centerMedian / 2 - laneWidthCar * 3.5f;
            // For finalY, let's just rotate centerX 90 degreees around center
            float xTemp = centerX - width / 2;
            float yTemp = stopLineBikes - height / 2;
            finalY = round(yTemp * cos(-PI / 2) + xTemp * sin(-PI / 2) + height / 2);
            n = 48;
            for (int i = 0; i <= n; i++) {
                float theta = map(i, 0, n, 0, PI / 2);
                float x = finalX + (centerX - finalX) * cos(theta);
                float y = stopLineBikes - (stopLineBikes - finalY) * sin(theta);
                bikeTegelijk.addIntersectionNode(x, y);
            }
            generateStraightExits(bikeTegelijk, finalX, finalY, 0, finalY);
        }

        // Rotate
        for (Movement movement : movementsTemp) {
            if (!tegelijkGroen && movement.type == MovementType.BIKE_TEGELIJK)
                continue;
            movement.rotatePath(rotation);
        }

        movements.addAll(movementsTemp);
        movementsCars.add(carLeft);
        movementsCars.add(carStraight1);
        movementsCars.add(carStraight2);
        movementsCars.add(carRight);
        movementsBikes.add(bikeStraight);
        if (tegelijkGroen)
            movementsBikes.add(bikeTegelijk);
    }

    void generateStraightIntros(Movement movement, float x, float finalY) {
        int n = 16;
        for (int i = 0; i <= n; i++) {
            movement.addIntroNode(x, map(i, 0, n, height, finalY));
        }
    }

    void generateStraightExits(Movement movement, float startX, float startY, float finalX, float finalY) {
        int n = 40;
        for (int i = 0; i <= n; i++) {
            movement.addExitNode(map(i, 0, n, startX, finalX), map(i, 0, n, startY, finalY));
        }
    }

    public void draw() {
        background(27, 135, 11);

        if (random(1) < 0.06)
            createPerson();

        IntersectionManager.update();

        drawIntersectionMarkings();

        for (Movement movement : movementsCars) {
            if (movement.signal == Signal.RED)
                movement.draw();
        }
        for (Movement movement : movementsBikes) {
            if (movement.signal == Signal.RED)
                movement.draw();
        }
        for (Movement movement : movementsCars) {
            if (movement.signal == Signal.YELLOW)
                movement.draw();
        }
        for (Movement movement : movementsBikes) {
            if (movement.signal == Signal.YELLOW)
                movement.draw();
        }
        for (Movement movement : movementsCars) {
            if (movement.signal == Signal.GREEN)
                movement.draw();
        }
        for (Movement movement : movementsBikes) {
            if (movement.signal == Signal.GREEN)
                movement.draw();
        }

        // Draw traffic lights at end so they are on top
        for (Movement movement : movements) {
            movement.drawTrafficLight();
        }

        for (int i = traffic.size() - 1; i >= 0; i--) {
            Person person = traffic.get(i);
            if (person.update()) {
                person.movement.removeTraffic(person);
                traffic.remove(i);

                if (person.movement.type == MovementType.CAR_STRAIGHT || person.movement.type == MovementType.CAR_RIGHT
                        || person.movement.type == MovementType.CAR_LEFT) {
                    timeWaitingCars.add(person.timeWaiting);
                } else if (person.movement.type == MovementType.BIKE_STRAIGHT
                        || person.movement.type == MovementType.BIKE_TEGELIJK) {
                    timeWaitingBikes.add(person.timeWaiting);
                }

                continue;
            }
            person.draw();
        }

        // for (int i = 0; i < movements.size(); i++) {
        // fill(255);
        // textSize(30);
        // textAlign(CENTER);
        // text(i, PVector.add(movements.get(i).pathIntro.get(0),
        // PVector.sub(PVector.center(),
        // movements.get(i).pathIntro.get(0)).setMag(30)));
        // }

        textSize(50);
        fill(255);
        textAlign(LEFT);
        text("Phase: " + (IntersectionManager.currentPhaseIndex + 1), 10, 30);

        if (timeWaitingCars.size() > 0) {
            int timeWaitingCarsSum = 0;
            for (int i = 0; i < timeWaitingCars.size(); i++) {
                timeWaitingCarsSum += timeWaitingCars.get(i);
            }
            textSize(30);
            text("Cars: " + round((float) timeWaitingCarsSum / (float) timeWaitingCars.size() / 60.0, 1) + " s", 10,
                    100);
        }
        if (timeWaitingBikes.size() > 0) {
            int timeWaitingBikesSum = 0;
            for (int i = 0; i < timeWaitingBikes.size(); i++) {
                timeWaitingBikesSum += timeWaitingBikes.get(i);
            }
            textSize(30);
            text("Bikes: " + round((float) timeWaitingBikesSum / (float) timeWaitingBikes.size() / 60.0, 1) + " s", 10,
                    140);
        }
    }

    void drawIntersectionMarkings() {
        noStroke();
        rectMode(CENTER);

        fill(Settings.ROAD_BACKGROUND);
        square(PVector.center(), laneWidthCar * 13);

        fill(Settings.ROAD_BACKGROUND);
        square(PVector.center(), centerMedian * 2);
    }

    String temp = "";

    public void keyPressed() {
        if (keyString.equals("Enter")) {
            String[] split = temp.split(",");
            int first = Integer.parseInt(split[0]);
            int second = Integer.parseInt(split[1]);
            Movement movement = movements.get(first);
            Movement movement2 = movements.get(second);
            if (movement.clearanceTimes.get(movement2) == null) {
                println("null");
            } else {
                println(movement.clearanceTimes.get(movement2));
            }
            temp = "";
        } else {
            temp += key;
        }
    }

    public void mousePressed() {
        createPerson();
    }

    public void createPerson() {
        Movement movement = movements.get(getWeightedRandomMovement());
        Person person = null;
        float speed = speedCar;
        float acceleration = accelerationCar;
        switch (movement.type) {
            case BIKE_STRAIGHT:
                speed = speedBike;
                acceleration = accelerationBike;
                break;
            case BIKE_TEGELIJK:
                speed = speedBike;
                acceleration = accelerationBike;
                break;
            case CAR_STRAIGHT:
                speed = speedCar;
                acceleration = accelerationCar;
                break;
            case CAR_RIGHT:
                speed = speedCarTurn;
                acceleration = accelerationCar;
                break;
            case CAR_LEFT:
                speed = speedCarTurn;
                acceleration = accelerationCar;
                break;
            case PEDESTRIAN:
                break;
        }
        person = new Person(movement, speed, acceleration);
        traffic.add(person);
    }

    public int getWeightedRandomMovement() {
        if (!tegelijkGroen) {
            int[][] rows = {
                    { 1, 2, 6, 7 },
                    { 0, 3, 4, 5, 8, 9 },
                    { 11, 12, 16, 17 },
                    { 15, 18, 19, 10, 13, 14 }
            };

            float r = random(1);
            // weights: row0 most, row3 least
            float[] weights = { 0.35f, 0.25f, 0.22f, 0.18f };
            float sum = 0;
            for (int i = 0; i < rows.length; i++) {
                sum += weights[i];
                if (r < sum) {
                    return rows[i][(int) random(rows[i].length)];
                }
            }
            return -1; // should not hit
        } else {
            return (int) random(movements.size());
        }
    }

}
