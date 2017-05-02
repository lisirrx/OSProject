package sample.Model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by user on 5/2/17.
 */
public class CrossingMutex {

    private volatile int [][] mutex = {{1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 1} };


    public int[][] getMutex() {
        return mutex;
    }

    public synchronized int checkAndSet(int x, int y){
        int temp = mutex[x][y];
        if (temp == 1) {
            mutex[x][y]--;
        }
        return temp;
    }

    public synchronized void release(int x, int y){
        if (mutex[x][y] == 0){
            mutex[x][y] ++;
        }
    }
}
