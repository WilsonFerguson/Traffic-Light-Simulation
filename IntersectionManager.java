import library.core.*;
import java.util.*;

class IntersectionManager extends PComponent implements EventIgnorer {

    public static ArrayList<Movement> movements;
    public static ArrayList<Phase> phases = new ArrayList<Phase>();
    public static int currentPhaseIndex = 0;
    public static boolean started = false;

    public static void addPhases(Phase... phasesAdd) {
        for (Phase phase : phasesAdd) {
            phases.add(phase);
        }
    }

    public static void start() {
        phases.get(currentPhaseIndex).begin(null);
        started = true;
    }

    public static void nextPhase() {
        int next = getProbabilisticNextPhase();

        // End all spliced in movements
        for (Movement movement : movements) {
            // If the movement is in the new phase, then it's fine
            if (phases.get(next).movements.contains(movement))
                continue;

            if ((movement.phase == null || !movement.phase.activePhase) && !movement.changingRed
                    && movement.signal == Signal.GREEN) {
                movement.end();
            }
        }
        phases.get(currentPhaseIndex).end(true);
        phases.get(next).begin(phases.get(currentPhaseIndex));

        Sketch.phaseModifications = new boolean[3];

        // If the new phase doesn't have a special person, then if another phase has a
        // special vehicle waiting: then we will shorten this phase to the minimum
        // green.
        // Note: if a special vehicle comes within this short window it will indeed
        // extend the phase
        if (!phases.get(next).hasSpecialPerson()) {
            for (Phase phase : phases) {
                if (phase.hasSpecialPerson()) {
                    shortenPhase(phases.get(next));
                    break;
                }
            }
        }

        currentPhaseIndex = next;
    }

    public static int getProbabilisticNextPhase() {
        // ------Go to the next phase in order, skip empty ones:-------
        int next = (currentPhaseIndex + 1) % phases.size();

        // If all of the movements in the new phase have no traffic, go to the next one.
        // If this is true for all phases, then just stay where we are.
        while (true) {
            // The next phase has traffic, so return it
            for (Movement movement : phases.get(next).movements) {
                if (movement.waitingTraffic()) {
                    return next;
                }
            }

            // Advance the phase
            next = (next + 1) % phases.size();
            if (next == currentPhaseIndex) {
                // next = (next + 1) % phases.size();
                break;
            }
        }

        return next;

        // ------Go to phase with highest weight (exclude the current phase)-------
    }

    public static void requestShortenedPhase() {
        if (phases.get(currentPhaseIndex).hasSpecialPerson())
            return;

        shortenPhase(phases.get(currentPhaseIndex));
    }

    private static void shortenPhase(Phase phase) {
        phase.setRealizedGreenTime(Sketch.greenTime);
        Sketch.phaseModifications[0] = true;
        for (Phase p : phases) {
            for (Movement movement : p.movements) {
                movement.notifyShortenedPhase();
            }
        }
    }

    public static void reportExtededPhase() {
        Sketch.phaseModifications[1] = true;
    }

    public static void update() {
        if (!started)
            return;

        for (Phase phase : phases) {
            phase.update();
        }

        // There was no traffic so every light of the active phase is turning red
        if (!phases.get(currentPhaseIndex).activePhase) {
            nextPhase();
        }
    }

}
