package crossing.util;

/**
 * Created by lisirrx on 5/2/17.
 */
public class Position extends TwoValueTuple{

    public Position(Position position) {
        super(position.getX(), position.getY());
    }
    public Position(double xPosition, double yPosition){
        super(xPosition, yPosition);
    }
}
