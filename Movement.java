import library.core.*;
import java.util.*;

class Movement extends PComponent implements EventIgnorer {

    ArrayList<Movement> movements;
    MovementType type;

    int greenTimeDefault;
    int yellowTime;
    int exitTime;

    /**
     * The signal will change at the given time in millis(), when we reach this time
     * we set the signal and remove the entry.
     */
    HashMap<Integer, Signal> signalTimeline;

    float speed, acceleration;

    int laneWidth;

    ArrayList<PVector> pathIntro;
    ArrayList<PVector> pathIntersection;
    ArrayList<PVector> pathExit;
    ArrayList<PVector> path;

    float pathIntersectionDistance;
    HashMap<Movement, Integer> clearanceTimes;

    int sensorIndex;
    int timeUntilGreenCounter = -1;
    int timeUntilGreenCounterMax = -1;

    ArrayList<Person> traffic;

    Signal signal;
    color pathColor;

    Phase phase = null;
    boolean changingGreen = false;
    boolean changingRed = false;
    int greenStartTime = -1;
    /**
     * When the movement will become red.
     */
    int redStartTime = -1;

    public Movement(ArrayList<Movement> movements, MovementType type, int greenTime, int yellowTime, float speed,
            int laneWidth, float acceleration,
            color pathColor) {
        this.movements = movements;
        this.type = type;

        greenTimeDefault = greenTime;
        this.yellowTime = yellowTime;

        signalTimeline = new HashMap<Integer, Signal>();

        this.speed = speed;
        this.acceleration = acceleration;

        this.laneWidth = laneWidth;

        pathIntro = new ArrayList<PVector>();
        pathIntersection = new ArrayList<PVector>();
        pathExit = new ArrayList<PVector>();
        path = new ArrayList<PVector>();

        clearanceTimes = new HashMap<Movement, Integer>();

        traffic = new ArrayList<Person>();

        signal = Signal.RED;
        this.pathColor = pathColor;
    }

    public void addIntroNode(double x, double y) {
        pathIntro.add(new PVector(x, y));

        path.add(new PVector(x, y));

    }

    public void addIntersectionNode(double x, double y) {
        // Calculate the sensor index so that at this index the traffic takes
        // greenTimeDefault to reach intersection. If there is traffic from this point
        // on (and before the intersection), then we want to be green
        // When the first intersection node is added, we will calculate this as no more
        // intro nodes will be added.
        if (pathIntersection.size() == 0) {
            sensorIndex = pathIntro.size() - 1;
            float dist = 0;
            while ((dist / speed) < greenTimeDefault) {
                dist += pathIntro.get(sensorIndex).dist(pathIntro.get(sensorIndex - 1));
                sensorIndex--;
                if (sensorIndex == 0)
                    throw new RuntimeException(
                            "It takes longer than the green time to get to the intersection, thus failing this calculation");
            }
            sensorIndex += 1;
        }

        pathIntersection.add(new PVector(x, y));
        if (pathIntersection.size() > 1) {
            pathIntersectionDistance += (pathIntersection.get(pathIntersection.size() - 1)
                    .dist(pathIntersection.get(pathIntersection.size() - 2)));
            exitTime = ceil(pathIntersectionDistance / speed);
        }

        if (pathIntersection.size() > 1)
            path.add(new PVector(x, y));
    }

    public void addExitNode(double x, double y) {
        pathExit.add(new PVector(x, y));
        if (pathExit.size() > 1)
            path.add(new PVector(x, y));
    }

    private PVector rotatePoint(PVector point, float rotation) {
        float x = point.x - width / 2;
        float y = point.y - height / 2;

        float newX = x * cos(rotation) - y * sin(rotation);
        float newY = x * sin(rotation) + y * cos(rotation);

        return new PVector(round(newX + width / 2, 2), round(newY + height / 2, 2));
    }

    public void rotatePath(float rotation) {
        for (PVector point : pathIntro) {
            pathIntro.set(pathIntro.indexOf(point), rotatePoint(point, rotation));
        }
        for (PVector point : pathIntersection) {
            pathIntersection.set(pathIntersection.indexOf(point), rotatePoint(point, rotation));
        }
        for (PVector point : pathExit) {
            pathExit.set(pathExit.indexOf(point), rotatePoint(point, rotation));
        }
        for (PVector point : path) {
            path.set(path.indexOf(point), rotatePoint(point, rotation));
        }
    }

