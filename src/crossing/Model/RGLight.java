package crossing.Model;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lisirrx on 4/30/17.
 */
public class RGLight {

    private final int RATE = 3; // by second

    public static enum Direction {EW, SN}

    Direction direction;

    private volatile Paint color; // false for red, true for green

    private int period;
    private Circle circle;

    public RGLight(Direction direction, Paint color, Circle circle) {
        this.direction = direction;
        this.color = color;
        circle.setFill(color);
        this.circle = circle;
    }

    public void start() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (color == Color.RED) {
                    color = Color.GREEN;
                    Platform.runLater(() -> {
                        circle.setFill(Color.GREEN);
                    });
                } else if (color == Color.GREEN) {
                    color = Color.RED;
                    Platform.runLater(() -> {
                        circle.setFill(Color.RED);
                    });
                }
            }
        };
        timer.schedule(timerTask, 0, 1000 * RATE);
    }

    public Direction getDirection() {

        return direction;
    }

    public boolean getColor() {
        return color == Color.GREEN;
    }
}
