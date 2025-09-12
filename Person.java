import library.core.*;
import java.util.*;

class Person extends PComponent implements EventIgnorer {

    Movement movement;
    int currentIndex;
    PVector pos;
    PVector dir;
    float speed;
    float maxSpeed;
    float acceleration;
    boolean move;

    boolean start = true;
    int movingTimer = -1;
    int movingDelay;

    int timeWaiting = 0;

    public Person(Movement movement, float maxSpeed, float acceleration) {
        this.movement = movement;
        movement.addTraffic(this);
        speed = 0;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;

        currentIndex = 0;
        pos = movement.pathIntro.get(0).copy();

        move = false;

        movingDelay = ceil(0.35 * 60);
        if (movement.type == MovementType.BIKE_TEGELIJK || movement.type == MovementType.BIKE_STRAIGHT) {
            movingDelay = ceil(0.1 * 60);
        }
    }

    public boolean update() {
        if (!move) {
            if (movement.canProceed(this)) {
                move = true;
                currentIndex++;
                if (currentIndex >= movement.path.size() - 1) {
                    return true;
                }

                if (!start) {
                    if (speed == 0) {
                        movingTimer = frameCount + movingDelay;
                        currentIndex--;
                    }
                } else {
                    start = false;
                }
            }
        }

        if (!move) {
            speed = 0;
            timeWaiting++;
            return false;
        }

        if (movingTimer != -1 && frameCount >= movingTimer) {
            currentIndex++;
            movingTimer = -1;
        } else if (movingTimer != -1 && frameCount < movingTimer) {
            return false;
        }

        speed = min(speed + acceleration, maxSpeed);
        dir = PVector.sub(movement.path.get(currentIndex + 1), pos);
        if (dir.mag() > speed) {
            dir.setMag(speed);
        } else {
            // After this move, we will arrive at the node
            move = false;
        }
        pos.add(dir);

        return false;
    }

    public void draw() {
        noStroke();
        fill(230, 150);
        // circle(pos, movement.laneWidth * 0.5);
        // Only null if you first spawn in and can't move immediately so it's safe to
        // add 1
        if (dir == null) {
            dir = PVector.sub(movement.path.get(currentIndex + 1), pos).normalize();
        }
        push();
        translate(pos);
        rotate(dir.heading());
        rect(0, 0, movement.laneWidth / 1.4, movement.laneWidth / 3);
        pop();
    }

}