    public void addTraffic(Person person) {
        traffic.add(person);
    }

    public void removeTraffic(Person person) {
        traffic.remove(person);
    }

    /**
     * Minimum green + yellow + exit time
     */
    public int getMinimumTime() {
        return greenTimeDefault + yellowTime + exitTime;
    }

    public int getTime(int maximumTypicalGreenTime) {
        return maximumTypicalGreenTime + yellowTime + exitTime;
    }

    /**
     * Returns the distance from the start of the intersection to the given index in
     * meters
     */
    public float getDistance(int index) {
        float dist = 0;
        for (int i = 0; i < index; i++) {
            dist += pathIntersection.get(i).dist(pathIntersection.get(i + 1));
        }

        return dist;
    }

    public void calculateClearanceTime(Movement movement, float threshold) {
        int myIndex = -1, otherIndex = -1;
        for (int i = 0; i < pathIntersection.size(); i++) {
            for (int j = 0; j < movement.pathIntersection.size(); j++) {
                float dist = pathIntersection.get(i).dist(movement.pathIntersection.get(j));
                if (dist < threshold) {
                    myIndex = i;
                    otherIndex = j;
                    break;
                }
            }
            if (myIndex != -1)
                break;
        }
        if (myIndex == -1)
            return;

        int exitTime = ceil(movement.getDistance(otherIndex) / movement.speed);
        float distToCollision = getDistance(myIndex);
        int entryTime = ceil((2 * acceleration * distToCollision + speed * speed) / (2 * acceleration * speed));

        int clearanceTime = exitTime - entryTime;

        clearanceTimes.put(movement, clearanceTime);
    }

    public void calculateClearanceTimes(float threshold) {
        for (Movement movement : movements) {
            if (movement == this)
                continue;
            // For tegelijk groen bikes, ignore the conflicting bike movements
            if (type == MovementType.BIKE_TEGELIJK && type == movement.type)
                continue;

            calculateClearanceTime(movement, threshold);
        }
    }

    public boolean canProceed(Person person) {
        // If someone is at person.currentIndex + 1, they can't move forward
        for (Person other : traffic) {
            if (other.currentIndex == person.currentIndex + 1) {
                return false;
            }
        }

        // As long as the person isn't at the light, they can move forward if the next
        // node is clear
        if (person.currentIndex != pathIntro.size()) {
            return true;
        } else {
            return signal != Signal.RED;
        }
    }

    public void begin(Phase phase, Phase outboundPhase) {
        this.phase = phase;
        int changeTime = frameCount;

        if (outboundPhase == null) {
            // 0 clearance time
        } else {
            // If I'm in the ending phase and I'm still green, just keep being green
            if (outboundPhase.movements.contains(this) && signal == Signal.GREEN) {
                // 0 clearance time
                return;
            } else {
                for (Movement movement : movements) {
                    if (!movement.changingRed || !clearanceTimes.containsKey(movement))
                        continue;

                    changeTime = max(changeTime, movement.redStartTime + clearanceTimes.get(movement));
                }
            }
        }

        signalTimeline.put(changeTime, Signal.GREEN);
        changingGreen = true;
    }

    /**
     * This assumes we have passed the minimum green time.
     */
    public void end() {
        if (signal == Signal.GREEN && frameCount < greenStartTime + greenTimeDefault) {
            signalTimeline.put(greenStartTime + greenTimeDefault, Signal.YELLOW);
            return;
        }
        if (!changingRed && signal != Signal.RED) {
            signal = Signal.YELLOW;
            redStartTime = frameCount + yellowTime;
            signalTimeline.put(redStartTime, Signal.RED);
            changingRed = true;
            greenStartTime = -1;
        }
    }

