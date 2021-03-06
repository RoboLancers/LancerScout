package robolancer.com.lancerscout.models.match;

public enum EndGameAttempt {
    DID_NOT_CLIMB("Did not climb"),
    LEVEL_1("Climbed to level 1"),
    LEVEL_2("Climbed to level 2"),
    LEVEL_3("Climbed to level 3");

    private final String name;

    EndGameAttempt(String name){
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
