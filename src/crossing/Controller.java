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
import java.util.Timer;
import java.util.TimerTask;
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

    @FXML private Label label1;
    final private String label1Text = "CPU LogicProcessor : ";
    @FXML private Label label2;
    final private String label2Text = "Thread Count : ";

    private double velocity;

    private int cpuCount;
    private int deadLockCont = 0;
    private boolean deadLockFlag = false;
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

        createThreadAndSetvelocity(cpuCount);

        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                    randomCreate();

            }
        };

        timer.schedule(timerTask, 0, 3000);


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

        checkDeadLock();

        addAVehicle(index);
    }


    @FXML void deadlockClear(){

        try {


            deadlockLabel.setVisible(false);

            for (Thread thread : threads) {
                thread.interrupt();
                thread = null;
            }
            threads.clear();


            for (Road road : roads) {
                road.clear();
            }

            crossingMutexListener.reset();
            createThreadAndSetvelocity(cpuCount);

        } catch (ArrayIndexOutOfBoundsException e){
            crossingMutexListener.reset();
        }


        
    }

    @FXML void randomCreate(){

        checkDeadLock();

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


    private void checkDeadLock(){
        if(mutex.checkDeadLock()){
            deadLockCont ++;
            System.out.println(" Inc to ->" +  deadLockCont);
            deadLockFlag = true;
        } else if (deadLockFlag){
            deadLockCont --;
            System.out.println(" Dec to ->" + deadLockCont);
            deadlockLabel.setVisible(false);
            deadLockFlag = false;
        }

        if (deadLockCont >= 5) {
            deadLockCont = 5;
            deadlockLabel.setVisible(true);
        }
    }
    public void createThreadAndSetvelocity (int cpuCount){

        label1.setText(label1Text + cpuCount);

        ArrayList<Runnable> rs = new ArrayList<>();
        switch (cpuCount){
            case 1:
            case 2:
                rs.add(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()){
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
                velocity =  10;
                label2.setText(label2Text + 1);
                break;
            case 3:
            case 4:
                rs.add(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {

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
                        while (!Thread.currentThread().isInterrupted()) {
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
                velocity =  5;
                label2.setText(label2Text  + 2);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
                rs.add(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {
                            roads[0].run();
                            roads[1].run();
                        }
                    }
                });
                rs.add(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {
                            roads[2].run();
                            roads[3].run();
                        }
                    }
                });
                rs.add(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {
                            roads[4].run();
                            roads[5].run();
                        }
                    }
                });
                rs.add(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {
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
                label2.setText(label2Text  + 4);
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
                        while (true) {
                            if (Thread.currentThread().isInterrupted()){
//                                System.out.println("Interrputed");
                            } else {
                                road.run();
                            }
                        }
                    });
                    threads.add(t);
                    t.start();
                }
                velocity = 1;
                label2.setText(label2Text  + 8);
                break;
        }
    }
}




