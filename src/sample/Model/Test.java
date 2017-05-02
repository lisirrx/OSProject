package sample.Model;

import sample.util.Postion;
import sample.util.Velocity;

/**
 * Created by user on 5/2/17.
 */
public class Test {

    public static void main(String [] args){
//
//        CrossingMutex mutex = new CrossingMutex();
//        CrossingMutexEvent event = new CrossingMutexEvent(mutex);
//
//        CrossingMutexListener listener = new CrossingMutexListener(event);
//
//        Runnable r = ()->{
//            for (int i = 0; i < 4; i++) {
//                for (int j = 0; j < 4; j ++) {
//
//                    int temp = listener.checkSignal(i, j);
//                    System.out.println(Thread.currentThread().getId() + " -- " + "(" + i + " " + j + ")" + " get " + temp);
//                    if (temp == 1) {
//                        System.out.println(Thread.currentThread().getId() + " -- " + "(" + i + " " + j + ")" + "do something");
//                        listener.release(i, j);
//                    }
//                }
//            }
//
//        };
//
//
//
//        Thread t1 = new Thread(r);
//        Thread t2 = new Thread(r);
//        Thread t3 = new Thread(r);
//        Thread t4 = new Thread(r);
//        t1.start();
//        t2.start();
//        t3.start();
//        t4.start();


        RGLight lightSN = new RGLight(RGLight.Direction.SN, false);
        RGLight lightEW = new RGLight(RGLight.Direction.EW, false);
        lightSN.start(1);
        CrossingMutex mutex = new CrossingMutex();
        CrossingMutexListener crossingMutexListener = new CrossingMutexListener(new CrossingMutexEvent(mutex));

        RGLightChangeListener listenerSN = new RGLightChangeListener(new RGLightChangeEvent(lightSN));
        RGLightChangeListener listenerEW = new RGLightChangeListener(new RGLightChangeEvent(lightEW));

        Road roadSN = new Road(Road.Type.Common, Road.Direction.S2N, new Postion( 300, 0));
        Road roadNS = new Road(Road.Type.Common, Road.Direction.N2S, new Postion( 100, 800));


        Thread t = new Thread(roadSN);
        t.start();

        while (true) {
            Vehicle v1 = new Vehicle(roadSN, new Velocity(0, 10));
            roadSN.addVehicle(v1);
            v1.registRGLightChangeListener(listenerSN);
            v1.registCrossingMutexListener(crossingMutexListener);
        }



    }
}
