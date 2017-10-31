import java.util.ArrayList;
import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Creates and manipulates bubble objects
 * Also tracks explosion bubbles, a subset of bubbles that are all grey and take only one hit
 * to pop. When they pop, they do one hit of damage on the bubbles immediately to their left
 * and right. The exception is bubbles on the left or right edge of the pyramid, which by dint 
 * of their positioning have a bubble on only one side. These bubbles then pop only the bubble 
 * in their proximity.
 *  
 * Extends ArrayList
 * 
 * This class effectively uses constants to cut down on numbers loose in the code, uses
 * private instance variables (demonstrating encapsulation), has no redundant code and code
 * is easy to read and understand. Methods and variables have appropriate, understandable names,
 * and this class protects Bubble information by acting as the only class to access them. It 
 * also holds onto the colors used in each level, the only class to do so.
 * 
 * @author Natalie Huffman
 */

public class BubbleGroup extends ArrayList<Bubble>{
	public static final int BUBBLE_RADIUS = 10;
	public static final int BUBBLE_OFFSET = 15;
	public static final int MAX_ROWS = 13;
	private static final long serialVersionUID = 1L; //attached to ArrayList extension
	private Random rand = new Random();
	private int colorDex = 0; 
	private ArrayList<Paint> possibleColors;
	private int sceneSize;
	private int pyramidRows;
	private int bombAmount;
	
	
	/**
	 * Constructor-- sets instance variables and makes call to create bubbles
	 * 
	 * @param level		the level the user is on
	 * @param paints	the colors it's possible for the Bubbles of this level to be	
	 * @param size		the size of the scene window	
	 */
	public BubbleGroup(int level, ArrayList<Paint> paints, int size){
		super();
		possibleColors = paints;
		sceneSize = size;
		pyramidRows = level+5+rand.nextInt(3);
		if (pyramidRows>MAX_ROWS) pyramidRows=MAX_ROWS; 
		//there can't be more than 13 rows bc theyll spill off the edges of the scene
		bombAmount = rand.nextInt(4)+1;
		
		genBubbles(level);
	}
	
	/**
	 * Creates and positions all of the Bubble objects in an upside-down pyramid shape
	 * Bubble takes five parameters: XCoordinate, YCoordinate, Color, Number of Hits Needed to Break, and Radius
	 * 
	 * @param level		level of the game that the player is on
	 */
	private void genBubbles(int level){
		for (int i = pyramidRows; i > 0; i--){
			for (int j = 0; j <i; j++){
				int xCoordinate = sceneSize/2-(i-1)*BUBBLE_OFFSET+j*2*BUBBLE_OFFSET;
				int yCoordinate = BUBBLE_RADIUS*(2*(pyramidRows-i)+1);
				
				super.add(new Bubble(xCoordinate, yCoordinate, bubbleColorChoice(),
						bubbleStrength(level), BUBBLE_RADIUS));
			}
		}
	}
	
	/**
	 * Chooses random color for Bubble, including possibility of grey for explosion bubble
	 * 
	 * @return	color of Bubble-- either grey for explosion bubble or random color from those available
	 */
	private Paint bubbleColorChoice(){
		if (bombAmount>0 && rand.nextInt(10)==0)
		{
			bombAmount--;
			return Color.GRAY;
		}
		return possibleColors.get(rand.nextInt(possibleColors.size()));
	}
	
	/**
	 * Chooses randomly how many hits the bubble needs to break
	 * (note that the number cannot be higher than the total number of colors)
	 * 
	 * @param level		level of the game that the player is on
	 * @return			how many hits the bubble needs to break
	 */
	private int bubbleStrength(int level){
		int randNum = rand.nextInt(10);
		if (randNum < 6 || possibleColors.size()==1) return 1;
		if (randNum < 9 || possibleColors.size()==2) return 2;
		return 3;
	}
	
	/**
	 * Cycles through the possible colors so the Ball can change color
	 * 
	 * @return next color, based on the last one used
	 */
	public Paint calcNextColor(){
		colorDex = (colorDex+1==possibleColors.size())? 0: colorDex+1;
		
		return possibleColors.get(colorDex);
	}
	
	
	/**
	 * Performs the .hit call on a bubble when the ball collides with it
	 * 
	 * @param bubble	a bubble hit by the ball
	 */
	public void hit(Bubble bubble){
		bubble.hit(possibleColors);
	}

	/**
	 * Composes and returns an ArrayList of the Bubbles popped by a particular explosion Bubble
	 * (note that a bubble on the edge of the pyramid will pop only one other bubble) 
	 * 
	 * @param bubble	a bubble hit by the ball
	 * @return	the ball hit plus the balls directly to its left and its right
	 */
	public ArrayList<Bubble> explode(Bubble bubble){
		ArrayList<Bubble> surrounding = new ArrayList<Bubble>();
		surrounding.add(bubble);
		int index = super.indexOf(bubble);
		
		if (!isEdge(index, -1))
			valid(index+1, surrounding);
		if (!isEdge(index, 0))
			valid(index-1, surrounding);
		
		return surrounding;
	}
	
	/**
	 * Checks that the bordering bubble exists and that it hasn't already been popped
	 * 
	 * @param index			The index of a bubble to the left/right of the explosion bubble
	 * @param surrounding	The ArrayList holding bubbles to by popped
	 */
	private void valid(int index, ArrayList<Bubble> surrounding){
		if (index!= -1 && index<super.size() && !super.get(index).isHit())
			surrounding.add(super.get(index));
	}
	
	
	/**
	 * Checks if the particular bubble is on an edge, as then it will only pop one other bubble
	 * 
	 * @param index		The index of the bubble
	 * @param edge		The starting point for the counting: 0 for left side, -1 for right side
	 * @return			Whether or not the bubble is on either edge of the pyramid
	 */
	private boolean isEdge(int index, int edge){
		for (int i = pyramidRows; i>0; i--){
			if (edge==index) return true;
			edge += i;
		}
		
		return edge==index;
	}

}