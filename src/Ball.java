import java.util.Random;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * @author Natalie Huffman
 */

public class Ball extends Circle{
	public static final int MEDIAN_HSPEED = 80;
	public static final int BOTTOM_HSPEED = 50;
	public static final int TOP_HSPEED = 150;
	public static final int START_SPEED = -350;
	public static final int RADIUS = 10;
	public static final double DIST_FROM_BOTTOM = 30;
	private int sceneWidth;
	private int sceneHeight;
	private int hSpeed = 0;
	private int vSpeed = 0;
	private Random rand = new Random();
	private int[] ballVel = new int[2]; //so you can pause & restart @ same speed
	private double changeSpeed = .5; //so you can slow down the speed of the ball with powerup
	private SceneObject object = new SceneObject();
	
	public Ball(int w, int h, Paint c, int paddleOffset){
		super(w/2, h-paddleOffset-RADIUS, RADIUS, c);
		sceneWidth = w;
		sceneHeight = h;
	}
	
	public void nextColor(BubbleGroup b){
		super.setFill(b.calcNextColor());
	}
	
	public void update(double elapsedTime){
		object.update(this, "X", hSpeed, elapsedTime);
		object.update(this, "Y", vSpeed, elapsedTime);
	}
	
	public boolean checkWallCollision(int size){
		if (super.getCenterX()>=size-super.getRadius()||super.getCenterX()-super.getRadius()<=0)
			hSpeed*=-1; //else change direction
		if (super.getCenterY()-super.getRadius()<=0)
			hit();
		
		if (super.getCenterX()-super.getRadius()<0) super.setCenterX(super.getRadius()); 		 //double checking
		if (super.getCenterY()-super.getRadius()<0) super.setCenterY(super.getRadius()); 		 //so it doesnt get stuck
		if (super.getCenterX()-super.getRadius()>size) super.setCenterX(super.getRadius()+size); //anywhere
		
		return (super.getCenterY()>=size+super.getRadius()); //if falls off bottom of screen
	}
	
	public boolean paddleCollision(Paddle p, BubbleGroup b){
		if (object.paddleCollision(p, this))
		{
			hit(b.calcNextColor(), super.getCenterX(), p.getX());
			super.setCenterY(p.getY()-super.getRadius());
			return true; //did hit (used for powerup updates)
		}
		return false;
	}
	
	public void start(){
		vSpeed = START_SPEED;
	}
	
	public void reset(){
		catchBall();
		object.reset(this, sceneWidth, sceneHeight, DIST_FROM_BOTTOM);
	}
	
	public void catchBall(){
		vSpeed=START_SPEED;
		hSpeed=0;
	}
	
	public void changeSpeed(){
		vSpeed*=changeSpeed;
		hSpeed*=changeSpeed;
		
		//if it just slowed by 1/2, the next time we want it to double (back to normal)
		changeSpeed = (changeSpeed==.5) ? 2:.5;
	}
	
	private void hit(Paint p, double bX, double pX){ //ball hits paddle
		if (hSpeed != 0){ //we dont want this to happen if we're in the middle of a reset so that hSpeed will stay 0 
			hit();
			if (hSpeed>TOP_HSPEED) hSpeed = MEDIAN_HSPEED; //correction in case it goes too far in one direction
			if (hSpeed<BOTTOM_HSPEED) hSpeed = MEDIAN_HSPEED;
			hSpeed*=-randDir();
			super.setFill(p);
		}
	}
	
	public void hit(){ //ball hits bubble
		vSpeed*=-1;
		if (hSpeed == 0) hSpeed = MEDIAN_HSPEED*randDir(); //first hit
		hSpeed+=randDir()*20*rand.nextInt(4);
	}
	
	private int randDir(){
		//randomly selects -1 or 1, broken out bc i needed it so often
		return (rand.nextInt(2)==0)? -1:1;
	}
	
	public boolean matchBubble(Bubble b){ //if color of bubble and ball match
		return b.match(super.getFill());
	}
	
	public void pause(){
		if (vSpeed!=0){ //store velocities so unpausing is possible
			ballVel[0]=hSpeed;
			ballVel[1]=vSpeed;
			hSpeed = 0;
			vSpeed = 0;
		}
		else
		{
			hSpeed = ballVel[0];
			vSpeed = ballVel[1];
		}
	}

}