    public void update() {
        int timeUntilGreen = estimatedTimeUntilGreen();
        if (timeUntilGreen == -1 || timeUntilGreenCounter == -1) {
            timeUntilGreenCounter = timeUntilGreen;
            if (timeUntilGreenCounter != -1)
                timeUntilGreenCounterMax = timeUntilGreen;
        } else {
            if (timeUntilGreen > timeUntilGreenCounter)
                timeUntilGreenCounter = min(timeUntilGreenCounter + 8, timeUntilGreen);
            else if (timeUntilGreen < timeUntilGreenCounter)
                timeUntilGreenCounter = max(timeUntilGreenCounter - 8, timeUntilGreen);
        }

        for (Integer time : signalTimeline.keySet()) {
            if (frameCount >= time) {
                Signal signal = signalTimeline.get(time);
                signalTimeline.remove(time);

                switch (signal) {
                    case GREEN:
                        changeToGreen();
                        break;
                    case YELLOW:
                        end();
                        break;
                    case RED:
                        this.signal = Signal.RED;
                        break;
                }

                break;
            }
        }

        // Only turn off changingRed if we are red and have past exit time
        if (changingRed && frameCount >= redStartTime + exitTime) {
            changingRed = false;
        }

        if (signal == Signal.GREEN && !waitingTraffic() && frameCount - greenStartTime >= greenTimeDefault) {
            end();
        }
        if (signal == Signal.RED && waitingTraffic()) {
            // If tegelijk groen, don't allow bike splicing
            if (Sketch.tegelijkGroen && type == MovementType.BIKE_TEGELIJK || type == MovementType.BIKE_STRAIGHT) {
                return;
            }
            tryChangingToGreen();
        }
    }

    public void changeToGreen() {
        if (wantsGreen()) {
            signal = Signal.GREEN;
            greenStartTime = frameCount;
        }

        changingGreen = false;
    }

    /**
     * Returns true if we want to be green
     */
    boolean wantsGreen() {
        if (signal != Signal.RED)
            return false;

        return waitingTraffic();
    }

    boolean waitingTraffic() {
        for (Person person : traffic) {
            int index = person.currentIndex;
            // Note that when index = pathIntro, the person is waiting at the signal
            if (index < sensorIndex || index > pathIntro.size())
                continue;

            return true;
        }

        return false;
    }

    int waitingTrafficCount() {
        int count = 0;
        for (Person person : traffic) {
            int index = person.currentIndex;
            if (index < sensorIndex || index > pathIntro.size())
                continue;

            count++;
        }

        return count;
    }

    public void tryChangingToGreen() {
        if (phase != null && phase.activePhase) {
            // Get max waiting time due to other traffic
            int changeTime = frameCount;
            for (Movement movement : movements) {
                if (!clearanceTimes.containsKey(movement))
                    continue;

                if (movement.signal == Signal.RED && !movement.changingRed)
                    continue;

                // Now, the movements left are conflicting traffic that aren't fully cleared
                if (movement.changingRed) {
                    changeTime = max(changeTime, movement.redStartTime + clearanceTimes.get(movement));
                } else {
                    // So this signal is just green, so we have to force it off
                    int yellowStartTime = movement.greenStartTime + movement.greenTimeDefault;
                    movement.signalTimeline.put(yellowStartTime, Signal.YELLOW);
                    int redStartTime = yellowStartTime + movement.yellowTime;
                    changeTime = max(changeTime, redStartTime + clearanceTimes.get(movement));
                }
            }

            // If we could do a minimum green time before the phase ends, let's do it
            if (changeTime + greenTimeDefault < phase.phaseStartTime + phase.maximumTypicalGreenTime) {
                signalTimeline.put(changeTime, Signal.GREEN);
                changingGreen = true;
                changingRed = false;

                // We also must cancel any other conflicting movements that were going to turn
                // green
                for (Movement movement : movements) {
                    if (movement.changingGreen && clearanceTimes.containsKey(movement)) {
                        movement.changingGreen = false;
                        movement.signalTimeline.clear();
                    }
                }
            }
        } else {
            int changeTime = frameCount;
            for (Movement movement : movements) {
                if (!clearanceTimes.containsKey(movement))
                    continue;

                // If there are any conflicting phases that currently have a green or are
                // changing green, then give up
                if (movement.signal == Signal.GREEN || movement.changingGreen)
                    return;

                if (movement.signal == Signal.RED && !movement.changingRed)
                    continue;

                // Given the conditions, the signal must be changingRed
                changeTime = max(changeTime, movement.redStartTime + clearanceTimes.get(movement));
            }

            // If we could do a minimum green time before the phase ends or the next phase
            // includes me, let's do it
            Phase phase = IntersectionManager.phases.get(IntersectionManager.currentPhaseIndex);
            boolean enoughTime = changeTime + greenTimeDefault < phase.phaseStartTime + phase.maximumTypicalGreenTime;
            boolean inNextPhase = IntersectionManager.phases
                    .get((IntersectionManager.currentPhaseIndex + 1) % IntersectionManager.phases.size()).movements
                    .contains(this);
            if (enoughTime || inNextPhase) {
                signalTimeline.put(changeTime, Signal.GREEN);
                changingGreen = true;
                changingRed = false;
            }
        }
    }

