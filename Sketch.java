import library.core.*;
import java.util.*;

class Sketch extends Applet {

    Road[] roads;
    ArrayList<Movement> movements;
    ArrayList<Movement> movementsCars;
    ArrayList<Movement> movementsBikes;

    ArrayList<Person> traffic;
    ArrayList<Integer> timeWaitingCars;
    ArrayList<Integer> timeWaitingBikes;

    public static int unitsPerMeter = 10;

    public static float accelerationBike = 0.231f * unitsPerMeter / 60.0f;
    public static float accelerationCar = 1.75f * unitsPerMeter / 60.0f;

    public static float speedBike = 4.16f * unitsPerMeter / 60.0f;
    public static float speedCar = 8.3f * unitsPerMeter / 60.0f;
    public static float speedCarTurn = 7 * unitsPerMeter / 60.0f; // 5
    public static float speedWalk = 1.4f * unitsPerMeter / 60.0f;

    public static int yellowCar = (int) Math.ceil(3.5 * 60);
    public static int yellowBike = 2 * 60;
    public static int yellowWalk = (int) Math.ceil(2.5 * 60);

    public static int greenTime = 4 * 60;
    int maximumTypicalGreenTime = 13 * 60;

    double personCreationChance = 0.06; // Chance for person to be spawned each frame
    double personCarChance = 0.8; // Chance for person to be a car
    double personRoads02Chance = 0.6; // Chance for person to be on roads 0 or 2 (main roads)

    // Defaults:
    // Medium traffic: 0.06, 0.7, 0.6
    // Light traffic: 0.01, 0.92, 0.6

    /**
     * If true, then bikes will only use tegelijk groen. If false, they will never
     * use it.
     */
    public static boolean tegelijkGroen = false;

    public static int laneWidthCar = 3 * unitsPerMeter;
    public static int laneWidthBike = 2 * unitsPerMeter;
    public static float centerMedian;

    private class PhaseDirection {
        int roadIndex;
        MovementType[] types;

        public PhaseDirection(int roadIndex, MovementType... movementTypes) {
            this.roadIndex = roadIndex;
            this.types = movementTypes;
        }
    }

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

        MovementType CL = MovementType.CAR_LEFT;
        MovementType CS = MovementType.CAR_STRAIGHT;
        MovementType CR = MovementType.CAR_RIGHT;
        MovementType BS = MovementType.BIKE_STRAIGHT;
        MovementType BT = MovementType.BIKE_TEGELIJK;

        roads = new Road[4];
        roads[0] = new Road(0).addMovements(CL, CS, CS, CR, BS);
        roads[1] = new Road(-PI / 2).addMovements(CL, CS, CS, CR, BS);
        roads[2] = new Road(PI).addMovements(CL, CS, CS, CR, BS);
        roads[3] = new Road(PI / 2).addMovements(CL, CS, CS, CR, BS);

        for (int i = 0; i < 4; i++) {
            roads[i].getOtherRoads(roads, i);
        }
        for (Road road : roads) {
            road.createStartPoints();
        }
        for (Road road : roads) {
            road.createEndPoints();
        }
        for (Road road : roads) {
            road.createMovements(movements, movementsCars, movementsBikes);
        }

        for (Movement movement : movements) {
            movement.calculateClearanceTimes(laneWidthCar / 5.0f);
        }

