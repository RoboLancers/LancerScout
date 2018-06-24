package robolancer.com.lancerscout.models.match;

public enum EndGameAttempt {
    NOT_PARKED_ON_PLATFORM ("Not parked on platform"),
    PARKED_ON_PLATFORM ("Parked on platform"),
    ATTEMPTED_TO_HOOK_ONTO_BAR ("Attempted to hook onto bar"),
    ATTEMPTED_TO_ATTACH_TO_A_ROBOT ("Attempted to attach to a robot"),
    ATTEMPTED_TO_CARRY_A_ROBOT ("Attempted to carry a robot"),
    HOOKED_BAR_AND_ATTEMPTED_CLIMB ("Hooked bar and attempted climb"),
    SUCCESSFUL_CLIMB_ON_ANOTHER_ROBOT ("Successful climb on another robot"),
    SUCCESSFUL_CLIMB_WITH_ANOTHER_ROBOT_ATTACHED ("Successful climb with another robot attached"),
    SUCCESSFUL_CLIMB_ON_OWN ("Successful climb on own");

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
