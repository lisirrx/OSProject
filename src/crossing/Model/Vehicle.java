package crossing.Model;

import crossing.util.Position;
import javafx.beans.property.SimpleBooleanProperty;
import crossing.util.CrossingMutexListener;
import crossing.util.RGLightChangeListener;


/**
 * Created by lisirrx on 4/30/17.
 */
public class Vehicle {


    final static public double width = 20;
    final static public double height = 20;
    final static public double safeDistance = 40;
    final static public double arcW = 10;
    final static public double arcH = 10;


    private RGLightChangeListener lightChangeListener;
    private CrossingMutexListener crossingMutexListener;
    private Road road;
    private int index;
    private Position position;
    private double velocity;
    private double mileage = 0;
    private SimpleBooleanProperty visable;

    public boolean isVisable() {
        return visable.get();
    }

    public SimpleBooleanProperty visableProperty() {
        return visable;
    }

    public void setVisable(boolean visable) {
        this.visable.set(visable);
    }

    public Vehicle(Road road, double velocity) {
        this.position = new Position(road.getPosition());
        this.velocity = velocity;
        this.road = road;
        this.visable = new SimpleBooleanProperty(true);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getWidth() {

        if (road.getDirection() == Road.Direction.N2S || road.getDirection() == Road.Direction.S2N) {
            return width;
        } else {
            return height;
        }
    }

    public double getHeight() {
        if (road.getDirection() == Road.Direction.N2S || road.getDirection() == Road.Direction.S2N) {
            return height;
        } else {
            return width;
        }
    }

    public double getArcW() {

        if (road.getDirection() == Road.Direction.N2S || road.getDirection() == Road.Direction.S2N) {
            return arcW;
        } else {
            return arcH;
        }
    }

    public double getArcH() {
        if (road.getDirection() == Road.Direction.N2S || road.getDirection() == Road.Direction.S2N) {
            return arcH;
        } else {
            return arcW;
        }
    }

    public Position getPosition() {
        return position;
    }

    public void registRGLightChangeListener(RGLightChangeListener listener) {
        this.lightChangeListener = listener;
    }

    public void registCrossingMutexListener(CrossingMutexListener listener) {
        this.crossingMutexListener = listener;
    }

    public void move() {

//        System.out.println(lightChangeListener.getSignal() + " " + this.hashCode()+ " moved " + getMileage());

        if (check()) {

            switch (road.getDirection()) {
                case E2W:
                    position.setX(position.getX() - velocity);
                    break;
                case W2E:
                    position.setX(position.getX() + velocity);
                    break;
                case N2S:
                    position.setY(position.getY() + velocity);
                    break;
                case S2N:
                    position.setY(position.getY() - velocity);
                    break;
            }
            mileage += velocity;
//            System.out.println("Vehicle" + this.hashCode() +" move");
        }


    }

    private boolean check() {

        double distance = 800;

        if (index != 0) {
            if (road.getDirection() == Road.Direction.E2W || road.getDirection() == Road.Direction.W2E) {
                distance = Math.abs(road.getVehicles().get(index - 1).position.getX()
                        - road.getVehicles().get(index).position.getX());
            } else {
                distance = Math.abs(road.getVehicles().get(index - 1).position.getY()
                        - road.getVehicles().get(index).position.getY());
            }
        }

//        System.out.println("Vehicle" + this.hashCode() +" distacne " + distance);

        boolean flag = true;
        double judgeMileage = mileage;

        if (road.getDirection() == Road.Direction.N2S || road.getDirection() == Road.Direction.W2E) {
            judgeMileage += height / 2;
        } else {
            judgeMileage += width / 2;
        }


        if (judgeMileage == Road.halfLength) {
            if (road.type == Road.Type.Common && !lightChangeListener.getSignal()) {
                flag = false;
            } else {
                flag = checkMutexSignal(0) == 1;
            }
        } else if (judgeMileage == Road.halfLength + Road.crossBlockLength) {
            releaseMutexSignal(0);
            flag = checkMutexSignal(1) == 1;
        } else if (judgeMileage == Road.halfLength + 2 * Road.crossBlockLength) {
            releaseMutexSignal(1);
            flag = checkMutexSignal(2) == 1;
        } else if (judgeMileage == Road.halfLength + 3 * Road.crossBlockLength) {
            releaseMutexSignal(2);
            flag = checkMutexSignal(3) == 1;
        } else if (judgeMileage == Road.halfLength + 4 * Road.crossBlockLength) {
            releaseMutexSignal(3);
        }
//        System.out.println(distance);

        return flag && distance >= safeDistance;
    }

    private int checkMutexSignal(int index) {
        return crossingMutexListener.checkSignal(road.getRowMutexIndexList()[index],
                road.getColMutexIndexList()[index]);
    }

    public double getMileage() {
        return mileage;
    }

    private void releaseMutexSignal(int index) {
        crossingMutexListener.release(road.getRowMutexIndexList()[index],
                road.getColMutexIndexList()[index]);
    }


}


