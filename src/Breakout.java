import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Bubble shooter/pong combo game
 * 
 * @author Natalie Huffman
 */

public class Breakout extends Application{
	public static final int END_FONT_SIZE = 50;
	public static final int MAX_POWERUP_BOUNCES = 3;
	public static final int NUM_POWERUPS = 6;
	public static final int NUM_START_LIVES = 3;
	public static final int POWERUP_ODDS = 4;
	public static final int SCORE_CHANGE_AMOUNT = 10;
	public static final int SIZE = 400;
	public static final Paint BACKGROUND = Color.LIGHTGREEN;
	public static final Paint[] COLOR_OPTIONS = {Color.WHITE, Color.RED, Color.BLUE, Color.GREEN, 
			Color.YELLOW, Color.ORANGE, Color.BLACK, Color.PURPLE, Color.PINK, Color.BROWN, Color.WHITE};
	public static final int FRAMES_PER_SECOND = 60;
    public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
    public static final String BOMB_IMAGE= "bomb.png";
	
    private Group root = new Group();
	private Scene myScene;
	private Paddle myPaddle;
	private Ball myBall;
	private BubbleGroup myBubbles;
	private Text instructions = new Text(30,50,"Welcome to Breakout! Play by bouncing \nthe ball off the paddle so that it"
			+ " collides \nwith the bubbles. If the ball's color \nmatches the bubble's, the bubble will \npop. Some "
			+ "bubbles, once popped, will \ndrop powerups--catch them with the \npaddle to activate them. Grey bubbles \nwill, "
			+ "when hit, cause the surrounding \nbubbles to explode. Pop all the bubbles \nto advance to the next level. "
			+ "Make it \nthrough five levels to win! \n\nClick anywhere to continue");
	private Text levelText = new Text(30, 300, "Lvl: 1");
	private Text scoreText = new Text(30, 320, "Score: 0");
	private Text lifeText = new Text(30, 340, "Lives: 3");
	private Text endText = new Text(50, 200, "GAME OVER");
	
	private int level = 1;
	private Random rand = new Random();
	private int lives = NUM_START_LIVES;
	private int score = 0;
	private boolean startGame = false; //user gets to trigger start of game
	private boolean onInstructions = true; //to leave instructions
	//Int1 = randNum corresponding with particular powerup; Int2 = num of bounces (3 ends powerup)
	private HashMap<Integer, Integer> powerupBounces = new HashMap<Integer, Integer>();
	//holds onto powerups still visible on screen
	private ArrayList<Powerup> livePowerups = new ArrayList<Powerup>();
	private boolean catchBall = false; //for the powerup where the paddle catches the ball
	private SceneObject object = new SceneObject();
	
