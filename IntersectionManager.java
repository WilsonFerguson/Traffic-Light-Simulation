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
        int next = (currentPhaseIndex + 1) % phases.size();

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

        currentPhaseIndex = next;
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
