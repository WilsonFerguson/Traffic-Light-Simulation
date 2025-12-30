import library.core.*;
import java.util.*;

class Phase extends PComponent implements EventIgnorer {

    ArrayList<Movement> movements;
    int defaultTime;
    int maximumTypicalGreenTime;
    int maximumRealizedGreenTime;
    int minimumRealizedGreenTime;
    HashSet<Person> specialPersonsExtended = new HashSet<Person>();
    boolean activePhase = false;
    int phaseStartTime = -1;

    int weight = 0;

    public Phase(int maximumTypicalGreenTime, ArrayList<Movement> allMovements, int... indices) {
        this.maximumTypicalGreenTime = maximumTypicalGreenTime;
        movements = new ArrayList<Movement>();

        for (int index : indices) {
            movements.add(allMovements.get(index));
        }
    }

    public void addMovement(Movement movement) {
        movements.add(movement);
        defaultTime = max(defaultTime, movement.getTime(maximumTypicalGreenTime));
    }

    public void begin(Phase outboundPhase) {
        // The minimum time for this phase is the one for which all phases can just
        // barely get a green.
        minimumRealizedGreenTime = frameCount;
        for (Movement movement : movements) {
            minimumRealizedGreenTime = max(minimumRealizedGreenTime, movement.begin(this, outboundPhase));
        }
        minimumRealizedGreenTime += -frameCount + 1 + Sketch.greenTime;
        setRealizedGreenTime(maximumTypicalGreenTime);
        specialPersonsExtended.clear();
        activePhase = true;
        phaseStartTime = frameCount;

        weight = 0;
    }

    public void end(boolean callEnd) {
        for (Movement movement : movements) {
            if (callEnd)
                movement.end();
            movement.phase = null;
        }
        activePhase = false;

        weight = 0;
    }

    public void setRealizedGreenTime(int realizedGreenTime) {
        maximumRealizedGreenTime = realizedGreenTime;
        // Ensure that the realized time is at least the minimum time
        maximumRealizedGreenTime = max(maximumRealizedGreenTime, minimumRealizedGreenTime);
    }

    public void update() {
        for (Movement movement : movements) {
            movement.update();

            if (movement.hasSpecialPerson()) {
                // // Don't calculate the extension multiple times for the same person
                // if (specialPersonsExtended.contains(movement.specialPerson))
                // continue;

                int neededMaxGreenTime = movement.getSpecialPersonToIntersectionTime() + (frameCount - phaseStartTime);
                boolean canExtend = neededMaxGreenTime - maximumTypicalGreenTime < Sketch.phaseExtensionAllowance;
                if (canExtend && neededMaxGreenTime > maximumRealizedGreenTime) {
                    setRealizedGreenTime(neededMaxGreenTime);
                    IntersectionManager.reportExtededPhase();
                }

                // Even if it we couldn't extend the phase, we have still accounted for this
                // person
                specialPersonsExtended.add(movement.specialPerson);
            }
        }

        // If everyone is changing red, then we can stop this phase
        if (activePhase) {
            boolean everyoneChangingRed = true;
            for (Movement movement : movements) {
                if (!movement.changingRed && movement.signal != Signal.RED || movement.changingGreen) {
                    everyoneChangingRed = false;
                    break;
                }
            }
            if (everyoneChangingRed) {
                end(false);
            }
        } else {
            // Weight calculation
            weight = 0;
            for (Movement movement : movements) {
                for (Person person : movement.getWaitingTraffic()) {
                    if (person.special)
                        weight += person.age * Sketch.weightPersonSpecial;
                    else
                        weight += person.age * Sketch.weightPerson;
                }
            }
        }

        if (activePhase && frameCount >= phaseStartTime + maximumRealizedGreenTime) {
            end(true);
        }
    }

    public boolean hasSpecialPerson() {
        for (Movement movement : movements) {
            if (movement.hasSpecialPerson())
                return true;
        }

        return false;
    }
}
