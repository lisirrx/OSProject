package crossing.util;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import crossing.Model.Vehicle;

/**
 * Created by lisirrx on 5/2/17.
 */
public class TwoValueTuple{
    protected SimpleDoubleProperty xProperty;
    protected double x;
    protected SimpleDoubleProperty yProperty;
    protected double y;

    public TwoValueTuple(double x, double y) {
        this.x = x;
        this.y = y;

        Platform.runLater(()->{
            this.xProperty = new SimpleDoubleProperty(x  - Vehicle.width / 2);
            this.yProperty = new SimpleDoubleProperty(y - Vehicle.height / 2);
        });
       }

    public SimpleDoubleProperty getXProperty(){
        return xProperty;
    }

    public SimpleDoubleProperty getYProperty(){
        return yProperty;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        setXProperty(x - Vehicle.width / 2);
    }

    public double getY() {
        return y;
    }

    public void setXProperty(double xProperty) {
        Platform.runLater(()->{
            this.xProperty.set(xProperty);
          });

    }

    public void setYProperty(double yProperty) {
        Platform.runLater(()->{
            this.yProperty.set(yProperty);
        });

    }

    public void setY(double y) {
        this.y = y;
        setYProperty(y - Vehicle.height / 2);
    }
}
