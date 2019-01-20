package robolancer.com.lancerscout.models.pit;

import java.io.Serializable;

@SuppressWarnings("WeakerAccess")
public class LancerPit implements Serializable {
    private int teamNumber;
    private Drivetrain drivetrain;

    private Intake cargoIntake;
    private Intake hatchIntake;
    private Climb climb;

    private int robotWeight;
    private ProgrammingLanguage programmingLanguage;
    private String comments;

    public LancerPit(int teamNumber, Drivetrain drivetrain, Intake cargoIntake, Intake hatchIntake, Climb climb, int robotWeight, ProgrammingLanguage programmingLanguage, String comments) {
        this.teamNumber = teamNumber;
        this.drivetrain = drivetrain;

        this.cargoIntake = cargoIntake;
        this.hatchIntake = hatchIntake;
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

    public Intake getCargoIntake() {
        return cargoIntake;
    }

    public Intake getHatchIntake(){
        return hatchIntake;
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
