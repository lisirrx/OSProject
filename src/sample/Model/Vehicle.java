package sample.Model;

/**
 * Created by user on 4/30/17.
 */
public class Vehicle {

    public static enum Type {Common, Special}
    Type type;

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

    public Vehicle(Type type, Road road, Velocity velocity){
        this.postion = road.getPostion();
        this.velocity  =velocity;
        this.type = type;
        this.road = road;
        this.index = road.getCount();
    }

    public void registListener(RGLightChangeListener listener){
        this.lightChangeListener = listener;
    }

    public void move(){
            if (!lightChangeListener.getSignal()) {
                postion.xPostion += velocity.xV;
                postion.yPostion += velocity.yV;
                mileage += velocity.xV + velocity.yV;
                System.out.println("Vehicle move");
            }
    }

    private boolean check(){

        double distance = 0;
        if (road.getDirection() == Road.Direction.E2W || road.getDirection() == Road.Direction.W2E){
            distance = Math.abs(road.getVehicles().get(index - 1).postion.xPostion
                    - road.getVehicles().get(index).postion.xPostion);
        } else {
            distance = Math.abs(road.getVehicles().get(index - 1).postion.yPostion
                    - road.getVehicles().get(index).postion.yPostion);
        }

        boolean flag = false;

        if (mileage == Road.halfLength){
            if (type == Type.Common){
                flag = false;
            }

            if (crossingMutexListener.checkSignal())

        }

        return lightChangeListener.getSignal() || distance <= safeDistance;
    }
}



class Postion {
    public double xPostion;
    public double yPostion;

    public Postion(double xPostion, double yPostion){
        this.xPostion = xPostion;
        this.yPostion = yPostion;
    }
}

class Velocity {
    public double xV;
    public double yV;

    public Velocity(double xV, double yV){
        this.xV = xV;
        this.yV = yV;
    }
}