package sample.util;

/**
 * Created by user on 5/2/17.
 */
public class TwoValueTuple{
    protected double x;
    protected double y;

    public TwoValueTuple(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
