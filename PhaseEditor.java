import library.core.*;
import GameEngine.*;
import java.util.*;

class PhaseEditor extends PComponent {
    Sketch sketch;

    Panel panel;
    ArrayList<ArrayList<Movement>> phases;

    ArrayList<Button> textButtons;
    ArrayList<Button> deleteButtons;
    Button addPhaseButton;
    Button uploadButton;

    int editing = -1;
    boolean active = false;

    // Stop lines
    float stopLineButtonRadius = 10;
    boolean draggingButton = false;

    public PhaseEditor(Sketch sketch) {
        this.sketch = sketch;

        this.phases = new ArrayList<>();
        panel = new Panel(0, 0, width / 4, height * 0.35, color(200, 98, 50), color(21, 28, 36));
        panel.setActive(false);
        panel.setAlpha(0);

        textButtons = new ArrayList<>();
        deleteButtons = new ArrayList<>();

        Button.setDefaults(new Button(0, 0, panel.size.x * 0.14, panel.size.x * 0.14, color(40, 40, 50),
                color(65, 65, 75), color(20, 20, 30), color(22, 22, 32), "", 25, color(255)).setCornerRadius(8));
    }

    public void addPhase(ArrayList<Movement> phase) {
        phases.add(phase);
        createButtons();
    }

    public void addPhase() {
        phases.add(new ArrayList<Movement>());
        createButtons();
        editPhase(phases.size() - 1);
    }

    public boolean isActive() {
        return active;
    }

    public void toggleActive() {
        active = !active;
        if (active) {
            panel.setActive(true);
            panel.setPos(panel.size.x * 0.55, panel.size.y * 0.55);
            createButtons();
            new Animator(panel, 0.2).setAlpha(0, 255).start();
        } else {
            new Animator(panel, 0.2).setAlpha(255, 0).start().onEnd(new Runnable() {
                @Override
                public void run() {
                    panel.setActive(false);
                }
            });
            for (Movement movement : sketch.movements) {
                movement.highlight = false;
            }
        }
    }

    private void createButtons() {
        if (panel.getElements().size() > 0) {
            for (UIElement element : panel.getElements()) {
                element.delete();
            }
            panel.clearElements();
            panel.setElementHeightCounter(-1);
            textButtons.clear();
            deleteButtons.clear();
        }

        // Title
        Text title = new Text(0, 0, "Phases", 40, color(255));
        panel.addElementFromTop(title);
        title.setPos(title.pos.x, title.pos.y + 5);

        float margin = panel.size.x * 0.05f;
        for (int i = 0; i < phases.size(); i++) {
            // Text button
            Button textButton = new Button(0, 0, "Phase " + (i + 1));
            push();
            textSize(textButton.textSize);
            float textWidth = textWidth(textButton.text);
            textButton.setSize(textWidth + 2 * 10, textButton.size.y);
            pop();

            panel.addElementFromTop(textButton, false);
            textButton.setPos(panel.pos.x - panel.size.x / 2 + textButton.size.x / 2 + margin, textButton.pos.y);

            // Delete button
            Button deleteButton = new Button(0, 0, "X");
            panel.addElementFromTop(deleteButton);
            deleteButton.setPos(textButton.pos.x + textButton.size.x / 2 + deleteButton.size.x / 2 + margin,
                    deleteButton.pos.y);
            panel.incrementElementHeight(10);

            textButtons.add(textButton);
            deleteButtons.add(deleteButton);

            textButton.onClick(new Runnable() {
                @Override
                public void run() {
                    editPhase(textButtons.indexOf(textButton));
                }
            });
            deleteButton.onClick(new Runnable() {
                @Override
                public void run() {
                    deletePhase(deleteButtons.indexOf(deleteButton));
                }
            });
        }

        // Add phase button
        addPhaseButton = new Button(-panel.size.x / 2 + Button.getDefaults().size.x / 2 + margin, 0, "+");
        addPhaseButton.onClick(new Runnable() {
            @Override
            public void run() {
                addPhase();
            }
        });
        panel.addElementFromTop(addPhaseButton);

        // Upload button
        uploadButton = new Button(0, 0, "Send");
        push();
        textSize(uploadButton.textSize);
        float uploadButtonWidth = textWidth(uploadButton.text);
        uploadButton.setSize(uploadButtonWidth + 2 * 10, uploadButton.size.y);
        pop();
        uploadButton.setPos(panel.size.x / 2 - uploadButton.size.x / 2 - margin,
                panel.size.y / 2 - margin - uploadButton.size.y / 2);
        panel.addElement(uploadButton);
        uploadButton.onClick(new Runnable() {
            @Override
            public void run() {
                uploadPhases();
            }
        });
    }

