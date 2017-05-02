package sample.Model;

import java.util.ArrayList;

/**
 * Created by user on 4/30/17.
 */
public class Road {

    public static enum Direction {W2E, E2W, N2S, S2N}
    private Direction direction;

    private ArrayList<Vehicle> vehicles;

    private Postion postion;
    final static double halfLength = 320;
    final static double crossBlockLength = 40;

    public Road(Direction direction, Postion postion){
        this.direction = direction;
        this.postion = postion;
    }

    public void addVehicle(Vehicle v){
        vehicles.add(v);
    }

    public int getCount(){
        return vehicles.size();
    }

    public Postion getPostion() {
        return postion;
    }

    public Direction getDirection() {

        return direction;
    }

    public ArrayList<Vehicle> getVehicles(){
        return vehicles;
    }

}
