package sample.util;

/**
 * Created by user on 5/2/17.
 */
public class Postion  extends TwoValueTuple{

    public Postion(Postion postion) {
        super(postion.x, postion.y);
    }
    public Postion(double xPostion, double yPostion){
        super(xPostion, yPostion);
    }
}
