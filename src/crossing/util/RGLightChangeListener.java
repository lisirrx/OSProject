package crossing.util;

import crossing.Model.RGLight;

import java.util.EventListener;


public class RGLightChangeListener implements EventListener{

    private RGLight light;

    public RGLightChangeListener(RGLight light){
        this.light = light;
    }

    public boolean getSignal(){
        return light.getColor();
    }

}

