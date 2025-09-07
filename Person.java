import library.core.*;
import java.util.*;

class Person extends PComponent implements EventIgnorer {

    Movement movement;
    int currentIndex;
    PVector pos;
    float speed;
    float maxSpeed;
    float acceleration;
    boolean move;

    public Person(Movement movement, float maxSpeed, float acceleration) {
        this.movement = movement;
        movement.addTraffic(this);
        speed = 0;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;

        currentIndex = 0;
        pos = movement.pathIntro.get(0).copy();

        move = false;
    }

    public boolean update() {
        if (!move) {
            if (movement.canProceed(this)) {
                move = true;
                currentIndex++;
                if (currentIndex >= movement.path.size() - 1) {
                    return true;
                }
            }
        }

        if (!move) {
            speed = 0;
            return false;
        }

        speed = min(speed + acceleration, maxSpeed);
        PVector dir = PVector.sub(movement.path.get(currentIndex + 1), pos);
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
        circle(pos, movement.laneWidth * 0.5);
    }

}
