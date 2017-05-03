package crossing.Model;

import crossing.util.Position;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by lisirrx on 4/30/17.
 */
public class Road implements Runnable {


    final static public Position N2SC = new Position(340, 0);
    final static public Position S2NC = new Position(460, 800);
    final static public Position N2SS = new Position(380, 0);
    final static public Position S2NS = new Position(420, 800);
    final static public Position E2WC = new Position(800, 340);
    final static public Position W2EC = new Position(0, 460);
    final static public Position E2WS = new Position(800, 380);
    final static public Position W2ES = new Position(0, 420);

    public static enum Direction {W2E, E2W, N2S, S2N}

    public static enum Type {Common, Special}

    final Type type;


    private Direction direction;

    private CopyOnWriteArrayList<Vehicle> vehicles;

    private Position position;

    private int[] rowMutexIndexList;
    private int[] colMutexIndexList;
    final static double halfLength = 320;
    final static double crossBlockLength = 40;

    public int[] getRowMutexIndexList() {
        return rowMutexIndexList;
    }

    public int[] getColMutexIndexList() {
        return colMutexIndexList;
    }

    public Road(Type type, Direction direction) {
        this.type = type;
        this.direction = direction;
        this.vehicles = new CopyOnWriteArrayList<Vehicle>();
        switch (direction) {
            case E2W:
                if (type == Type.Common) {
                    rowMutexIndexList = new int[]{0, 0, 0, 0};
                    colMutexIndexList = new int[]{3, 2, 1, 0};
                    this.position = E2WC;
                } else {
                    rowMutexIndexList = new int[]{1, 1, 1, 1};
                    colMutexIndexList = new int[]{3, 2, 1, 0};
                    this.position = E2WS;
                }
                break;
            case W2E:
                if (type == Type.Common) {
                    rowMutexIndexList = new int[]{3, 3, 3, 3};
                    colMutexIndexList = new int[]{0, 1, 2, 3};
                    this.position = W2EC;
                } else {
                    rowMutexIndexList = new int[]{2, 2, 2, 2};
                    colMutexIndexList = new int[]{0, 1, 2, 3};
                    this.position = W2ES;
                }
                break;
            case N2S:
                if (type == Type.Common) {
                    rowMutexIndexList = new int[]{0, 1, 2, 3};
                    colMutexIndexList = new int[]{0, 0, 0, 0};
                    this.position = N2SC;
                } else {
                    rowMutexIndexList = new int[]{0, 1, 2, 3};
                    colMutexIndexList = new int[]{1, 1, 1, 1};
                    this.position = N2SS;
                }
                break;
            case S2N:
                if (type == Type.Common) {
                    rowMutexIndexList = new int[]{3, 2, 1, 0};
                    colMutexIndexList = new int[]{3, 3, 3, 3};
                    this.position = S2NC;
                } else {
                    rowMutexIndexList = new int[]{3, 2, 1, 0};
                    colMutexIndexList = new int[]{2, 2, 2, 2};
                    this.position = S2NS;
                }
                break;
        }
    }

    public Type getType() {
        return type;
    }

    public synchronized boolean addVehicle(Vehicle v) {
        if (vehicles.size() == 0 || vehicles.get(vehicles.size() - 1).getMileage() >= 70) {
            vehicles.add(v);
            v.setIndex(getCount() - 1);
//            System.out.println("Road add v!-               - ----------- - - - -- - - - - - - -- - -- - ");

            return true;
        }

        return false;
    }

    public int getCount() {
        return vehicles.size();
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {

        return direction;
    }

    public CopyOnWriteArrayList<Vehicle> getVehicles() {
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
                } else {
                    v.move();
                    v.setVisable(false);
                }
            }
        }
    }
}
