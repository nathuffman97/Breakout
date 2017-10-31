import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Natalie Huffman
 */

public class Paddle extends Rectangle{
	public static final int MOVE_AMOUNT = 200;
	public static final int HEIGHT = 10;
	public static final int WIDTH = 50;
	private int direction = 1; //really just for the flip powerup
	private int sceneWidth;
	private int sceneHeight;
	private SceneObject object = new SceneObject();
	
	public Paddle(int w, int h){
		super(w/2-WIDTH/2,h-HEIGHT*2,WIDTH,HEIGHT);
		sceneWidth = w;
		sceneHeight = h;
	}
	
	public void update(double X){
		super.setX(MOVE_AMOUNT+direction*(X-MOVE_AMOUNT)-super.getWidth()/2);
	}
	
	public void flipDirection(){//when you get the flip powerup
		super.setFill(super.getFill()==Color.BLACK? Color.RED:Color.BLACK);
		
		direction*=-1;
	}
	
	public void reset(){
		object.reset(this, sceneWidth-WIDTH, sceneHeight, HEIGHT*2);
	}
}
