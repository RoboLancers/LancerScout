package robolancer.com.lancerscout.models.pit;

public enum Climb {
    LEVEL_1("Level 1"),
    LEVEL_2("Level 2"),
    LEVEL_3("Level 3");

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