    private void editPhase(int index) {
        for (Movement movement : sketch.movements) {
            movement.highlight = false;
        }
        for (Movement movement : phases.get(index)) {
            movement.highlight = true;
        }

        if (editing != -1 && editing != index) {
            new Animator(textButtons.get(editing), 0.2).setStrokeColor(Button.getDefaults().strokeColor).start();
        }
        new Animator(textButtons.get(index), 0.2).setStrokeColor(color(255)).start();

        editing = index;
    }

    private void deletePhase(int index) {
        phases.remove(index);
        createButtons();
    }

    public void uploadPhases() {
        sketch.uploadPhases(phases);
    }

    public void draw() {
        panel.draw();

        if (isActive()) {
            noStroke();
            fill(36, 169, 209);
            circle(Sketch.stopLineTopLeft, stopLineButtonRadius);
            circle(Sketch.stopLineBottomRight, stopLineButtonRadius);

            if (mousePressed) {
                float distTL = PVector.dist(mouse, Sketch.stopLineTopLeft);
                float distBR = PVector.dist(mouse, Sketch.stopLineBottomRight);

                if (draggingButton) {
                    if (distTL < distBR) {
                        Sketch.stopLineTopLeft = mouse.copy();
                    } else {
                        Sketch.stopLineBottomRight = mouse.copy();
                    }
                } else {
                    if (distTL < stopLineButtonRadius) {
                        draggingButton = true;
                        Sketch.stopLineTopLeft = mouse.copy();
                    } else if (distBR < stopLineButtonRadius) {
                        draggingButton = true;
                        Sketch.stopLineBottomRight = mouse.copy();
                    }
                }
            } else {
                if (draggingButton) {
                    draggingButton = false;
                    sketch.updateStopLines();
                }
            }
        }
    }

    public void mousePressed() {
        if (editing != -1) {
            Movement movement = null;
            for (Movement m : sketch.movements) {
                if (m.hover()) {
                    movement = m;
                    break;
                }
            }
            if (movement == null)
                return;

            boolean highlight = !movement.highlight;
            if (mouseButton == LEFT) {
                // Select all of same movement
                for (Movement m : movement.road.movements.get(movement.type)) {
                    m.highlight = highlight;
                    if (highlight) {
                        phases.get(editing).add(m);
                    } else {
                        phases.get(editing).remove(m);
                    }
                }
            } else if (mouseButton == RIGHT) {
                // Select all of same direction (all lefts, straights, or rights)
                for (Movement m : movement.road.movementsAll) {
                    if (m.direction == movement.direction) {
                        m.highlight = highlight;
                        if (highlight) {
                            phases.get(editing).add(m);
                        } else {
                            phases.get(editing).remove(m);
                        }
                    }
                }
            } else if (mouseButton == MIDDLE) {
                // Select every movement
                for (Movement m : movement.road.movementsAll) {
                    m.highlight = highlight;
                    if (highlight) {
                        phases.get(editing).add(m);
                    } else {
                        phases.get(editing).remove(m);
                    }
                }
            }
        }
    }

    public void keyPressed() {
        String[] allowedKeys = { "Left", "Right", "Up", "Down", "P", "B", "Delete", "Backspace" };
        if (!Arrays.asList(allowedKeys).contains(keyString))
            return;

        Movement movement = null;
        for (Movement m : sketch.movements) {
            if (m.hover()) {
                movement = m;
                break;
            }
        }
        if (movement == null)
            return;

        Road road = movement.road;
        int roadIndex = -1;
        for (int i = 0; i < sketch.roads.length; i++) {
            if (sketch.roads[i] == road) {
                roadIndex = i;
                break;
            }
        }
        if (roadIndex == -1) {
            println("Road not found somehow");
            return;
        }

        switch (keyString) {
            case "Left":
                sketch.updateRoadsAddition(roadIndex, MovementType.CAR_LEFT, Direction.LEFT);
                break;
            case "Right":
                sketch.updateRoadsAddition(roadIndex, MovementType.CAR_RIGHT, Direction.RIGHT);
                break;
            case "Up":
                sketch.updateRoadsAddition(roadIndex, MovementType.CAR_STRAIGHT, Direction.STRAIGHT);
                break;
            case "Down":
                sketch.updateRoadsAddition(roadIndex, MovementType.CAR_RIGHT, Direction.RIGHT);
                break;
            case "P":
                sketch.updateRoadsAddition(roadIndex, MovementType.PEDESTRIAN, Direction.STRAIGHT);
                break;
            case "B":
                sketch.updateRoadsAddition(roadIndex, MovementType.BIKE_STRAIGHT, Direction.STRAIGHT);
                break;
            case "Delete":
                sketch.updateRoadsRemoval(roadIndex, movement);
                break;
            case "Backspace":
                sketch.updateRoadsRemoval(roadIndex, movement);
                break;
        }
    }
}
