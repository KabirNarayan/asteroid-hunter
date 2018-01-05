package pl.michalskrzypek.ah.powerups;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pl.michalskrzypek.ah.main.AsteroidGameBoard;
import pl.michalskrzypek.ah.objects.Asteroid;

public class Freezer extends Polygon implements PowerUp {

	private boolean onScreen;
	private static int[] polygonXCoordinates = { -7, 7, 7, -7, -7 };
	private static int[] polygonYCoordinates = { -10, -10, 10, 10, -10 };
	private static int numberOfCorners = 5;
	double centerX;
	double centerY;
	private double yVelocity = 2;

	public static int brokenAsteroids = 0;
	public static int howMany = 0;
	public int which = 0;

	public Freezer(int[] polygonXCoordinates) {
		super(polygonXCoordinates, polygonYCoordinates, numberOfCorners);
		onScreen = true;

	}

	// Bounds for collision detection
	public Rectangle getBounds() {
		return new Rectangle(this.xpoints[0], this.ypoints[0], 40, 30);
	}

	public void setOnScreen(boolean bool) {
		this.onScreen = bool;
	}

	public boolean getOnScreen() {
		return this.onScreen;
	}

	public void setYVelovity(double yVel) {
		this.yVelocity = yVel;
	}

	public double getYVelocity() {
		return this.yVelocity;
	}

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
	
	public int[] getPolyYCoordinates() {
		return polygonYCoordinates;
	}

	public void move() {
		if (this.onScreen == true) {
			Rectangle rectToCheck = this.getBounds();
			Rectangle shipBounds = AsteroidGameBoard.ship.getBounds();

			for (Freezer fr : AsteroidGameBoard.freezers) {
				if (fr.onScreen == true) {
					Rectangle otherRect = fr.getBounds();
					Point p3 = otherRect.getLocation();

					// collision detection with ship
					if (otherRect.intersects(shipBounds)) {
						fr.setOnScreen(false);
						AsteroidGameBoard.freeze = true;
						AsteroidGameBoard.playSound("./sounds/collect.wav");
						ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
						executor.schedule(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								AsteroidGameBoard.freeze = false;
							}
						}, 5000, TimeUnit.MILLISECONDS);

					}

				}
				if (fr.ypoints[0] > AsteroidGameBoard.frameHeight) {
					fr.setOnScreen(false);
				}

			}

			for (int i = 0; i < super.ypoints.length; i++) {
				super.ypoints[i] += yVelocity;
			}
		}
	}

	public static int[] getInitialXPosition(int randomInitialXPosition) {
		int[] tempXPosition = (int[]) polygonXCoordinates.clone();
		for (int i = 0; i < tempXPosition.length; i++) {
			tempXPosition[i] += randomInitialXPosition;
		}

		return tempXPosition;
	}

}
