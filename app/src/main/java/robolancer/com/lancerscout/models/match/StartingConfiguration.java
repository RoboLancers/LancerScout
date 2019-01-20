package robolancer.com.lancerscout.models.match;

public enum StartingConfiguration {
    LEVEL_1("Level 1"),
    LEVEL_2("Level 2");

    private final String name;

    StartingConfiguration(String name){
        this.name = name;
    }

    private String getName(){
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