	@Override
	public void start(Stage s) throws Exception { 
		//method from ExampleBounce by Robert Duvall
		//https://coursework.cs.duke.edu/CompSci308_2017Fall/lab_bounce/blob/master/src/ExampleBounce.java
		Scene scene = initGame(SIZE, SIZE, BACKGROUND);
		s.setScene(scene);
		s.show();
		
		KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY),
				e -> play(SECOND_DELAY));
		Timeline animation = new Timeline();
		animation.setCycleCount(Timeline.INDEFINITE);
		animation.getKeyFrames().add(frame);
		animation.play();
	}

	private Scene initGame (int width, int height, Paint background) {
		//inspired by ExampleBounce 
		//https://coursework.cs.duke.edu/CompSci308_2017Fall/lab_bounce/blob/master/src/ExampleBounce.java
		myScene = new Scene(root, width, height, background);
		
		myScene.setOnMouseClicked(e -> handleMouseClickInput());
		
		instructions.setFont(Font.font(Font.getDefault().toString(), 20));
		root.getChildren().add(instructions);
		
		return myScene;
	}
	
	private void gameScene(){
		myPaddle = new Paddle(SIZE,SIZE);
		createBubbles(); //initializes myBubbles (bubblegroup obj)
		//the last parameter is to situate the ball so that it's above the paddle-- paddle is height*2 above
		//bottom, and ball centerY is that + ball radius
		myBall = new Ball(SIZE,SIZE, myBubbles.calcNextColor(), Paddle.HEIGHT*2);
		
		myScene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
		myScene.setOnMouseMoved(e -> handleMouseMoveInput(e.getSceneX()));
		
		addToRoot(); //adds all screen objects to the group	
	}
	
	private void addToRoot(){
		//idea from ExampleBounce
		//https://coursework.cs.duke.edu/CompSci308_2017Fall/lab_bounce/blob/master/src/ExampleBounce.java
		root.getChildren().add(myPaddle);
		root.getChildren().add(myBall);
		for (Bubble b:myBubbles)
			root.getChildren().add(b);
		root.getChildren().add(scoreText);
		root.getChildren().add(levelText);
		root.getChildren().add(lifeText);
	}
	
	private void createBubbles(){
		//decides how many colors to include in each level
		int colors = level>=COLOR_OPTIONS.length? COLOR_OPTIONS.length:rand.nextInt(level+2)+1;
		
		ArrayList<Paint> pick = new ArrayList<Paint>();
		//^ randomly selected colors from main array; length determined above
		
		for (int i = 0; i < colors; i ++)
		{
			int num = rand.nextInt(COLOR_OPTIONS.length);
			if (!pick.contains(COLOR_OPTIONS[num]))
				pick.add(COLOR_OPTIONS[num]);
			else
				i--;
		}
		
		myBubbles = new BubbleGroup(level, pick, SIZE);
	}
	
	private void play(double elapsedTime){
		if (startGame){//if the user has released the ball already
			myBall.update(elapsedTime);
			
			if (myBall.checkWallCollision(SIZE)) //returns true if
				reset(); 						//ball falls off bottom of screen
			
			powerupUpdate(elapsedTime); //update pos, check for powerup/paddle/collision
			
			if (myBall.paddleCollision(myPaddle, myBubbles)){ //check for ball/paddle collision
				if (!catchBall)
					powerupBounce(); //reduce how long current powerups are in play for
				else //if the powerup where the paddle catches the ball is in effect
					catchBall();
			}
			
			//perform bubble collision, check if all bubbles are gone
			if (checkBubbleCollision())
				nextLevel();
		}
	}

	private void catchBall() {
		if (object.paddleCollision(myPaddle, myBall))
		{
			myBall.setCenterX(myPaddle.getX()+myPaddle.getWidth()/2);
			myBall.setCenterY(myPaddle.getY()-Ball.RADIUS);
			myBall.catchBall();
			startGame = false; //this is basically the ball/powerup collision method except collision
			catchBall=false; //causes the ball to stop moving and the game "pauses"
		}
	}

	private void powerupUpdate(double elapsedTime) {
		Iterator<Powerup> iter = livePowerups.iterator();
		while (iter.hasNext()){
			Powerup temp = iter.next();
			temp.update(elapsedTime); //move powerup
			if (temp.paddleCollision(myPaddle)){
				root.getChildren().remove(temp);
				caughtPowerup(temp.getType()); //add to hashmap
				if (temp.getType()==1) return; //this is the start over one--all powerups are gone
				iter.remove();
			}
		}
	}

	private boolean checkBubbleCollision() {
		boolean win = true; //is game over? (all bubbles popped)
		for (Bubble b: myBubbles)
		{
			if (b.isHit()) continue; //if the bubble has already been hit (is transparent)
			
			if (dist(myBall.getCenterX(), myBall.getCenterY(), b.getCenterX(), b.getCenterY())<=
					2*myBall.getRadius()){
				if (b.getFill()==Color.GRAY) //all explosion bubbles are grey
					for(Bubble bubble: myBubbles.explode(b)) //explode returns an arraylist of bordering bubbles
						bubbleHit(bubble);
				else if (myBall.matchBubble(b))
					bubbleHit(b);
				myBall.hit(); //we bounce off it
			}
			win = false;
		}
		return win;
	}
	
	private void bubbleHit(Bubble b){
		scoreChange();
		myBubbles.hit(b);//bubble is set as "hit"
		if (b.isHit()&&rand.nextInt(POWERUP_ODDS)==0) //include first part so only if bubble pops on this hit
			genPowerup(b); //25% chance of getting a powerup
	}
	
	private void scoreChange(){
		score+=SCORE_CHANGE_AMOUNT;
		scoreText.setText("Score: " + score);
	}
	
	private void lifeChange(int l){
		lives = l;
		if (lives == 0) endGame();
		
		lifeText.setText("Lives: "+lives);
		lifeText.setFill(lives==1? Color.RED:Color.BLACK);
	}
	
	private void lvlChange(){
		level++;
		levelText.setText("Level: "+level);
	}
	
	private void nextLevel(){
		lifeChange(NUM_START_LIVES+1); //+1 bc reset removes a life
		reset();
		lvlChange();
		
		regen(); //redo bubbles
		if (level==6) endGame();
	}
	
	private void regen(){
		root.getChildren().clear();
		livePowerups.clear();
		powerupBounces.clear();
		
		createBubbles(); //redo scene
		
		addToRoot();
		
		myBall.nextColor(myBubbles); //make sure ball is a viable color
	}
	
	private void reset(){
		lifeChange(lives-1); //assume you missed the ball...lives decreases
		myPaddle.reset();
		myBall.reset();
		startGame = false;
	}
	
	private double dist(double x1, double y1, double x2, double y2){
		//distance formula for checking bubble/ball collision
		return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
	}
	
	private void handleKeyInput (KeyCode code) {
		//concept from ExampleBounce
		//https://coursework.cs.duke.edu/CompSci308_2017Fall/lab_bounce/blob/master/src/ExampleBounce.java
		
		if (code== KeyCode.P) //pause
			myBall.pause();
		if (code== KeyCode.C)
			myBall.nextColor(myBubbles); //cycle through colors
		if (code== KeyCode.S)
		{//start over entirely
			lifeChange(4); //bc reset will remove 1 life
			reset();
			regen();
		}
		if (code== KeyCode.SPACE)
		{//reset ball and paddle
			lifeChange(lives+1); //bc reset will remove 1 life
			reset();
		}
		if (code==KeyCode.NUMPAD0) //go to next level
			if (lives!=0 && level!=6)
				nextLevel();
	}
	
	private void handleMouseMoveInput(double X){
		if (X>myPaddle.getWidth()/2 && X < SIZE-myPaddle.getWidth()/2)
		{//make sure paddle can't go off the edge
			//move paddle with mouse
			myPaddle.update(X);
			if (!startGame) //if the game hasn't started, ball moves too
				myBall.setCenterX(myPaddle.getX()+myPaddle.getWidth()/2);
		}
	}
	
	private void handleMouseClickInput(){
		if (onInstructions)
		{
			onInstructions = false;
			root.getChildren().clear();
			gameScene(); //draw actual game
		}
		else if (!startGame) //clicking starts the game
		{
			startGame=true;
	    	myBall.start();
		}
	}
	
	private void genPowerup(Bubble b){
		int x = rand.nextInt(NUM_POWERUPS);
		
		Powerup p = new Powerup(b.getCenterX(), b.getCenterY(), x);
		livePowerups.add(p);
		root.getChildren().add(p);
	}
	
	private void caughtPowerup(int x){
		if (!powerupBounces.containsKey(x) || powerupBounces.get(x)==MAX_POWERUP_BOUNCES){
			if (x==0) //actually enact powerup
				myPaddle.flipDirection(); //paddle goes in wrong direction
			if (x==1)
			{
				for (Integer i: powerupBounces.keySet()) //get rid of current powerups
						powerupBounces.put(i, 2);
				powerupBounce();
				regen(); //bubbles start over
			}
			if (x==2)
				lifeChange(lives+1); //new life
			if (x==3)
				myBall.changeSpeed();
			if (x==4) //paddle doubles in size
				myPaddle.setWidth(myPaddle.getWidth()*2);
			if (x==5) //paddle catches ball
				catchBall=true;
			
			if (x==0 || x==3 || x==4) //only things that have to be undone
				powerupBounces.put(x, 0); //into hashmap
		}
	}
	
	private void powerupBounce(){
		for (Integer i: powerupBounces.keySet())
		{
			if (powerupBounces.get(i) == MAX_POWERUP_BOUNCES) continue; //powerup has eneded
			if (powerupBounces.get(i) == MAX_POWERUP_BOUNCES-1) //powerup ends with this bounce
			{
				if (i==0) //powerups that must be undone get undone
					myPaddle.flipDirection();
				if (i==3)
					myBall.changeSpeed();
				if (i==4)
					myPaddle.setWidth(myPaddle.getWidth()/2);
			}
			powerupBounces.put(i, powerupBounces.get(i)+1); //add tally to hashmap
		}
	}
	
	private void endGame(){ //end game screen
		root.getChildren().clear();
		myScene.setFill(Color.BLACK);
		endText.setFill(Color.WHITE);
		endText.setFont(Font.font(Font.getDefault().toString(), FontWeight.BOLD, END_FONT_SIZE));
		endText.setText(lives!=0? "  YOU WIN!\n    Score: "+score: "GAME OVER\n   Score: "+score);
		root.getChildren().add(endText);
		root.getChildren().add(levelText);
	}
	
	public static void main (String[] args) {
        //from ExampleBounce
		//https://coursework.cs.duke.edu/CompSci308_2017Fall/lab_bounce/blob/master/src/ExampleBounce.java
		launch(args);
    }
}