        IntersectionManager.movements = movements;
        if (!tegelijkGroen) {
            Phase phase1 = createPhase(new PhaseDirection(0, CS, BS), new PhaseDirection(2, CS, BS));
            Phase phase2 = createPhase(new PhaseDirection(0, CL, CR), new PhaseDirection(2, CL, CR),
                    new PhaseDirection(1, CR), new PhaseDirection(3, CR));
            Phase phase3 = createPhase(new PhaseDirection(1, CS, BS), new PhaseDirection(3, CS, BS));
            Phase phase4 = createPhase(new PhaseDirection(1, CL, CR), new PhaseDirection(3, CL, CR),
                    new PhaseDirection(0, CR), new PhaseDirection(2, CR));
            IntersectionManager.addPhases(phase1, phase2, phase3, phase4);
        } else {
            Phase phase1 = createPhase(new PhaseDirection(0, CS, CR), new PhaseDirection(1, CR),
                    new PhaseDirection(2, CS, CR), new PhaseDirection(3, CR));
            Phase phase2 = createPhase(new PhaseDirection(0, CL, CR), new PhaseDirection(1, CR),
                    new PhaseDirection(2, CL, CR), new PhaseDirection(3, CR));
            Phase phase3 = createPhase(new PhaseDirection(0, CR), new PhaseDirection(1, CS, CR),
                    new PhaseDirection(2, CR), new PhaseDirection(3, CS, CR));
            Phase phase4 = createPhase(new PhaseDirection(0, CR), new PhaseDirection(1, CL, CR),
                    new PhaseDirection(2, CR), new PhaseDirection(3, CL, CR));
            Phase phase5 = createPhase(new PhaseDirection(0, BS, BT), new PhaseDirection(1, BS, BT),
                    new PhaseDirection(2, BS, BT), new PhaseDirection(3, BS, BT));
            IntersectionManager.addPhases(phase1, phase2, phase3, phase4, phase5);
        }

        IntersectionManager.start();
    }

    private Phase createPhase(PhaseDirection... directions) {
        ArrayList<Movement> activeMovements = new ArrayList<>();
        for (PhaseDirection direction : directions) {
            for (MovementType type : direction.types) {
                for (Movement movement : roads[direction.roadIndex].movements.get(type)) {
                    activeMovements.add(movement);
                }
            }
        }

        int[] indices = new int[activeMovements.size()];
        int i = 0;
        while (activeMovements.size() > 0) {
            int index = movements.indexOf(activeMovements.get(0));
            activeMovements.remove(0);
            indices[i] = index;
            i++;
        }

        return new Phase(maximumTypicalGreenTime, movements, indices);

    }

    public void draw() {
        background(27, 135, 11);

        if (random(1) < personCreationChance)
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
            movement.drawTrafficLightAndStopline();
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
        rectMode(CORNER);

        fill(Settings.ROAD_BACKGROUND);
        PVector topLeft = new PVector(roads[2].movements.get(MovementType.BIKE_STRAIGHT).get(0).path.get(0).x,
                roads[1].movements.get(MovementType.BIKE_STRAIGHT).get(0).path.get(0).y);
        PVector bottomRight = new PVector(roads[0].movements.get(MovementType.BIKE_STRAIGHT).get(0).path.get(0).x,
                roads[3].movements.get(MovementType.BIKE_STRAIGHT).get(0).path.get(0).y);
        rect(topLeft, PVector.sub(bottomRight, topLeft));
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
        // Movement movement = movements.get(getWeightedRandomMovement());
        Movement movement = generateMovement();

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

    public Movement generateMovement() {
        // return movements.get(random(new int[] { 0, 1, 2, 8 }));
        Movement movement = null;
        Road road;
        if (random(1) < personRoads02Chance) {
            road = roads[random(new int[] { 0, 2 })];
        } else {
            road = roads[random(new int[] { 1, 3 })];
        }
        int index = 0;
        if (random(1) < personCarChance) {
            index = (int) random(road.indexBikes);
        } else {
            index = (int) random(road.indexBikes, road.movementTypes.length);
        }
        MovementType type = road.movementTypes[index];
        if (type == MovementType.CAR_LEFT)
            movement = road.movements.get(type).get(index);
        else if (type == MovementType.CAR_STRAIGHT)
            movement = road.movements.get(type).get(index - road.indexStraight);
        else if (type == MovementType.CAR_RIGHT)
            movement = road.movements.get(type).get(index - road.indexRight);
        else
            movement = road.movements.get(type).get(index - road.indexBikes);

        return movement;
    }
}
