class Conflict {
    Movement conflictingMovement;
    int resolvedFrame;

    Conflict(Movement conflictingMovement, int resolvedFrame) {
        this.conflictingMovement = conflictingMovement;
        this.resolvedFrame = resolvedFrame;
    }
}
