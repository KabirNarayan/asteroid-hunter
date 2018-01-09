package pl.michalskrzypek.ah.objects;

import java.awt.*;

import pl.michalskrzypek.ah.main.AsteroidGameBoard;

@SuppressWarnings("serial")
public class SpaceShip extends SpaceObject {

	// Determines the speed the ship moves

	public final static double MAX_VELOCITY = 4;

	// Total lives of the ship
	private int lives = 5;

	// Get the board width and height
	private int gBWidth = AsteroidGameBoard.frameWidth;
	private int gBHeight = AsteroidGameBoard.frameHeight;

	// Center of space ship
	// private double centerX = gBWidth / 2 + 50, centerY = gBHeight / 2 - 50;

	// Will hold the x & y coordinates for the ship
	// Everything is based on coordinates from the center
	// It is done this way so that rotation works properly
	private static int[] polyXArray = { -20, 20, -20, -10, -20 };
	private static int[] polyYArray = { -15, 0, 15, 0, -15 };

	// Width and height of ship
	private int shipWidth = 40, shipHeight = 30;

	// Upper left hand corner of space ship
	private double uLeftXPos = getXCenter() + this.polyXArray[0];
	private double uLeftYPos = getYCenter() + this.polyYArray[0];

	// Defines if the ship should rotate
	private double rotationAngle = 0, movingAngle = 0;

	// Creates a new space ship with specified amount of lives
	public SpaceShip() {
		// Creates a Polygon by calling the super class Polygon
		super(polyXArray, polyYArray, 5);
		this.setXVelocity(2);
		this.setYVelocity(0);
		this.setWidth(shipWidth);
		this.setHeight(shipHeight);
		this.setXCenter(gBWidth / 2 + 50);
		this.setYCenter(gBHeight / 2 - 50);
		this.setOnScreen(true);
	}

	// Gets & sets ship's lives
	public int getLives() {
		return this.lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	// Once the ship hit an asteroid it loses one life
	public void takeLife() {
		this.lives--;
	}

	public void gainLife() {
		this.lives++;
	}

	public void increaseXPos(double incAmt) {
		this.setXCenter(this.getXCenter() + incAmt);
	}

	public void increaseYPos(double incAmt) {
		this.setYCenter(this.getYCenter() + incAmt);
	}

	// Gets & sets the x & y for upper left hand corner of ship
	public double getuLeftXPos() {
		return uLeftXPos;
	}

	public double getuLeftYPos() {
		return uLeftYPos;
	}

	public void setuLeftXPos(double xULPos) {
		this.uLeftXPos = xULPos;
	}

	public void setuLeftYPos(double yULPos) {
		this.uLeftYPos = yULPos;
	}

	// Set and increase the ship movement angle
	public void setMovingAngle(double moveAngle) {
		this.movingAngle = moveAngle;
	}

	public double getMovingAngle() {
		return movingAngle;
	}

	public void increaseMovingAngle(double moveAngle) {
		this.movingAngle += moveAngle;
	}

	// Calculate the ship angle of movement
	public double shipXMoveAngle(double xMoveAngle) {

		return (double) (Math.cos(xMoveAngle * Math.PI / 180));

	}

	public double shipYMoveAngle(double yMoveAngle) {

		return (double) (Math.sin(yMoveAngle * Math.PI / 180));

	}

	// Gets & increases or decreases the rotation angle of the ship
	public double getRotationAngle() {
		return rotationAngle;
	}

	public void setRotationAngle(double angle) {
		rotationAngle = angle;
	}

	public void increaseRotationAngle() {

		if (getRotationAngle() >= 355) {
			rotationAngle = 0;
		} else {
			rotationAngle += 5;
		}
	}

	// Decrease the rotation angle
	public void decreaseRotationAngle() {

		if (getRotationAngle() < 0) {
			rotationAngle = 355;
		} else {
			rotationAngle -= 5;
		}
	}

	// Bounds of the ship for collision detection
	public Rectangle getBounds() {

		return new Rectangle((int) getXCenter() - 14, (int) getYCenter() - 15, getWidth(), getHeight());

	}

	public double getShipNoseX() {
		return this.getXCenter() + Math.cos(rotationAngle * Math.PI / 180) * 14;
	}

	public double getShipNoseY() {
		return this.getYCenter() + Math.sin(rotationAngle * Math.PI / 180) * 15;
	}

	public void move() {

		// Increase the x origin value based on current velocity
		this.increaseXPos(this.getXVelocity());
		// If the ship goes off the board flip it to the other side of the board
		if (this.getXCenter() < 0) {
			this.setXCenter(gBWidth);

		} else if (this.getXCenter() > gBWidth) {
			this.setXCenter(0);
		}

		// Increase the y origin value based on current velocity
		this.increaseYPos(this.getYVelocity());

		// If the ship goes off the board flip it to the other side of the board
		if (this.getYCenter() < 0) {
			this.setYCenter(gBHeight);
		} else if (this.getYCenter() > gBHeight) {
			this.setYCenter(0);
		}

	}

}