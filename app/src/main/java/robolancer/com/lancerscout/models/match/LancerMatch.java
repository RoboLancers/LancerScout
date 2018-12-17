package robolancer.com.lancerscout.models.match;

import java.io.Serializable;
import java.util.Objects;

public class LancerMatch implements Serializable {

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
    private boolean robotBrokeDown;

    private String comment;

    public LancerMatch(int matchNumber, int teamNumber, AllianceColor color, StartingConfiguration startingConfiguration, boolean crossedAutoLine, AutonomousAttempt autonomousAttempt, boolean wrongSideAuto, int allianceSwitch, int centerScale, int opponentSwitch, int exchange, EndGameAttempt endGameAttempt, boolean robotBrokeDown, String comment) {
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
        this.robotBrokeDown = robotBrokeDown;
        this.comment = comment;
    }

    public int getMatchNumber() {
        return matchNumber;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public AllianceColor getColor() {
        return color;
    }

    public StartingConfiguration getStartingConfiguration() {
        return startingConfiguration;
    }

    public boolean getCrossedAutoLine() {
        return crossedAutoLine;
    }

    public AutonomousAttempt getAutonomousAttempt() {
        return autonomousAttempt;
    }

    public boolean getWrongSideAuto() {
        return wrongSideAuto;
    }

    public int getAllianceSwitch() {
        return allianceSwitch;
    }

    public int getCenterScale() {
        return centerScale;
    }

    public int getOpponentSwitch() {
        return opponentSwitch;
    }

    public int getExchange() {
        return exchange;
    }

    public EndGameAttempt getEndGameAttempt() {
        return endGameAttempt;
    }

    public boolean getRobotBrokeDown() {
        return robotBrokeDown;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString(){
        return "Match " + getMatchNumber() + " - Team " + getTeamNumber();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this.getClass() != other.getClass()) {
            return false;
        }

        return this.matchNumber == ((LancerMatch) other).matchNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchNumber, teamNumber, color, startingConfiguration, crossedAutoLine, autonomousAttempt, wrongSideAuto, allianceSwitch, centerScale, opponentSwitch, exchange, endGameAttempt, robotBrokeDown, comment);
    }

    public String getMatchInfo(LancerMatch match){
        StringBuilder matchInfo = new StringBuilder();

        matchInfo.append("Alliance Color: ");
        matchInfo.append(match.getColor());
        matchInfo.append("\n");
        matchInfo.append("Starting Configuration: ");
        matchInfo.append(match.getStartingConfiguration());
        matchInfo.append("\n");

        matchInfo.append("\nAutonomous\n");
        matchInfo.append("Crossed auto line: ");
        matchInfo.append(match.getCrossedAutoLine());
        matchInfo.append("\n");
        matchInfo.append("Autonomous Attempt: ");
        matchInfo.append(match.getAutonomousAttempt());
        matchInfo.append("\n");
        matchInfo.append("Put cube on wrong side? ");
        matchInfo.append(match.getWrongSideAuto() ? "Yes" : "No");
        matchInfo.append("\n");

        matchInfo.append("\nTeleOp\n");
        matchInfo.append("Alliance Switch: ");
        matchInfo.append(match.getAllianceSwitch());
        matchInfo.append("\n");
        matchInfo.append("Center Scale: ");
        matchInfo.append(match.getCenterScale());
        matchInfo.append("\n");
        matchInfo.append("Opponent Switch: ");
        matchInfo.append(match.getOpponentSwitch());
        matchInfo.append("\n");
        matchInfo.append("Exchange: ");
        matchInfo.append(match.getExchange());
        matchInfo.append("\n");

        matchInfo.append("\nEnd Game\n");
        matchInfo.append("End Game Attempt: ");
        matchInfo.append(match.getEndGameAttempt());
        matchInfo.append("\n");
        matchInfo.append("Did robot break down? ");
        matchInfo.append(match.getRobotBrokeDown() ? "Yes" : "No");
        matchInfo.append("\n");

        matchInfo.append("Other Comments: ");
        matchInfo.append(match.getComment());
        matchInfo.append("\n");

        return matchInfo.toString();
    }
}
