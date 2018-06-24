package robolancer.com.lancerscout.models.pit;

public enum Climb {
    SOLO_CLIMB("Solo Climb"),
    CLIMBER_WITH_RAMP_1("Climber with ramp for one robot"),
    CLIMBER_WITH_RAMP_2("Climber with ramp for two robots"),
    RAMP_1("Ramp for one robot"),
    RAMP_2("Ramp for two robots"),
    NONE_INTAKE("None");

    private final String name;

    Climb(String name) {
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
