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

    public Phase(int maximumTypicalGreenTime, ArrayList<Movement> allMovements, int... indices) {
        this.minimumRealizedGreenTime = Sketch.greenTime;
        this.maximumTypicalGreenTime = maximumTypicalGreenTime;
        this.maximumRealizedGreenTime = maximumTypicalGreenTime;
        movements = new ArrayList<Movement>();

        for (int index : indices) {
            movements.add(allMovements.get(index));
        }
    }

    public Phase(int maximumTypicalGreenTime, ArrayList<Movement> allMovements, ArrayList<Movement> phase) {
        this.minimumRealizedGreenTime = Sketch.greenTime;
        this.maximumTypicalGreenTime = maximumTypicalGreenTime;
        this.maximumRealizedGreenTime = maximumTypicalGreenTime;
        movements = new ArrayList<Movement>();
        movements = new ArrayList<Movement>(phase);
    }

    public void begin() {
        activePhase = true;
        phaseStartTime = frameCount;
        minimumRealizedGreenTime = Sketch.greenTime;

        for (Movement movement : movements) {
            movement.phase = this;
            if (!movement.waitingTraffic() || movement.signal == Signal.GREEN)
                continue;

            movement.queueGreen();
        }
    }

    public void end() {
        activePhase = false;
        phaseStartTime = -1;

        for (Movement movement : movements) {
            movement.phase = null;
        }
    }

    // public void setRealizedGreenTime(int realizedGreenTime) {
    // maximumRealizedGreenTime = realizedGreenTime;
    // // Ensure that the realized time is at least the minimum time
    // maximumRealizedGreenTime = max(maximumRealizedGreenTime,
    // minimumRealizedGreenTime);
    // }

    public void update() {
        for (Movement movement : movements) {
            movement.update();

            // if (movement.hasSpecialPerson()) {
            // // // Don't calculate the extension multiple times for the same person
            // // if (specialPersonsExtended.contains(movement.specialPerson))
            // // continue;
            //
            // int neededMaxGreenTime = movement.getSpecialPersonToIntersectionTime() +
            // (frameCount - phaseStartTime);
            // boolean canExtend = neededMaxGreenTime - maximumTypicalGreenTime <
            // Sketch.phaseExtensionAllowance;
            // if (canExtend && neededMaxGreenTime > maximumRealizedGreenTime) {
            // setRealizedGreenTime(neededMaxGreenTime);
            // IntersectionManager.reportExtededPhase();
            // }
            //
            // // Even if it we couldn't extend the phase, we have still accounted for this
            // // person
            // specialPersonsExtended.add(movement.specialPerson);
            // }
        }

        if (activePhase) {
            updateActivePhase();
        } else {
            updateInactivePhase();
        }
    }

    public void updateActivePhase() {
        for (Movement movement : movements) {
            if (movement.wantsGreen) {
                int earliestGreenTime = movement.getEarliestGreenTime();
                if (earliestGreenTime + movement.greenMinimumTime < phaseStartTime + maximumRealizedGreenTime) {
                    for (Movement m : movement.getConflictingMovements()) {
                        if (m.signal == Signal.GREEN) {
                            m.queueYellow();
                        } else if (m.signal == Signal.RED && m.newSignal == Signal.GREEN) {
                            m.newSignal = null;
                            m.newSignalChangeTime = -1;
                        }
                    }
                    movement.queueGreen();
                }
            }
        }

        boolean allNonGreen = true;
        for (Movement m : movements) {
            if (m.signal == Signal.GREEN || m.newSignal == Signal.GREEN) {
                allNonGreen = false;
                break;
            }
        }
        if ((allNonGreen && frameCount >= phaseStartTime + minimumRealizedGreenTime)
                || frameCount >= phaseStartTime + maximumRealizedGreenTime) {
            end();
        }
    }

    public void updateInactivePhase() {
        for (Movement movement : movements) {
            if (!movement.wantsGreen)
                continue;

            boolean canTurnGreen = true;
            for (Movement m : movement.getConflictingMovements()) {
                if (m.signal == Signal.GREEN || m.newSignal == Signal.GREEN) {
                    canTurnGreen = false;
                    break;
                }
            }
            if (!canTurnGreen)
                continue;

            int earliestGreenTime = movement.getEarliestGreenTime();
            Phase currentPhase = IntersectionManager.phases.get(IntersectionManager.currentPhaseIndex);

            boolean canFitInCurrentPhase = earliestGreenTime + movement.greenMinimumTime < currentPhase.phaseStartTime
                    + currentPhase.maximumRealizedGreenTime;
            boolean inNextPhase = IntersectionManager.phases
                    .get(IntersectionManager.getProbabilisticNextPhase()).movements.contains(movement);
            boolean noConflictWithNextPhase = true;
            for (Movement m : movement.getConflictingMovements()) {
                if (!IntersectionManager.phases.get(IntersectionManager.getProbabilisticNextPhase()).movements
                        .contains(m))
                    continue;

                noConflictWithNextPhase = false;
                break;
            }
            canTurnGreen = canFitInCurrentPhase || inNextPhase || noConflictWithNextPhase;

            if (canTurnGreen) {
                movement.queueGreen();
                currentPhase.minimumRealizedGreenTime = max(currentPhase.minimumRealizedGreenTime,
                        earliestGreenTime + movement.greenMinimumTime);
            }
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
