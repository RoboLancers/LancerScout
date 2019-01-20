package robolancer.com.lancerscout.models.match;

public class LancerMatchBuilder {
    private int matchNumber;
    private int teamNumber;
    private AllianceColor color;
    private StartingConfiguration startingConfiguration;

    private boolean crossedAutoLine;
    private Sandstorm sandstorm;

    private int rocketCargo;
    private int rocketHatch;
    private int shipCargo;
    private int shipHatch;

    private EndGameAttempt endGameAttempt;
    private boolean brokeDown;
    private String comment;

    public LancerMatchBuilder setMatchNumber(int matchNumber) {
        this.matchNumber = matchNumber;
        return this;
    }

    public LancerMatchBuilder setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
        return this;
    }

    public LancerMatchBuilder setColor(AllianceColor color) {
        this.color = color;
        return this;
    }

    public LancerMatchBuilder setStartingConfiguration(StartingConfiguration startingConfiguration) {
        this.startingConfiguration = startingConfiguration;
        return this;
    }

    public LancerMatchBuilder setCrossedAutoLine(boolean crossedAutoLine) {
        this.crossedAutoLine = crossedAutoLine;
        return this;
    }

    public LancerMatchBuilder setSandstorm(Sandstorm sandstorm) {
        this.sandstorm = sandstorm;
        return this;
    }

    public LancerMatchBuilder setRocketCargo(int rocketCargo) {
        this.rocketCargo = rocketCargo;
        return this;
    }

    public LancerMatchBuilder setRocketHatch(int rocketHatch) {
        this.rocketHatch = rocketHatch;
        return this;
    }

    public LancerMatchBuilder setShipCargo(int shipCargo) {
        this.shipCargo = shipCargo;
        return this;
    }

    public LancerMatchBuilder setShipHatch(int shipHatch) {
        this.shipHatch = shipHatch;
        return this;
    }

    public LancerMatchBuilder setEndGameAttempt(EndGameAttempt endGameAttempt) {
        this.endGameAttempt = endGameAttempt;
        return this;
    }

    public LancerMatchBuilder setBrokeDown(boolean brokeDown) {
        this.brokeDown = brokeDown;
        return this;
    }

    public LancerMatchBuilder setComment(String comments){
        this.comment = comments;
        return this;
    }

    public LancerMatch createLancerMatch() {
        return new LancerMatch(matchNumber, teamNumber, color, startingConfiguration, crossedAutoLine, sandstorm, rocketCargo, rocketHatch, shipCargo, shipHatch, endGameAttempt, brokeDown, comment);
    }
}