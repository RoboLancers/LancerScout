package robolancer.com.lancerscout.models.match;

public enum Sandstorm {
    AUTONOMOUS("Autonomous"),
    DRIVER_CONTROLLED("Driver Controlled");

    private final String name;

    Sandstorm(String name){
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
