import library.core.*;
import java.util.*;

class Movement extends PComponent implements EventIgnorer {

    Road road;
    ArrayList<Movement> movements;
    MovementType type;
    int id;
    Origin origin;
    Direction direction;

    int greenMinimumTime;
    int yellowTime;
    int redWaitTime;
    int exitTime;

    Signal newSignal;
    int newSignalChangeTime;

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

    /**
     * The special person closest to but not past the intersection. Can be null
     */
    Person specialPerson = null;

    Signal signal;
    color pathColor;
    color drawColor;
    color prevTargetColor = null;
    float pathHighlightAlpha = 0;
    boolean highlight = false;
    color highlightColor = Settings.PATH_HIGHLIGHT.copy().setAlpha(0);

    Phase phase = null;
    int greenStartTime = -1;
    int yellowStartTime = -1;
    int redStartTime = -1000;
    boolean wantsGreen = false;

    public Movement(Road road, ArrayList<Movement> movements, MovementType type, Origin origin, Direction direction,
            int greenTime, int yellowTime, int redWaitTime,
            float speed,
            int laneWidth, float acceleration,
            color pathColor) {
        this.road = road;
        this.movements = movements;
        this.type = type;
        this.origin = origin;
        this.direction = direction;

        greenMinimumTime = greenTime;
        this.yellowTime = yellowTime;
        this.redWaitTime = redWaitTime;

        newSignal = null;
        newSignalChangeTime = -1;

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
        drawColor = pathColor;
    }

    public void setId(int id) {
        this.id = id;
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
        if (type == MovementType.PEDESTRIAN) {
            // Not the very last one because you reach out for the button and to make the
            // second stage work better
            sensorIndex = pathIntro.size() - 3;
        } else {
            if (pathIntersection.size() == 0) {
                sensorIndex = pathIntro.size() - 1;
                float dist = 0;
                while ((dist / speed) < greenMinimumTime) {
                    dist += pathIntro.get(sensorIndex).dist(pathIntro.get(sensorIndex - 1));
                    sensorIndex--;
                    if (sensorIndex == 0) {
                        println("WARNING: It takes longer than the green time to get to the intersection! Using the first node by default.");
                        break;
                    }
                }
                sensorIndex += 1;
            }
        }

        // Add the node
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
        return greenMinimumTime + yellowTime + exitTime;
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
        if (person.currentIndex != pathIntro.size() - 1) {
            return true;
        } else {
            return signal != Signal.RED;
        }
    }

    public ArrayList<Movement> getConflictingMovements() {
        ArrayList<Movement> conflictingMovements = new ArrayList<>();
        for (Movement movement : movements) {
            if (clearanceTimes.containsKey(movement))
                conflictingMovements.add(movement);
        }
        return conflictingMovements;
    }

    /**
     * Returns list of conflicting movements that are either green, yellow, or red
     * but aren't finished with the clearance time. Includes the frame when this
     * conflict will be completely resolved.
     */
    public ArrayList<Conflict> getRelevantConflictingMovements() {
        ArrayList<Conflict> relevantConflictingMovements = new ArrayList<>();
        for (Movement movement : movements) {
            if (!clearanceTimes.containsKey(movement))
                continue;

            if (movement.signal == Signal.RED && frameCount > movement.redStartTime + clearanceTimes.get(movement))
                continue;

            int resolvedFrame = frameCount;
            int realizedYellowTime = max(movement.yellowTime + clearanceTimes.get(movement), 0);
            switch (movement.signal) {
                case GREEN:
                    if (frameCount < movement.greenStartTime + movement.greenMinimumTime)
                        resolvedFrame = movement.greenStartTime + movement.greenMinimumTime + realizedYellowTime;
                    else
                        resolvedFrame = frameCount + realizedYellowTime;
                    break;
                case YELLOW:
                    resolvedFrame = movement.yellowStartTime + realizedYellowTime;
                    break;
                case RED:
                    resolvedFrame = movement.redStartTime + clearanceTimes.get(movement);
                    break;
            }

            relevantConflictingMovements
                    .add(new Conflict(movement, resolvedFrame));
        }
        return relevantConflictingMovements;
    }

    public int getEarliestGreenTime() {
        int time = frameCount;
        for (Conflict conflict : getRelevantConflictingMovements()) {
            time = max(time, conflict.resolvedFrame);
        }

        int personalTime = frameCount;
        switch (signal) {
            case GREEN:
                break;
            case YELLOW:
                personalTime = yellowStartTime + yellowTime + redWaitTime;
                break;
            case RED:
                personalTime = redStartTime + redWaitTime;
                break;
        }

        return max(time, personalTime);
    }

