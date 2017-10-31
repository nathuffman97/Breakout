import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * @author Natalie Huffman
 */

public class Bubble extends Circle{
	private int numHitsToBreak;
	private boolean hasBroken = false;
	private int numHitsSoFar = 0;

	public Bubble(int x, int y, Paint c, int b, int rad){
		super(x, y, rad, c);
		numHitsToBreak= (c==Color.GRAY)? 1:b; //bombs take only 1 hit
	}
	
	public void hit(ArrayList<Paint> possibleColors){
		numHitsSoFar++;
		super.setFill(nextColor(possibleColors));
		if (numHitsSoFar==numHitsToBreak){ 
			hasBroken = true;
			super.setFill(Color.TRANSPARENT);
		}
	}
	
	private Paint nextColor(ArrayList<Paint> options){
		if (options.indexOf(super.getFill())+1 == options.size())
			return options.get(0);
		return options.get(options.indexOf(super.getFill())+1);
	}
	
	public boolean isHit(){
		return hasBroken;
	}
	
	public boolean match(Paint p){
		return p.equals(super.getFill());
	}
}