    public void draw() {
        noStroke();
        fill(pathColor);
        if (Settings.HIGHLIGHT_SIGNALS) {
            switch (signal) {
                case GREEN:
                    fill(Settings.GREEN);
                    break;
                case YELLOW:
                    fill(Settings.YELLOW);
                    break;
                case RED:
                    break;
            }
        }
        drawPath();

        // Draw the normaly realized paths
        if (phase != null && phase.activePhase) {
            noStroke();
            fill(255, 30);
            drawPath();
        }

        drawSensors();

        // fill(255, 0, 0, 50);
        // noStroke();
        // for (PVector node : pathIntro) {
        // circle(node, 15);
        // }
        // fill(0, 255, 0, 50);
        // for (int i = 0; i < pathIntersection.size(); i++) {
        // PVector node = pathIntersection.get(i);
        // fill(color.fromHSB((float) i / pathIntersection.size(), 0.6, 0.6));
        // circle(node, 10);
        // }
        // fill(0, 0, 255, 50);
        // for (PVector node : pathExit) {
        // circle(node, 7);
        // }
    }

    public void drawPath() {
        beginShape();

        // There
        for (int i = 0; i < path.size() - 1; i++) {
            PVector node = path.get(i);
            PVector next = path.get(i + 1);

            PVector offset = PVector.sub(next, node).normalize().rotate(-PI / 2).mult(laneWidth / 2);
            vertex(PVector.add(node, offset));

            // Finish out this line by adding the same offset to the final node. This is
            // okay because the exit paths are always straight
            if (i == path.size() - 2) {
                vertex(PVector.add(next, offset));
            }
        }
        // Back
        for (int i = path.size() - 1; i > 0; i--) {
            PVector node = path.get(i);
            PVector next = path.get(i - 1);

            PVector offset = PVector.sub(next, node).normalize().rotate(-PI / 2).mult(laneWidth / 2);
            vertex(PVector.add(node, offset));

            // Finish out this line by adding the same offset to the final node. This is
            // okay because the exit paths are always straight
            if (i == 1) {
                vertex(PVector.add(next, offset));
            }
        }
        endShape();
    }