    public void queueGreen() {
        newSignal = Signal.GREEN;
        newSignalChangeTime = getEarliestGreenTime();
    }

    public void queueYellow() {
        newSignal = Signal.YELLOW;
        newSignalChangeTime = max(greenStartTime + greenMinimumTime, frameCount);
    }

    public void update() {
        updateGreenCounter();
        updateSpecialPersonTracker();

        checkForYellowChange();
        changeSignal();
        setWantsGreen();

        // // Shortening the current phase
        // if (specialPerson != null) {
        // // If we aren't the active phase, shorten it.
        // // NOTE: Yes we might be green right now and would want to extend it but we
        // // don't have power over extending the phase when we aren't an active
        // movement
        // // so the best we can do is just get to an active phase as soon as possible
        // if (phase == null)
        // IntersectionManager.requestShortenedPhase();
        // }
    }

    private void updateGreenCounter() {
        int timeUntilGreen = estimatedTimeUntilGreen();
        if (timeUntilGreen == -1 || timeUntilGreenCounter == -1) {
            timeUntilGreenCounter = timeUntilGreen;
            timeUntilGreenCounterMax = timeUntilGreen;
        } else {
            // If there's been a big change in the time until green then:
            // Change the max in such a way that the percentage stays the same but now the
            // max is much lower
            if (timeUntilGreen != 0 && abs(timeUntilGreen - timeUntilGreenCounter) / timeUntilGreen > 0.1) {
                float multiplier = ((float) timeUntilGreenCounterMax) / timeUntilGreenCounter;
                timeUntilGreenCounterMax = ceil(((float) timeUntilGreen) * multiplier);
                timeUntilGreenCounter = timeUntilGreen;
            } else {
                if (timeUntilGreen > timeUntilGreenCounter)
                    timeUntilGreenCounter = min(timeUntilGreenCounter + 6, timeUntilGreen);
                else if (timeUntilGreen < timeUntilGreenCounter)
                    timeUntilGreenCounter = max(timeUntilGreenCounter - 6, timeUntilGreen);
            }
        }
    }

    private void updateSpecialPersonTracker() {
        // If the special person exists, check if they are past the intersection. If so
        // update the index
        if (specialPerson != null) {
            if (specialPerson.currentIndex > pathIntro.size()) {
                specialPerson = null;
            }
        }
        if (specialPerson == null) {
            // Start looking at i = 0 which is the farthest person along
            for (int i = 0; i < traffic.size(); i++) {
                Person person = traffic.get(i);
                if (!person.special)
                    continue;

                if (person.currentIndex <= pathIntro.size()) {
                    specialPerson = person;
                    break;
                }
            }
        }
    }

    /**
     * Checks to see if we should change to yellow
     */
    private void checkForYellowChange() {
        if (signal != Signal.GREEN || waitingTraffic())
            return;

        if (frameCount < greenStartTime + greenMinimumTime)
            return;

        newSignal = Signal.YELLOW;
        newSignalChangeTime = frameCount;
    }

    private void changeSignal() {
        if (newSignal != null) {
            if (frameCount >= newSignalChangeTime) {
                signal = newSignal;
                newSignal = null;
                newSignalChangeTime = -1;

                switch (signal) {
                    case GREEN:
                        redStartTime = -1;
                        greenStartTime = frameCount;

                        wantsGreen = false;
                        break;
                    case YELLOW:
                        greenStartTime = -1;
                        yellowStartTime = frameCount;

                        newSignal = Signal.RED;
                        newSignalChangeTime = frameCount + yellowTime;
                        break;
                    case RED:
                        yellowStartTime = -1;
                        redStartTime = frameCount;
                        break;
                }
            }
        }
    }

    private void setWantsGreen() {
        if (signal != Signal.RED || newSignal == Signal.GREEN) {
            wantsGreen = false;
            return;
        }
        wantsGreen = waitingTraffic();
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
            if (index > pathIntro.size())
                continue;

            // Make it so that special vehicles are counted regardless of if they have hit
            // the sensor (so they use GPS)
            if (person.special)
                return true;

            if (index < sensorIndex)
                continue;
            return true;
        }

