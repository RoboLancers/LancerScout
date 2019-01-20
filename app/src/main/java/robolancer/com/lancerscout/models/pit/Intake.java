package robolancer.com.lancerscout.models.pit;

public enum Intake {
    FLOOR_INTAKE("Floor Intake"),
    HUMAN_INTAKE("Portal/Exchange Intake"),
    BOTH_INTAKES("Both Intakes"),
    NO_INTAKE("None");

    private final String name;

    Intake(String name) {
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
