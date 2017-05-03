package crossing;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import crossing.Model.*;
import crossing.util.CrossingMutexListener;
import crossing.util.RGLightChangeListener;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;


public class Controller {
    @FXML
    private Button n2sComBtn_2;

    @FXML
    private Button n2sSpeBtn_3;

    @FXML
    private Button s2nComBtn_7;

    @FXML
    private Button s2nSpeBtn_6;

    @FXML
    private Button e2wComBtn_1;

    @FXML
    private Button e2wSpeBtn_0;

    @FXML
    private Button w2eComBtn_5;

    @FXML
    private Button w2eSpeBtn_4;

    @FXML
    private Button randomCreate;

    @FXML
    private Button deadlockClear;

    @FXML
    private Group mainGroup;

    @FXML
    private Label deadlockLabel;

    @FXML private Circle NSLight;
    @FXML private Circle EWLight;

    private double velocity;

    private int cpuCount;
    private int deadLockCont = 0;
    private CrossingMutex mutex;
    private CrossingMutexListener crossingMutexListener;

    private RGLightChangeListener listenerSN;
    private RGLightChangeListener listenerEW;

    private static Road.Direction [] directions = {Road.Direction.S2N, Road.Direction.N2S,
                                    Road.Direction.W2E, Road.Direction.E2W};
    private static Road.Type [] types = {Road.Type.Common, Road.Type.Special};

    private static Road [] roads = new Road[8];
    // [S2NC, S2NS, N2SC, N2SS, W2EC, W2ES, E2WC, E2WS]

    private ArrayList<Thread> threads = new ArrayList<>();

    public void init(){
        RGLight lightSN = new RGLight(RGLight.Direction.SN, Color.RED, NSLight);
        RGLight lightEW = new RGLight(RGLight.Direction.EW, Color.GREEN, EWLight);

        mutex = new CrossingMutex();
        crossingMutexListener = new CrossingMutexListener(mutex);

        listenerSN = new RGLightChangeListener(lightSN);
        listenerEW = new RGLightChangeListener(lightEW);


        lightSN.start();
        lightEW.start();


        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 2;  j++){
                roads[i * 2 + j] = new Road(types[j], directions[i]);
            }
        }
        cpuCount =  Runtime.getRuntime().availableProcessors();

        createThreadAndSetvelocity(8);

    }

    public void addAVehicle(int index) {
        Road current = roads[index];
        RGLightChangeListener listener;
        if (index < 4){
            listener = listenerSN;
        } else {
            listener = listenerEW;
        }


        Vehicle v = new Vehicle(current, velocity);
        v.registRGLightChangeListener(listener);
        v.registCrossingMutexListener(crossingMutexListener);
//        System.out.println("try to add v - ----------- - - - -- - - - - - - -- !");

        if (current.addVehicle(v)) {
            Platform.runLater(()->{
                Rectangle rectangle = new Rectangle(v.getWidth(), v.getHeight());
                rectangle.xProperty().bindBidirectional(v.getPosition().getXProperty());
                rectangle.yProperty().bindBidirectional(v.getPosition().getYProperty());
                rectangle.visibleProperty().bindBidirectional(v.visableProperty());
                if (current.getType() == Road.Type.Special){
                    rectangle.setFill(Color.RED);
                }
                mainGroup.getChildren().add(rectangle);
            });


        }
    }


    @FXML void onClick(ActionEvent event){
        Button btn = (Button)event.getSource();
        int index = Integer.valueOf(btn.getId().substring(btn.getId().length() - 1));
//        System.out.println(index);

        if(mutex.checkDeadLock()){
            deadLockCont ++;
            if (deadLockCont > 5) {
                deadlockLabel.setVisible(true);
            }
        }

        addAVehicle(index);
    }


    @FXML void deadlockClear(){
        deadlockLabel.setVisible(false);

        for (Road road : roads){
            road.clear();
        }

        for (Thread thread : threads){
            thread.stop();
            thread = null;
        }
        threads.clear();

        crossingMutexListener.reset();

       createThreadAndSetvelocity(cpuCount);
        
    }

    @FXML void randomCreate(){
        if(mutex.checkDeadLock()){
            deadLockCont ++;
            if (deadLockCont > 7) {
                deadlockLabel.setVisible(true);
            }
        }

        int max=7;
        int min=0;
        Random random = new Random();

        Thread t = new Thread(()->{
            for (int i = 0; i < 3; i ++){
                int s = random.nextInt(max)%(max-min+1) + min;
                addAVehicle(s);
            }
        });
        t.start();
    }
    public void createThreadAndSetvelocity(int cpuCount){
        ArrayList<Runnable> rs = new ArrayList<>();
        switch (cpuCount){
            case 1:
            case 2:
                rs.add(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.interrupted()){
                            for (Road road : roads){
                                road.run();
                            }
                        }
                    }
                });
                for (Runnable r : rs){
                    Thread t = new Thread(r);
                    threads.add(t);
                    t.start();
                }
                velocity =  5;
                break;
            case 3:
            case 4:
                rs.add(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.interrupted()) {
                            roads[0].run();
                            roads[1].run();
                            roads[2].run();
                            roads[3].run();
                        }
                    }
                });
                rs.add(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.interrupted()) {
                            roads[4].run();
                            roads[5].run();
                            roads[6].run();
                            roads[7].run();
                        }
                    }
                });
                for (Runnable r : rs){
                    Thread t = new Thread(r);
                    threads.add(t);
                    t.start();
                }
                velocity =  2;
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                rs.add(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.interrupted()) {
                            roads[0].run();
                            roads[1].run();
                        }
                    }
                });
                rs.add(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.interrupted()) {
                            roads[2].run();
                            roads[3].run();
                        }
                    }
                });
                rs.add(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.interrupted()) {
                            roads[4].run();
                            roads[5].run();
                        }
                    }
                });
                rs.add(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.interrupted()) {
                            roads[6].run();
                            roads[7].run();
                        }
                    }
                });
                for (Runnable r : rs){
                    Thread t = new Thread(r);
                    threads.add(t);
                    t.start();
                }
                velocity = 2;
                break;
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                for (Road road : roads){
                    Thread t = new Thread(()-> {
                        while (!Thread.interrupted()) {
                            road.run();
                        }
                    });
                    threads.add(t);
                    t.start();
                }
                velocity = 1;
                break;
        }
    }
}




