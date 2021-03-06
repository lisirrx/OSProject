package crossing.util;

import crossing.Model.CrossingMutex;

import java.util.EventListener;

/**
 * Created by lisirrx on 5/2/17.
 */
public class CrossingMutexListener implements EventListener{
    private CrossingMutex mutex;

    public CrossingMutexListener(CrossingMutex mutex) {
        this.mutex = mutex;
    }

    public int checkSignal(int x, int y) {
        return mutex.checkAndSet(x, y);
    }

    public void release(int x, int y){
        mutex.release(x, y);
    }

    public CrossingMutex getMutex() {
        return mutex;
    }

    public void reset(){
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                mutex.getMutex()[i][j] = 1;
                }
            }
        }
    }



