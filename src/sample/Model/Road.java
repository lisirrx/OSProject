package sample.Model;

import sample.util.Pair;
import sample.util.Postion;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by user on 4/30/17.
 */
public class Road implements Runnable{

    public static enum Direction {W2E, E2W, N2S, S2N}
    public static enum Type {Common, Special}
    final Type type;


    private Direction direction;

    private CopyOnWriteArrayList<Vehicle> vehicles;

    private Postion postion;

    private int [] rowMutexIndexList;
    private int [] colMutexIndexList;
    final static double halfLength = 320;
    final static double crossBlockLength = 40;

    public int[] getRowMutexIndexList() {
        return rowMutexIndexList;
    }

    public int[] getColMutexIndexList() {
        return colMutexIndexList;
    }

    public Road(Type type, Direction direction, Postion postion){
        this.type = type;
        this.direction = direction;
        this.postion = postion;
        this.vehicles = new CopyOnWriteArrayList<Vehicle>();
        switch (direction){
            case E2W:
                if (type == Type.Common){
                    rowMutexIndexList = new int[]{0, 0, 0 ,0};
                    colMutexIndexList = new int[]{3, 2, 1, 0};
                } else {
                    rowMutexIndexList = new int[]{1, 1, 1 ,1};
                    colMutexIndexList = new int[]{3, 2, 1, 0};
                }
                break;
            case W2E:
                if (type == Type.Common){
                    rowMutexIndexList = new int[]{3, 3, 3 ,3};
                    colMutexIndexList = new int[]{0, 1, 2, 3};
                } else {
                    rowMutexIndexList = new int[]{2, 2, 2 ,2};
                    colMutexIndexList = new int[]{0, 1, 2, 3};
                }
                break;
            case N2S:
                if (type == Type.Common){
                    rowMutexIndexList = new int[]{0, 1, 2 ,3};
                    colMutexIndexList = new int[]{0, 0, 0, 0};
                } else {
                    rowMutexIndexList = new int[]{0, 1, 2 ,3};
                    colMutexIndexList = new int[]{1, 1, 1, 1};
                }
                break;
            case S2N:
                if (type == Type.Common){
                    rowMutexIndexList = new int[]{3, 2, 1 ,0};
                    colMutexIndexList = new int[]{3, 3, 3, 3};
                } else {
                    rowMutexIndexList = new int[]{3, 2, 1 ,0};
                    colMutexIndexList = new int[]{2, 2, 2, 2};
                }
                break;
        }
    }
    public Type getType() {
        return type;
    }

    public void addVehicle(Vehicle v){

        if ( vehicles.size() == 0 || vehicles.get(vehicles.size() - 1).getMileage() >= 70) {
            vehicles.add(v);
        }
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

    public CopyOnWriteArrayList<Vehicle> getVehicles(){
        return vehicles;
    }

    @Override
    public void run() {
        while (true) {
            for (Vehicle v : vehicles) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
                if (v.getMileage() <= 800) {
                    v.move();
                }
            }
        }
    }
}
