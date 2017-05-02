package sample.Model;

import javax.management.timer.TimerNotification;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 4/30/17.
 */
public class RGLight {

    private final int RATE = 3; // by second

    public static enum Direction {EW, SN}

    Direction direction;

    private volatile boolean color; // false for red, true for green

    private int period;

    public RGLight(Direction direction, boolean color){
        this.direction = direction;
        this.color = color;
    }

    public void start(int period){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                color = !color;
            }
        };
        timer.schedule(timerTask, 0, 1000 * RATE);
    }

    public Direction getDirection() {

        return direction;
    }

    public boolean getColor() {
        return color;
    }
}
