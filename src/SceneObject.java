/**
 * @author Natalie Huffman
 * Contains methods shared by multiple game objects
 */

public class SceneObject {
	
	public boolean paddleCollision(Paddle paddle, Object obj){ //check for collision with paddle
		if (obj instanceof Powerup){
			Powerup powerup = (Powerup) obj;
			return collisionCheck(paddle.getX(), paddle.getWidth(), powerup.getX(), powerup.getWidth()) &&
					collisionCheck(paddle.getY(), paddle.getHeight(), powerup.getY(), powerup.getHeight());
		}
		
		Ball ball = (Ball) obj;
		return collisionCheck(paddle.getX(), paddle.getWidth(), ball.getCenterX(), ball.getRadius()) &&
					collisionCheck(paddle.getY(), paddle.getHeight(), ball.getCenterY(), ball.getRadius());
	}
	
	private boolean collisionCheck(double coor1, double dim1, double coor2, double dim2){
		return Math.abs(coor1+dim1/2-coor2)<=(dim1/2+dim2);
	}
	
	public void update(Object obj, String dir, int speed, double elapsedTime){
		if (dir.equals("Y")) updateY(obj, speed*elapsedTime);
		else updateX((Ball)obj, speed*elapsedTime);
	}
	
	private void updateX(Ball ball, double update){
		ball.setCenterX(ball.getCenterX()+update); //only the ball has horizontal movement
	}
	
	private void updateY(Object obj, double update){
		if (obj instanceof Powerup){
			Powerup powerup = (Powerup) obj;
			powerup.setY(powerup.getY()+update);
		}
		else if (obj instanceof Ball){
			Ball ball = (Ball) obj;
			ball.setCenterY(ball.getCenterY()+update);
		}
		
	}
		
	public void reset(Object obj, double sceneWidth, double sceneHeight, double offset){
		double xCoordinate = sceneWidth/2;
		double yCoordinate = sceneHeight-offset;
		
		if (obj instanceof Ball)
		{
			Ball ball = (Ball) obj;
			ball.setCenterX(xCoordinate);
			ball.setCenterY(yCoordinate);
		}
		else if (obj instanceof Paddle)
		{
			Paddle powerup = (Paddle) obj;
			powerup.setX(xCoordinate);
			powerup.setY(yCoordinate);
		}
	}
}
