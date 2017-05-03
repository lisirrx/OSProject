package crossing;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import crossing.Model.*;
import crossing.util.CrossingMutexListener;
import crossing.util.RGLightChangeListener;


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
    private Group mainGroup;

    @FXML
    private Circle NSLight;
    @FXML
    private Circle EWLight;


    CrossingMutex mutex;
    CrossingMutexListener crossingMutexListener;

    RGLightChangeListener listenerSN;
    RGLightChangeListener listenerEW;


    private static Road.Direction[] directions = {Road.Direction.S2N, Road.Direction.N2S,
            Road.Direction.W2E, Road.Direction.E2W};
    private static Road.Type[] types = {Road.Type.Common, Road.Type.Special};

    private static Road[] roads = new Road[8];
    // [S2NC, S2NS, N2SC, N2SS, W2EC, W2ES, E2WC, E2WS]

    public void init() {
        RGLight lightSN = new RGLight(RGLight.Direction.SN, Color.RED, NSLight);
        RGLight lightEW = new RGLight(RGLight.Direction.EW, Color.GREEN, EWLight);

        mutex = new CrossingMutex();
        crossingMutexListener = new CrossingMutexListener(mutex);

        listenerSN = new RGLightChangeListener(lightSN);
        listenerEW = new RGLightChangeListener(lightEW);


        lightSN.start();
        lightEW.start();


        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                roads[i * 2 + j] = new Road(types[j], directions[i]);
            }
        }
        for (int k = 0; k < 8; k++) {
            Thread t = new Thread(roads[k]);
            t.start();
        }


    }

    public void addAVehicle(int index) {
        Road current = roads[index];
        RGLightChangeListener listener;
        if (index < 4) {
            listener = listenerSN;
        } else {
            listener = listenerEW;
        }


        Vehicle v = new Vehicle(current, 10);
        v.registRGLightChangeListener(listener);
        v.registCrossingMutexListener(crossingMutexListener);
//        System.out.println("try to add v - ----------- - - - -- - - - - - - -- !");

        if (current.addVehicle(v)) {
            Platform.runLater(() -> {
                Rectangle rectangle = new Rectangle(v.getWidth(), v.getHeight());
                rectangle.xProperty().bindBidirectional(v.getPosition().getXProperty());
                rectangle.yProperty().bindBidirectional(v.getPosition().getYProperty());
                if (current.getType() == Road.Type.Special) {
                    rectangle.setFill(Color.RED);
                }
                mainGroup.getChildren().add(rectangle);
            });


        }
    }


    @FXML
    void onClick(ActionEvent event) {
        Button btn = (Button) event.getSource();
        int index = Integer.valueOf(btn.getId().substring(btn.getId().length() - 1));
//        System.out.println(index);

        addAVehicle(index);
    }

}
