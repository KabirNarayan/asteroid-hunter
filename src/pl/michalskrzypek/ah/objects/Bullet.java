package pl.michalskrzypek.ah.objects;
import java.awt.Polygon;
import java.awt.Rectangle;

import pl.michalskrzypek.ah.main.AsteroidGameBoard;

public class Bullet extends Polygon implements SpaceObject{

	// Get the board width and height
	private int gBWidth = AsteroidGameBoard.frameWidth;
	private int gBHeight = AsteroidGameBoard.frameHeight;

	// Center of bullet
	private double centerX = 0, centerY = 0;

	// Will hold the x & y coordinates for the bullet
	// Everything is based on coordinates from the center
	private static int[] polyXArray = { -3, 3, 3, -3, -3 };
	private static int[] polyYArray = { -3, -3, 3, 3, -3 };

	// Width and height of bullet
	private int bulletWidth = 6, bulletHeight = 6;

	// Keep track of whether bullet is on screen
	private boolean onScreen = false;

	// The angle the bullet moves on the screen
	private double movingAngle = 0;

	// Determines how quickly the bullet moves on
	// its assigned path
	private double xVelocity = 5, yVelocity = 5;

	public Bullet(double shipNoseX, double shipNoseY, double movingAngleOfShip) {

		// Creates a Polygon by calling the super class Polygon
		super(polyXArray, polyYArray, 5);

		// Defines the center based on the vectors of
		// the ships nose. movingAngle is the same as ship
		this.centerX = shipNoseX;
		this.centerY = shipNoseY;
		this.movingAngle = movingAngleOfShip;

		this.onScreen = true;
		
		this.setXVelocity(this.bulletXMoveAngle(this.movingAngle) * 10);
		this.setYVelocity(this.bulletYMoveAngle(this.movingAngle) * 10);

	}

	public void setOnScreen (boolean onScr) {
		this.onScreen = onScr;
	}
	
	public boolean getOnScreen () {
		return this.onScreen;
	}

	// Gets, sets, the bullet velocity

	public double getXVelocity() {
		return xVelocity;
	}

	public double getYVelocity() {
		return yVelocity;
	}

	public void setXVelocity(double xVel) {
		this.xVelocity = xVel;
	}

	public void setYVelocity(double yVel) {
		this.yVelocity = yVel;
	}

	// Gets & sets the x & y for upper left hand corner of ship

	public int getWidth() {
		return bulletWidth;
	}

	public int getHeight() {
		return bulletHeight;
	}

	// Set and increase the bullet movement angle

	public void setMovingAngle(double moveAngle) {
		this.movingAngle = moveAngle;
	}

	public double getMovingAngle() {
		return movingAngle;
	}

	// Gets & sets the values for the x & y center of bullet

	public double getXCenter() {
		return centerX;
	}

	public double getYCenter() {
		return centerY;
	}

	public void setXCenter(double xCent) {
		this.centerX = xCent;
	}

	public void setYCenter(double yCent) {
		this.centerY = yCent;
	}

	public void changeXPos(double incAmt) {
		this.centerX += incAmt;
	}

	public void changeYPos(double incAmt) {
		this.centerY += incAmt;
	}
	
	
	// Artificial rectangle that is used for collision detection
	public Rectangle getBounds() {

		return new Rectangle((int) this.getXCenter() - 3,
				(int) this.getYCenter() - 3, getWidth(), getHeight());

	}

	// Calculate the bullet angle of movement
	public double bulletXMoveAngle(double xMoveAngle) {

		return (double) (Math.cos(xMoveAngle * Math.PI / 180));

	}

	public double bulletYMoveAngle(double yMoveAngle) {

		return (double) (Math.sin(yMoveAngle * Math.PI / 180));

	}

	public void move() {

		// Increase the x origin value based on current velocity
		this.changeXPos(this.getXVelocity());

		if (this.getXCenter() < 0 || this.getXCenter() > gBWidth) {
			this.onScreen = false;
			AsteroidGameBoard.currentBullets--;
		}

		// Increase the y origin value based on current velocity
		this.changeYPos(this.getYVelocity());

		if (this.getYCenter() < 0 || this.getYCenter() > gBHeight) {

			this.onScreen = false;
			AsteroidGameBoard.currentBullets--;
		}
	}

	//velocity remains the same for a bullet so i dont override following methods
	@Override
	public void increaseXVelocity(double incAmt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decreaseXVelocity(double incAmt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseYVelocity(double incAmt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decreaseYVelocity(double incAmt) {
		// TODO Auto-generated method stub
		
	}
}