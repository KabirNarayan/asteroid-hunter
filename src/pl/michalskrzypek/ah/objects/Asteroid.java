package pl.michalskrzypek.ah.objects;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import pl.michalskrzypek.ah.main.AsteroidGameBoard;
import pl.michalskrzypek.ah.main.Collision;

public class Asteroid extends SpaceObject {

	private static double speed;

	private static int[] polygonXCoordinates = { -20, -10, -1, 0, 10, 8, 20, 4, -2, -10, -20 };
	private static int[] polygonYCoordinates = { -5, -13, -10, -20, -7, -3, 5, 20, 8, 12, -5 };

	private int width = 40;
	private int height = 40;

	public Asteroid(int[] polygonXCoordinates, int[] polygonYCoordinates, int numberOfCorners) {
		super(polygonXCoordinates, polygonYCoordinates, numberOfCorners);

		this.setXVelocity(Math.random() * speed + 1);
		this.setYVelocity(Math.random() * speed + 1);

		this.setWidth(width);
		this.setHeight(height);

		this.setOnScreen(true);
		;
	}

	// Bounds for collision detection
	public Rectangle getBounds() {
		return new Rectangle(this.xpoints[0], this.ypoints[3], 40, 40);
	}

	public double setXMoveAngle(double xMoveAngle) {

		return (double) (Math.cos(xMoveAngle * Math.PI / 180));

	}

	public double setYMoveAngle(double yMoveAngle) {

		return (double) (Math.sin(yMoveAngle * Math.PI / 180));

	}

	public void move() {

		if (super.xpoints[0] <= 0 || super.xpoints[6] >= AsteroidGameBoard.frameWidth) {
			this.setXVelocity(this.getXVelocity()*(-1));
		}

		if (super.ypoints[3] <= 0 || super.ypoints[7] >= AsteroidGameBoard.frameHeight) {
			this.setYVelocity(this.getYVelocity()*(-1));
		}

		for (int i = 0; i < super.xpoints.length; i++) {
			super.xpoints[i] += this.getXVelocity();
		}

		for (int i = 0; i < super.ypoints.length; i++) {
			super.ypoints[i] += this.getYVelocity();
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
