package clubSimulation;

public class GlobalPause {
    private static boolean paused;

    public GlobalPause(){
        paused = false;
    }

    public static synchronized void setpaused(){
        paused = true;
    }

    public static synchronized void setplay(){
        paused = false;
    }

    public static synchronized boolean checkpause(){
        return paused;
    }
}
