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
    float angle;
    boolean move;

    /*
     * Public transport vehicles, really
     */
    boolean special = false;

    boolean start = true;
    int movingTimer = -1;
    int movingDelay;

    int timeWaiting = 0;
    int age = 0;

    public Person(Movement movement, float maxSpeed, float acceleration, boolean special) {
        this.movement = movement;
        movement.addTraffic(this);
        speed = 0;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;

        this.special = special;

        currentIndex = 0;
        pos = movement.pathIntro.get(0).copy();

        move = false;

        movingDelay = ceil(0.35 * 60);
        if (movement.type == MovementType.BIKE_TEGELIJK || movement.type == MovementType.BIKE_STRAIGHT) {
            movingDelay = ceil(0.1 * 60);
        }
    }

    public boolean update() {
        age++;

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

        // dirTemp is used for velocity as well as smoothly setting the drawing "dir"
        PVector dirTemp = PVector.sub(movement.path.get(currentIndex + 1), pos);
        if (dir == null)
            dir = dirTemp;
        else
            dir.lerp(dirTemp, 0.1f);

        if (dirTemp.mag() > speed) {
            dirTemp.setMag(speed);
        } else {
            // After this move, we will arrive at the node
            move = false;
        }
        pos.add(dirTemp);

        return false;
    }

    public void draw() {
        noStroke();
        if (!special)
            fill(230, 150);
        else
            fill(235, 64, 52, 150);
        // Only null if you first spawn in and can't move immediately so it's safe to
        // add 1
        if (dir == null) {
            dir = PVector.sub(movement.path.get(currentIndex + 1), pos).normalize();
        }
        push();
        translate(pos);
        rotate(dir.heading());
        float w = movement.laneWidth / 1.4f;
        float h = movement.laneWidth / 3;
        rect(-w, 0, w, h);
        pop();
    }

}
