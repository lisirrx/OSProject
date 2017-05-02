package sample.Model;

import sample.util.Postion;
import sample.util.Velocity;

/**
 * Created by user on 4/30/17.
 */
public class Vehicle {



    final private double width = 20;
    final private double length = 50;
    final private double safeDistance = 20;
    final private double arcW = 10;
    final private double arcL = 25;


    private RGLightChangeListener lightChangeListener;
    private CrossingMutexListener crossingMutexListener;
    private Road road;
    private int index;
    private Postion postion;
    private Velocity velocity;
    private double mileage = 0;

    private boolean stopSignal = false;

    public Vehicle(Road road, Velocity velocity){
        this.postion = new Postion(road.getPostion());
        this.velocity  =velocity;
        this.road = road;
        this.index = road.getCount();
    }

    public void registRGLightChangeListener(RGLightChangeListener listener){
        this.lightChangeListener = listener;
    }

    public void registCrossingMutexListener(CrossingMutexListener listener){
        this.crossingMutexListener = listener;
    }

    public synchronized void move(){

        System.out.println(lightChangeListener.getSignal() + " " + this.hashCode()+ " moved " + getMileage());


        if (check()) {
            postion.setX(postion.getX() + velocity.getX());
            postion.setY(postion.getY() + velocity.getY());
            mileage += velocity.getX() + velocity.getY();
            System.out.println("Vehicle" + this.hashCode() +" move");
        }


    }

    private boolean check(){

        double distance = 800;

        if (index != 0) {
            if (road.getDirection() == Road.Direction.E2W || road.getDirection() == Road.Direction.W2E) {
                distance = Math.abs(road.getVehicles().get(index - 1).postion.getX()
                        - road.getVehicles().get(index).postion.getX());
            } else {
                distance = Math.abs(road.getVehicles().get(index - 1).postion.getY()
                        - road.getVehicles().get(index).postion.getY());
            }
        }

        boolean flag = true;

        if (mileage == Road.halfLength){
            if (road.type == Road.Type.Common && !lightChangeListener.getSignal()){
                flag = false;
            } else {
                flag = checkMutexSignal(0) == 1;
            }
        } else if (mileage == Road.halfLength + Road.crossBlockLength){
            releaseMutexSignal(0);
            flag = checkMutexSignal(1) == 1;
        } else if (mileage == Road.halfLength + 2 * Road.crossBlockLength){
            releaseMutexSignal(1);
            flag = checkMutexSignal(2) == 1;
        } else if (mileage == Road.halfLength + 3 * Road.crossBlockLength){
            releaseMutexSignal(2);
            flag = checkMutexSignal(3) == 1;
        } else if (mileage == Road.halfLength + 4 * Road.crossBlockLength){
            releaseMutexSignal(3);
        }
        System.out.println(distance);

        return flag && distance >= safeDistance;
    }

    private int checkMutexSignal(int index){
        return crossingMutexListener.checkSignal(road.getRowMutexIndexList()[index],
                road.getColMutexIndexList()[index]);
    }

    public double getMileage() {
        return mileage;
    }

    private void releaseMutexSignal(int index){
        crossingMutexListener.release(road.getRowMutexIndexList()[index],
                road.getColMutexIndexList()[index]);
    }

}


