class PhaseDirection {
    int roadIndex;
    MovementType[] types;

    public PhaseDirection(int roadIndex, MovementType... movementTypes) {
        this.roadIndex = roadIndex;
        this.types = movementTypes;
    }
}