    public void drawTrafficLight() {
        // float dir = PVector.angleBetween(PVector.sub(pathIntro.get(1),
        // pathIntro.get(0)), new PVector(0, 1));
        PVector diff = PVector.sub(pathIntro.get(1), pathIntro.get(0));
        float dir = 0;
        // From:
        if (diff.x > 0) // right
            dir = PI / 2;
        else if (diff.x < 0) // left
            dir = -PI / 2;
        else if (diff.y > 0) // top
            dir = PI;
        else if (diff.y < 0) // bottom
            dir = 0;

        float signalRadius = laneWidth / 2.2f;

        push();
        translate(pathIntersection.get(0));
        rotate(dir);
        stroke(200);
        strokeWeight(2);
        fill(30);
        rectMode(CENTER);
        rect(PVector.zero(), signalRadius * 2, signalRadius * 4, signalRadius * 2);
        noStroke();

        drawTrafficLightSymbol(new PVector(0, -signalRadius * 1.1),
                color(Settings.RED, signal == Signal.RED ? 255 : 40), signalRadius);
        drawTrafficLightSymbol(new PVector(0, signalRadius * 1.1),
                color(Settings.GREEN, signal == Signal.GREEN ? 255 : 40), signalRadius);
        drawTrafficLightSymbol(PVector.zero(), color(Settings.YELLOW, signal == Signal.YELLOW ? 255 : 40),
                signalRadius);

        // Draw estimated time until green
        float maxTimeUntilGreen = IntersectionManager.phases.get(0).maximumTypicalGreenTime * 3.5f + yellowTime
                + exitTime;
        noStroke();
        rectMode(CORNER);

        float w = signalRadius * 0.3f;
        float h = signalRadius * 2;
        float startY = -h;

        fill(0);
        rect(-signalRadius - w, startY, w, signalRadius * 4);

        if (timeUntilGreenCounter != -1) {
            fill(224, 199, 72);
            int n = 30;
            for (int i = 0; i <= n; i++) {
                float y = map(i, n, 0, startY + w / 2, startY + signalRadius * 4 - w / 2);
                if ((float) i / n > (float) timeUntilGreenCounter / maxTimeUntilGreen)
                    break;
                circle(-signalRadius - w / 2, y, w / 2);
            }
        }

        pop();
    }

    private void drawTrafficLightSymbol(PVector pos, color col, float signalRadius) {
        if (type == MovementType.CAR_RIGHT) {
            strokeWeight(2);
            stroke(col);
            line(pos.x - signalRadius * 0.3, pos.y + 0, pos.x + signalRadius * 0.3, pos.y + 0);
            line(pos.x + 0, pos.y - signalRadius * 0.3, pos.x + signalRadius * 0.3, pos.y + 0);
            line(pos.x + 0, pos.y + signalRadius * 0.3, pos.x + signalRadius * 0.3, pos.y + 0);
        } else if (type == MovementType.CAR_LEFT) {
            strokeWeight(2);
            stroke(col);
            line(pos.x + signalRadius * 0.3, pos.y + 0, pos.x - signalRadius * 0.3, pos.y + 0);
            line(pos.x + 0, pos.y - signalRadius * 0.3, pos.x - signalRadius * 0.3, pos.y + 0);
            line(pos.x + 0, pos.y + signalRadius * 0.3, pos.x - signalRadius * 0.3, pos.y + 0);
        } else {
            noStroke();
            fill(col);
            circle(pos, signalRadius);
        }
    }

    public int estimatedTimeUntilGreen() {
        if (signal != Signal.RED || !waitingTraffic())
            return -1;

        if (changingGreen) {
            for (Integer time : signalTimeline.keySet()) {
                if (signalTimeline.get(time) == Signal.GREEN)
                    return time - frameCount;
            }
        }

        // Otherwise, we just wait figure out the max waiting time until our phase
        int index = IntersectionManager.currentPhaseIndex;
        int waitedPhases = 0;
        while (true) {
            index = (index + 1) % IntersectionManager.phases.size();
            if (IntersectionManager.phases.get(index).movements.contains(this))
                break;

            waitedPhases++;
        }
        // Return: time left of current phase + waitedPhases * max time of phase
        Phase currentPhase = IntersectionManager.phases.get(IntersectionManager.currentPhaseIndex);
        int currentPhaseTime = max(currentPhase.maximumTypicalGreenTime - (frameCount - currentPhase.phaseStartTime),
                0);
        int waitedPhasesTime = waitedPhases * currentPhase.maximumTypicalGreenTime;
        // Let's also add on a yellowTime + exitTime because we have calculated up until
        // the conflicting movement will turn yellow, but we probably won't go
        // immediately so this is an estimate
        // Note: the exitTime should be that of the conflicting movement, not us, but we
        // don't know that so this is approximate
        return currentPhaseTime + waitedPhasesTime + yellowTime + exitTime;
    }

    public void drawSensors() {
        noFill();
        stroke(5, 100);
        strokeWeight(2);
        rectMode(CENTER);
        rect(pathIntro.get(sensorIndex), laneWidth * 0.8, laneWidth * 0.5);
    }

}
