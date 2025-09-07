import library.core.*;
import java.util.*;

class Phase extends PComponent implements EventIgnorer {

    ArrayList<Movement> movements;
    int defaultTime;
    int maximumTypicalGreenTime;
    boolean activePhase = false;
    int phaseStartTime = -1;

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
        for (Movement movement : movements) {
            movement.begin(this, outboundPhase);
        }
        activePhase = true;
        phaseStartTime = frameCount;
    }

    public void end(boolean callEnd) {
        for (Movement movement : movements) {
            if (callEnd)
                movement.end();
            movement.phase = null;
        }
        activePhase = false;
    }

    public void update() {
        for (Movement movement : movements) {
            movement.update();
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
        }

        if (activePhase && frameCount >= phaseStartTime + maximumTypicalGreenTime) {
            end(true);
        }
    }

}
