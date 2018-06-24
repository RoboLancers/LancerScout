package robolancer.com.lancerscout.models.pit;

public enum Drivetrain {
    FOUR_WHEEL_DRIVE("4WD"),
    SIX_WHEEL_DRIVE("6WD"),
    EIGHT_WHEEL_DRIVE("8WD"),
    SWERVE("Swerve"),
    KIWI("Kiwi"),
    CUSTOM("Custom");

    private final String name;

    Drivetrain(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
