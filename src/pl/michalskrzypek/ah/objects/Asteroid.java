package pl.michalskrzypek.ah.objects;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import pl.michalskrzypek.ah.main.AsteroidGameBoard;
import pl.michalskrzypek.ah.main.Collision;

public class Asteroid extends Polygon {

	private static double speed;
	private boolean onScreen;
	private static int[] polygonXCoordinates = { -20, -10, -1, 0, 10, 8, 20, 4, -2,
			-10, -20 };
	private static int[] polygonYCoordinates = { -5, -13, -10, -20, -7, -3, 5, 20, 8,
			12, -5 };
private int width = 40;
private int height = 40;

	private double xVelocity = 0; 
	private double yVelocity = 0;

private static int howMany = 0;
	private int which = 0;
	
	public Asteroid(int[] polygonXCoordinates, int[] polygonYCoordinates,
			int numberOfCorners) {
		super(polygonXCoordinates, polygonYCoordinates, numberOfCorners);

		this.xVelocity = (Math.random() * speed) + 1;
		this.yVelocity = (Math.random() * speed) + 1;
		onScreen = true;
	}

	//Bounds for collision detection
	public Rectangle getBounds() {
		return new Rectangle(this.xpoints[0], this.ypoints[3], 40, 30);
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}

	public void setOnScreen(boolean bool){
		this.onScreen = bool;
	}
	
	public boolean getOnScreen(){
		return this.onScreen;
	}
	
	public void setXVelocity(double xVel){
		this.xVelocity = xVel;
	}
	
	public void setYVelocity(double yVel){
		this.yVelocity = yVel;
	}
	
	public double getXVelocity(){
		return this.xVelocity;
	}
	
	public double getYVelocity(){
		return this.yVelocity;
	}
	
	public void increaseXVelocity(double incAmt) {
		this.xVelocity += incAmt;
	}
	
public void decreaseXVelocity(double incAmt) {
	this.xVelocity -= incAmt;
	}

public void increaseYVelocity(double incAmt) {
	this.yVelocity += incAmt;
}

public void decreaseYVelocity(double incAmt) {
	this.yVelocity -= incAmt;
}

public double asteroidXMoveAngle(double xMoveAngle) {

	return (double) (Math.cos(xMoveAngle * Math.PI / 180));

}

public double asteroidYMoveAngle(double yMoveAngle) {

	return (double) (Math.sin(yMoveAngle * Math.PI / 180));

}

	
	public void move() {

		if (super.xpoints[0] <= 0
				|| super.xpoints[6] >= AsteroidGameBoard.frameWidth) {
			xVelocity = -xVelocity;
		}

		if (super.ypoints[3] <= 0
				|| super.ypoints[7] >= AsteroidGameBoard.frameHeight) {
			yVelocity = -yVelocity;
		}

		for (int i = 0; i < super.xpoints.length; i++) {
			super.xpoints[i] += xVelocity;
		}

		for (int i = 0; i < super.ypoints.length; i++) {
			super.ypoints[i] += yVelocity;
		}
	}
	

	public static int[] getInitialXPosition(int randomInitialXPosition) {
		int[] tempXPosition = (int[]) polygonXCoordinates.clone();
		for (int i = 0; i < tempXPosition.length; i++) {
			tempXPosition[i] += randomInitialXPosition;
		}

		return tempXPosition;
	}

	public static int[] getInitialYPosition(int randomInitialYPosition) {
		int[] tempYPosition = (int[]) polygonYCoordinates.clone();
		for (int i = 0; i < tempYPosition.length; i++) {
			tempYPosition[i] += randomInitialYPosition;
		}
		return tempYPosition;
	}

	public static double getSpeed() {
		return speed;
	}

	public static void setSpeed(double theSpeed) {
		speed = theSpeed;
	}

}
