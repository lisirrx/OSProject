package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.omg.PortableServer.THREAD_POLICY_ID;
import org.w3c.dom.css.Rect;


public class Controller {
    @FXML
    private Button n2sComBtn;

    @FXML
    private Button n2sSpeBtn;

    @FXML
    private Button s2nComBtn;

    @FXML
    private Button s2nSpeBtn;

    @FXML
    private Button e2wComBtn;

    @FXML
    private Button e2wSpeBtn;

    @FXML
    private Button w2eComBtn;

    @FXML
    private Button w2eSpeBtn;

    @FXML
    private Group mainGroup;


    private Rectangle rectangle;
    private Rectangle rectangle2;


    private int i = 0;
    private int j = 0;
    int x = 0;
    @FXML protected void onClick(ActionEvent event){





        Runnable r = ()->{
                Platform.runLater(()->{

                if (i == 0) {
                    rectangle = new Rectangle();
                    i ++;
                }
                rectangle.setWidth(50);
                rectangle.setHeight(100);

                System.out.println(rectangle.getX());

                if (i == 1) {
                    mainGroup.getChildren().add(rectangle);
                    i ++;
                }
                for (int i = 0; i < 100; i++){
                    rectangle.setX(rectangle.getX() + 1);
                }
            });
        };

        Runnable r2 = ()->{


            for (int i = 0; i < 100; i++){
                x += 1;

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e){

                }

                Platform.runLater(()->{

                    if (j == 0) {
                        rectangle2 = new Rectangle();
                        j ++;
                    }
                    rectangle2.setWidth(50);
                    rectangle2.setHeight(100);
                    rectangle2.setY(100);

                    System.out.println(rectangle2.getX());

                    if (j == 1) {
                        mainGroup.getChildren().add(rectangle2);
                        j ++;
                    }
                        rectangle2.setX(x);

                });

            }
        };

        Thread t = new Thread(r);
        Thread t2 = new Thread(r2);
        t.start();
        t2.start();

    }

}
