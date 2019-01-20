package robolancer.com.lancerscout.models.pit;

public class LancerPitBuilder {
    private int teamNumber;
    private Drivetrain drivetrain;

    private Intake cargoIntake;
    private Intake hatchIntake;
    private Climb climb;

    private int robotWeight;
    private ProgrammingLanguage programmingLanguage;
    private String comments;

    public LancerPitBuilder setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
        return this;
    }

    public LancerPitBuilder setDrivetrain(Drivetrain drivetrain) {
        this.drivetrain = drivetrain;
        return this;
    }

    public LancerPitBuilder setCargoIntake(Intake intake) {
        this.cargoIntake = intake;
        return this;
    }

    public LancerPitBuilder setHatchIntake(Intake intake) {
        this.hatchIntake = intake;
        return this;
    }

    public LancerPitBuilder setClimb(Climb climb) {
        this.climb = climb;
        return this;
    }

    public LancerPitBuilder setRobotWeight(int robotWeight) {
        this.robotWeight = robotWeight;
        return this;
    }

    public LancerPitBuilder setProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
        return this;
    }

    public LancerPitBuilder setComments(String comments) {
        this.comments = comments;
        return this;
    }

    public LancerPit createLancerPit() {
        return new LancerPit(teamNumber, drivetrain, cargoIntake, hatchIntake, climb, robotWeight, programmingLanguage, comments);
    }
}
