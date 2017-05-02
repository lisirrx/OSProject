package sample.Model;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Created by user on 5/2/17.
 */
public class CrossingMutexListener implements EventListener{
    private CrossingMutexEvent event;

    public CrossingMutexListener(CrossingMutexEvent event) {
        this.event = event;
    }

    public int checkSignal(int x, int y) {
        CrossingMutex mutex =  (CrossingMutex) event.getSource();
        return mutex.checkAndSet(x, y);
    }

    public void release(int x, int y){
        CrossingMutex mutex =  (CrossingMutex) event.getSource();
        mutex.release(x, y);
    }
}



class CrossingMutexEvent extends EventObject{
    public CrossingMutexEvent(Object source) {
        super(source);
    }
}
