package robolancer.com.lancerscout.models;

public class LancerMatch {

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

    public LancerMatch(int matchNumber, int teamNumber, AllianceColor color, StartingConfiguration startingConfiguration, boolean crossedAutoLine, AutonomousAttempt autonomousAttempt, boolean wrongSideAuto, int allianceSwitch, int centerScale, int opponentSwitch, int exchange, EndGameAttempt endGameAttempt, boolean brokeDown, String comment) {
        this.matchNumber = matchNumber;
        this.teamNumber = teamNumber;
        this.color = color;
        this.startingConfiguration = startingConfiguration;
        this.crossedAutoLine = crossedAutoLine;
        this.autonomousAttempt = autonomousAttempt;
        this.wrongSideAuto = wrongSideAuto;
        this.allianceSwitch = allianceSwitch;
        this.centerScale = centerScale;
        this.opponentSwitch = opponentSwitch;
        this.exchange = exchange;
        this.endGameAttempt = endGameAttempt;
        this.brokeDown = brokeDown;
        this.comment = comment;
    }
}
