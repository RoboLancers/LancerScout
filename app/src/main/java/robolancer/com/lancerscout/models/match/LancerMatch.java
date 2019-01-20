package robolancer.com.lancerscout.models.match;

import java.io.Serializable;
import java.util.Objects;

public class LancerMatch implements Serializable {
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
    private boolean robotBrokeDown;

    private String comment;

    public LancerMatch(int matchNumber, int teamNumber, AllianceColor color, StartingConfiguration startingConfiguration, boolean crossedAutoLine, Sandstorm sandstorm, int rocketCargo, int rocketHatch, int shipCargo, int shipHatch, EndGameAttempt endGameAttempt, boolean robotBrokeDown, String comment) {
        this.matchNumber = matchNumber;
        this.teamNumber = teamNumber;
        this.color = color;
        this.startingConfiguration = startingConfiguration;
        this.crossedAutoLine = crossedAutoLine;
        this.sandstorm = sandstorm;
        this.rocketCargo = rocketCargo;
        this.rocketHatch = rocketHatch;
        this.shipCargo = shipCargo;
        this.shipHatch = shipHatch;
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

    public Sandstorm getSandstorm() {
        return sandstorm;
    }

    public int getRocketCargo() {
        return rocketCargo;
    }

    public int getRocketHatch() {
        return rocketHatch;
    }

    public int getShipHatch() {
        return shipHatch;
    }

    public int getShipCargo() {
        return shipCargo;
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
        return Objects.hash(matchNumber, teamNumber, color, startingConfiguration, crossedAutoLine, sandstorm, rocketCargo, rocketHatch, shipCargo, shipHatch, endGameAttempt, robotBrokeDown, comment);
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
        matchInfo.append(match.getSandstorm());
        matchInfo.append("\n");

        matchInfo.append("\nTeleOp\n");
        matchInfo.append("Rocket Cargo: ");
        matchInfo.append(match.getRocketCargo());
        matchInfo.append("\n");
        matchInfo.append("Rocket Hatch: ");
        matchInfo.append(match.getRocketHatch());
        matchInfo.append("\n");
        matchInfo.append("Ship Cargo: ");
        matchInfo.append(match.getShipCargo());
        matchInfo.append("\n");
        matchInfo.append("Ship Hatch: ");
        matchInfo.append(match.getShipHatch());
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
