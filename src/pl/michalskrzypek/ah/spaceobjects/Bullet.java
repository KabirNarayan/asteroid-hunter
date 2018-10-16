package pl.michalskrzypek.ah.spaceobjects;

import java.awt.Rectangle;

import pl.michalskrzypek.ah.gui.AsteroidGameBoard;
import pl.michalskrzypek.ah.spaceobjects.common.SpaceObject;

public class Bullet extends SpaceObject {

	public static final int MAX_BULLETS = 5;
	public static int numberOfBullets;
	private static final int BULLET_WIDTH = 6;
	private static final int BULLET_HEIGHT = 6;
	// Will hold the x & y coordinates for the bullet
	// Everything is based on coordinates from the center
	private double movingAngle = 0;
	private static int[] polyXArray = { -3, 3, 3, -3, -3 };
	private static int[] polyYArray = { -3, -3, 3, 3, -3 };
	private int gBWidth = AsteroidGameBoard.FRAME_WIDTH;
	private int gBHeight = AsteroidGameBoard.FRAME_HEIGHT;

	public Bullet(double shipNoseX, double shipNoseY, double movingAngleOfShip) {
		super(polyXArray, polyYArray, 5);
		this.setXCenter(shipNoseX);
		this.setYCenter(shipNoseY);
		this.movingAngle = movingAngleOfShip;
		this.setOnScreen(true);
		this.setWidth(BULLET_WIDTH);
		this.setHeight(BULLET_HEIGHT);
		this.setXVelocity(this.getBulletXMoveAngle(this.movingAngle) * 10);
		this.setYVelocity(this.getBulletYMoveAngle(this.movingAngle) * 10);
	}

	public void move() {
		this.changeXPos(this.getXVelocity());
		if (this.getXCenter() < 0 || this.getXCenter() > gBWidth) {
			this.setOnScreen(false);
			numberOfBullets--;
		}

		this.changeYPos(this.getYVelocity());
		if (this.getYCenter() < 0 || this.getYCenter() > gBHeight) {
			this.setOnScreen(false);
			numberOfBullets--;
		}
	}
	
	public void changeXPos(double incAmt) {
		this.setXCenter(this.getXCenter() + incAmt);
	}

	public void changeYPos(double incAmt) {
		this.setYCenter(this.getYCenter() + incAmt);
	}

	public void setMovingAngle(double moveAngle) {
		this.movingAngle = moveAngle;
	}

	public double getMovingAngle() {
		return movingAngle;
	}

	// Artificial rectangle that is used for collision detection
	public Rectangle getBounds() {
		return new Rectangle((int) this.getXCenter() - 3, (int) this.getYCenter() - 3, getWidth(), getHeight());
	}

	public double getBulletXMoveAngle(double xMoveAngle) {
		return (double) (Math.cos(xMoveAngle * Math.PI / 180));
	}

	public double getBulletYMoveAngle(double yMoveAngle) {
		return (double) (Math.sin(yMoveAngle * Math.PI / 180));
	}

}