        return false;
    }

    public ArrayList<Person> getWaitingTraffic() {
        ArrayList<Person> waitingTraffic = new ArrayList<Person>();
        for (Person person : traffic) {
            if (person.currentIndex > pathIntro.size())
                continue;

            if (person.special) {
                waitingTraffic.add(person);
                continue;
            }

            if (person.currentIndex < sensorIndex)
                continue;
            waitingTraffic.add(person);
        }
        return waitingTraffic;
    }

    int waitingTrafficCount() {
        return getWaitingTraffic().size();
    }

    public boolean hasSpecialPerson() {
        return specialPerson != null;
    }

    public int getSpecialPersonToIntersectionTime() {
        if (!hasSpecialPerson())
            return -1;

        int indexDelta = pathIntro.size() - specialPerson.currentIndex;
        // We can use the distance between two nodes and this distance will be the same
        // for all intro nodes
        float distance = pathIntro.get(0).dist(pathIntro.get(1));

        return ceil((distance * indexDelta) / speed);
    }

    // public void notifyShortenedPhase() {
    // // This function only applies to red signals that are changing green
    // if (!changingGreen || signal != Signal.RED)
    // return;
    //
    // // If we are in the next phase we don't care about the shortening
    // boolean inNextPhase = IntersectionManager.phases
    // .get(IntersectionManager.getProbabilisticNextPhase()).movements
    // .contains(this);
    // if (inNextPhase)
    // return;
    //
    // int changeTime = 0;
    // for (Integer time : signalTimeline.keySet()) {
    // if (signalTimeline.get(time) == Signal.GREEN) {
    // changeTime = time;
    // break;
    // }
    // }
    // Phase phase =
    // IntersectionManager.phases.get(IntersectionManager.currentPhaseIndex);
    // boolean enoughTime = changeTime + greenMinimumTime < phase.phaseStartTime +
    // phase.maximumRealizedGreenTime;
    //
    // if (!enoughTime) {
    // signalTimeline.clear();
    // changingGreen = false;
    // }
    // }

    public void draw() {
        noStroke();

        color targetColor = null;
        if (Settings.HIGHLIGHT_SIGNALS) {
            switch (signal) {
                case GREEN:
                    targetColor = Settings.GREEN;
                    break;
                case YELLOW:
                    targetColor = Settings.YELLOW;
                    break;
                case RED:
                    targetColor = pathColor;
                    break;
            }
        }
        if (targetColor == prevTargetColor)
            drawColor = lerpColor(drawColor, targetColor, 0.25);

        if (type == MovementType.PEDESTRIAN)
            fill(pathColor);
        else
            fill(drawColor);
        prevTargetColor = targetColor;
        if (type == MovementType.PEDESTRIAN) {
            drawPath(pathIntro);
            drawPath(pathExit);
        } else {
            drawPath(path);
        }

        if (type == MovementType.PEDESTRIAN)
            drawZebraCrossing();

        // Draw the normaly realized paths
        if (phase != null && phase.activePhase)
            pathHighlightAlpha = min(pathHighlightAlpha + 4, 34);
        else
            pathHighlightAlpha = max(pathHighlightAlpha - 4, 0);
        // By making it > 4 and not > 0 it requires 2 frames in a row to be active phase
        // so you don't get flickering at the start. We then - 4 to make the alpha start
        // at 0 when drawing
        if (pathHighlightAlpha > 4) {
            noStroke();
            fill(255, pathHighlightAlpha - 4);
            if (type == MovementType.PEDESTRIAN) {
                drawPath(pathIntro);
                drawPath(pathExit);
            } else {
                drawPath(path);
            }
        }

        drawSensor();

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

    public void drawHighlight() {
        if (highlight) {
            highlightColor = lerpColor(highlightColor, Settings.PATH_HIGHLIGHT, 0.25);
            if (highlightColor.a >= 253)
                highlightColor.a = 255;
        } else if (!highlight && highlightColor.a > 0) {
            highlightColor = lerpColor(highlightColor, Settings.PATH_HIGHLIGHT.copy().setAlpha(0), 0.25);
            if (highlightColor.a <= 2)
                highlightColor.a = 0;
        }

        if (highlightColor.a == 0)
            return;

        stroke(highlightColor);
        strokeWeight(4);
        noFill();
        drawPath(path);
        noStroke();
    }

    public void drawPath(ArrayList<PVector> pathToTrace) {
        beginShape();

        // There
        for (int i = 0; i < pathToTrace.size() - 1; i++) {
            PVector node = pathToTrace.get(i);
            PVector next = pathToTrace.get(i + 1);

            PVector offset = PVector.sub(next, node).normalize().rotate(-PI / 2).mult(laneWidth / 2);
            vertex(PVector.add(node, offset));

            // Finish out this line by adding the same offset to the final node. This is
            // okay because the exit paths are always straight
            if (i == pathToTrace.size() - 2) {
                vertex(PVector.add(next, offset));
            }
        }
        // Back
        for (int i = pathToTrace.size() - 1; i > 0; i--) {
            PVector node = pathToTrace.get(i);
            PVector next = pathToTrace.get(i - 1);

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

    private void drawZebraCrossing() {
        if (signal != Signal.RED) {
            fill(drawColor);
            drawPath(pathIntersection);
        }

        fill(255, 100);
        float whiteWidth = Sketch.laneWidthCar / 3; // 2 white areas per car lane
        float blackWidth = Sketch.laneWidthCar / 9; // We have 3 black areas to fill a space of width / 3
        if (getRotation() == 0) {
            float x = pathIntro.get(0).x;
            float y = pathIntersection.get(0).y;

            y -= whiteWidth + blackWidth;
            y += whiteWidth / 2;
            while (y > pathExit.get(0).y) {
                rect(x, y, laneWidth, whiteWidth);

                y -= whiteWidth + blackWidth;
            }
        } else if (getRotation() == PI) {
            float x = pathIntro.get(0).x;
            float y = pathIntersection.get(0).y;

            y += whiteWidth + blackWidth;
            y -= whiteWidth / 2;
            while (y < pathExit.get(0).y) {
                rect(x, y, laneWidth, whiteWidth);

                y += whiteWidth + blackWidth;
            }
        } else if (getRotation() == PI / 2) {
            float x = pathIntersection.get(0).x;
            float y = pathIntro.get(0).y;

            x += whiteWidth + blackWidth;
            x -= whiteWidth / 2;
            while (x < pathExit.get(0).x) {
                rect(x, y, whiteWidth, laneWidth);

                x += whiteWidth + blackWidth;
            }
        } else if (getRotation() == -PI / 2) {
            float x = pathIntersection.get(0).x;
            float y = pathIntro.get(0).y;

            x -= whiteWidth + blackWidth;
            x += whiteWidth / 2;
            while (x > pathExit.get(0).x) {
                rect(x, y, whiteWidth, laneWidth);

                x -= whiteWidth + blackWidth;
            }
        }
    }

    private float getRotation() {
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

        return dir;
    }

    public void drawTrafficLightAndStoplineAndArrows() {
        float signalRadius = laneWidth / 2.2f;
        float w = signalRadius * 0.3f;
        float h = signalRadius * 2;
        float startY = -h;
        float dir = getRotation();

        push();
        PVector translation = pathIntersection.get(0).copy();
        translation.add(PVector.fromAngle(dir).rotate(PI / 2).mult(h * 1.3));
        translate(translation);
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
        noStroke();
        rectMode(CORNER);

        fill(0);
        rect(-signalRadius - w, startY, w, signalRadius * 4);

        if (timeUntilGreenCounter != -1) {
            fill(224, 199, 72);
            int n = 30;
            for (int i = 0; i <= n; i++) {
                float y = map(i, n, 0, startY + w / 2, startY + signalRadius * 4 - w / 2);
                if ((float) i / n > (float) timeUntilGreenCounter / timeUntilGreenCounterMax)
                    break;
                circle(-signalRadius - w / 2, y, w / 2);
            }
        }

        // Stopline
        if (type == MovementType.PEDESTRIAN) {
            pop();
            return;
        }
        noStroke();
        fill(220, 150);
        rectMode(CENTER);
        PVector offset = PVector.sub(pathIntro.get(1), pathIntro.get(0)).normalize().rotate(-PI / 2)
                .mult(laneWidth).abs();
        if (offset.x < 0.01)
            offset.x = 3;
        else
            offset.y = 3;

        translate(PVector.fromAngle(dir).rotate(PI / 2).mult(-h * 1.3));
        rotate(dir);
        rect(PVector.zero(), offset);

        pop();

        // Arrow
        if (!type.toString().contains("CAR"))
            return;

        push();
        translation = pathIntro.get(pathIntro.size() * 2 / 3).copy();
        translation.add(PVector.fromAngle(dir).rotate(PI / 2));
        translate(translation);
        rotate(dir);

        noStroke();
        fill(255, 100);
        switch (direction) {
            case STRAIGHT:
                float triH = laneWidth / 2;
                triangle(0, 0, -laneWidth / 6, triH, laneWidth / 6, triH);
                float rectH = laneWidth * 0.75f;
                rect(0, triH + rectH / 2, laneWidth / 8, rectH);
                break;
            case RIGHT:
                beginShape();
                float startW = -laneWidth / 2f;
                float startH = laneWidth / 2f;
                float xOffset = laneWidth / 2.8f;

                vertex(-(startW + xOffset), 0);
                vertex(-(startW * 1.3f + xOffset), startH / 2f);
                vertex(-(startW + xOffset), startH);
                vertex(-(startW + xOffset), startH * 0.75f);
                vertex(-(startW / 2f + xOffset), startH * 0.75f + (-startW / 2f));
                vertex(-(startW / 2f + xOffset), laneWidth / 2f + laneWidth * 0.75f);

                float finalX = startW / 2f + xOffset + laneWidth / 14f;
                vertex(-finalX, laneWidth / 2f + laneWidth * 0.75f);
                vertex(-finalX, startH * 0.25f + (-startW / 2f));

                vertex(-(startW + xOffset), startH * 0.25f);

                endShape(CLOSE);
                break;
            case LEFT:
                beginShape();
                startW = -laneWidth / 2f;
                startH = laneWidth / 2;
                xOffset = laneWidth / 2.8f;
                vertex(startW + xOffset, 0);
                vertex(startW * 1.3 + xOffset, startH / 2);
                vertex(startW + xOffset, startH);
                vertex(startW + xOffset, startH * 0.75);
                vertex(startW / 2 + xOffset, startH * 0.75 + (-startW / 2));
                vertex(startW / 2 + xOffset, laneWidth / 2 + laneWidth * 0.75);
                finalX = startW / 2 + xOffset + laneWidth / 14;
                vertex(finalX, laneWidth / 2 + laneWidth * 0.75);
                vertex(finalX, startH * 0.25 + (-startW / 2));
                vertex(startW + xOffset, startH * 0.25);
                endShape(CLOSE);
                break;
            default:
                break;
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

        if (newSignal == Signal.GREEN) {
            return newSignalChangeTime - frameCount;
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
        int currentPhaseTime = max(currentPhase.maximumRealizedGreenTime - (frameCount - currentPhase.phaseStartTime),
                0);
        int waitedPhasesTime = waitedPhases * currentPhase.maximumTypicalGreenTime;
        // Let's also add on a yellowTime + exitTime because we have calculated up until
        // the conflicting movement will turn yellow, but we probably won't go
        // immediately so this is an estimate
        // Note: the exitTime should be that of the conflicting movement, not us, but we
        // don't know that so this is approximate
        return currentPhaseTime + waitedPhasesTime + yellowTime + exitTime;
    }

    public void drawSensor() {
        if (type == MovementType.PEDESTRIAN)
            return;

        noFill();
        stroke(5, 100);
        strokeWeight(2);
        rectMode(CENTER);
        PVector dir = PVector.sub(pathIntro.get(1), pathIntro.get(0)).normalize();
        PVector dimensions = new PVector(dir.x == 0 ? laneWidth * 0.8f : laneWidth * 0.45f,
                dir.y == 0 ? laneWidth * 0.8f : laneWidth * 0.5f);
        rect(pathIntro.get(sensorIndex), dimensions);
    }

    public void printPath() {
        for (PVector node : pathIntersection) {
            print(node);
        }
        println();
    }

    public boolean hover() {
        switch (origin) {
            case NORTH:
                if (mouseY > pathIntersection.get(0).y)
                    return false;
                return mouseX > path.get(0).x - laneWidth / 2 && mouseX < path.get(0).x + laneWidth / 2;
            case SOUTH:
                if (mouseY < pathIntersection.get(0).y)
                    return false;
                return mouseX > path.get(0).x - laneWidth / 2 && mouseX < path.get(0).x + laneWidth / 2;
            case EAST:
                if (mouseX < pathIntersection.get(0).x)
                    return false;
                return mouseY > path.get(0).y - laneWidth / 2 && mouseY < path.get(0).y + laneWidth / 2;
            case WEST:
                if (mouseX > pathIntersection.get(0).x)
                    return false;
                return mouseY > path.get(0).y - laneWidth / 2 && mouseY < path.get(0).y + laneWidth / 2;
            default:
                return false;
        }
    }
}
