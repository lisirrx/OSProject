package sample.Model;

import java.util.EventListener;
import java.util.EventObject;


public class RGLightChangeListener implements EventListener{

    private RGLightChangeEvent event;

    public RGLightChangeListener(RGLightChangeEvent event){
        this.event = event;
    }

    public boolean getSignal(){
        RGLight light = (RGLight)event.getSource();
        return light.getColor();
    }

}

class RGLightChangeEvent extends EventObject{

    public RGLightChangeEvent(Object light){
        super(light);
    }
}