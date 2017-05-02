package sample.Model;

/**
 * Created by user on 5/2/17.
 */
public class Test {

    public static void main(String [] args){

        CrossingMutex mutex = new CrossingMutex();
        CrossingMutexEvent event = new CrossingMutexEvent(mutex);

        CrossingMutexListener listener = new CrossingMutexListener(event);

        Runnable r = ()->{
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j ++) {

                    int temp = listener.checkSignal(i, j);
                    System.out.println(Thread.currentThread().getId() + " -- " + "(" + i + " " + j + ")" + " get " + temp);
                    if (temp == 1) {
                        System.out.println(Thread.currentThread().getId() + " -- " + "(" + i + " " + j + ")" + "do something");
                        listener.release(i, j);
                    }
                }
            }

        };



        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        Thread t3 = new Thread(r);
        Thread t4 = new Thread(r);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }
}
