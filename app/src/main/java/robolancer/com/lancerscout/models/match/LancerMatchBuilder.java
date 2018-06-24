package robolancer.com.lancerscout.models.match;

public class LancerMatchBuilder {
    private int matchNumber;
    private int teamNumber;
    private AllianceColor color;
    private StartingConfiguration startingConfiguration;
    private boolean crossedAutoLine;
    private AutonomousAttempt autonomousAttempt;
    private boolean wrongSideAuto;
    private int allianceSwitch;
    private int centerScale;
    private int opponentSwitch;
    private int exchange;
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

    public LancerMatchBuilder setAutonomousAttempt(AutonomousAttempt autonomousAttempt) {
        this.autonomousAttempt = autonomousAttempt;
        return this;
    }

    public LancerMatchBuilder setWrongSideAuto(boolean wrongSideAuto) {
        this.wrongSideAuto = wrongSideAuto;
        return this;
    }

    public LancerMatchBuilder setAllianceSwitch(int allianceSwitch) {
        this.allianceSwitch = allianceSwitch;
        return this;
    }

    public LancerMatchBuilder setCenterScale(int centerScale) {
        this.centerScale = centerScale;
        return this;
    }

    public LancerMatchBuilder setOpponentSwitch(int opponentSwitch) {
        this.opponentSwitch = opponentSwitch;
        return this;
    }

    public LancerMatchBuilder setExchange(int exchange) {
        this.exchange = exchange;
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
        return new LancerMatch(matchNumber, teamNumber, color, startingConfiguration, crossedAutoLine, autonomousAttempt, wrongSideAuto, allianceSwitch, centerScale, opponentSwitch, exchange, endGameAttempt, brokeDown, comment);
    }
}