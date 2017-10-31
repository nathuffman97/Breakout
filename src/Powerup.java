import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Creates physical on-screen powerups that can be caught by the paddle
 * 
 * Assumes that constructor is passed a valid dex value
 * Assumes that paddle has been instantiated
 * 
 * Powerups in order: paddle flip, start over, extra life, slow ball, long paddle, catch
 * 
 * This class effectively uses constants to cut down on numbers loose in the code, uses
 * private instance variables (demonstrating encapsulation), has no redundant code and code
 * is easy to read and understand, checks for invalid parameters, methods and variables have 
 * appropriate, understandable names, and uses a SceneObject object to delegate code that other
 * classes use so as to eliminate redundancies
 * 
 * @author Natalie Huffman
 */

public class Powerup extends Rectangle{
	public static final int SIDE_LENGTH = 20;
	public static final Color[] COLORCODING = {Color.RED, Color.ORANGE,
			Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE};
	public static final int SPEED= 30;
	private int powerupIndex;
	private SceneObject object = new SceneObject();
	
	/**
	 * Constructor-- creates a Rectangle object and sets color
	 * 
	 * @param x 	X-coordinate of powerup location
	 * @param y 	Y-coordinate of powerup location
	 * @param dex 	Index of COLORCODING; each index is associated with a powerup
	 * @throws IllegalArgumentException if dex is not within the bounds of COLORCODING
	*/
	public Powerup(double x, double y, int dex){
		super(x,y,SIDE_LENGTH,SIDE_LENGTH);
		
		if (dex<0 || dex>= COLORCODING.length)
			throw new IllegalArgumentException();
		
		powerupIndex = dex;
		super.setFill(COLORCODING[powerupIndex]);
		
		
	}
	
	/**
	 * Allows other classes to use the same number ordering of powerups
	 * 
	 * @return the index associated with this powerup
	 */
	public int getType(){
		return powerupIndex;
	}
	
	
	/**
	 * Updates the position of the powerup rectangle, which moves downwards
	 * 
	 * @param elapsedTime
	 */
	public void update(double elapsedTime){
		object.update(this, "Y", SPEED, elapsedTime);
	}
	
	
	/**
	 * 
	 * @param paddle 
	 * @return whether or not the powerup rectangle has hit the paddle
	 */
	public boolean paddleCollision(Paddle paddle){
		return object.paddleCollision(paddle, this);
	}
}
