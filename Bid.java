class Bid {
    Movement movement;
    int changeTime;
    int waitingTraffic;

    public Bid(Movement movement, int changeTime, int waitingTraffic) {
        this.movement = movement;
        this.changeTime = changeTime;
        this.waitingTraffic = waitingTraffic;
    }
}
