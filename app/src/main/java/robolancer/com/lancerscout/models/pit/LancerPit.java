package robolancer.com.lancerscout.models.pit;

import java.io.Serializable;

@SuppressWarnings("WeakerAccess")
public class LancerPit implements Serializable {

    private int teamNumber;
    private Drivetrain drivetrain;
    private CubeIntake cubeIntake;
    private Climb climb;
    private int robotWeight;
    private ProgrammingLanguage programmingLanguage;
    private String comments;

    public LancerPit(int teamNumber, Drivetrain drivetrain, CubeIntake cubeIntake, Climb climb, int robotWeight, ProgrammingLanguage programmingLanguage, String comments) {
        this.teamNumber = teamNumber;
        this.drivetrain = drivetrain;
        this.cubeIntake = cubeIntake;
        this.climb = climb;
        this.robotWeight = robotWeight;
        this.programmingLanguage = programmingLanguage;
        this.comments = comments;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public Drivetrain getDrivetrain() {
        return drivetrain;
    }

    public CubeIntake getCubeIntake() {
        return cubeIntake;
    }

    public Climb getClimb() {
        return climb;
    }

    public int getRobotWeight() {
        return robotWeight;
    }

    public ProgrammingLanguage getProgrammingLanguage() {
        return programmingLanguage;
    }

    public String getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return "Team " + getTeamNumber();
    }
}
