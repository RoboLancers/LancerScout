package robolancer.com.lancerscout.models;

public enum AutonomousAttempt {
    NO_CUBE_ATTEMPT ("No cube attempt"),
    ATTEMPTED_SWITCH ("Attempted switch"),
    ATTEMPTED_SCALE ("Attempted scale"),
    SUCCESSFUL_SWITCH ("Successful switch"),
    SUCCESSFUL_SCALE ("Successful scale");

    private final String name;

    AutonomousAttempt(String name){
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
