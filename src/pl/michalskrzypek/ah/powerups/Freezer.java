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

import javax.rmi.CORBA.Util;

import pl.michalskrzypek.ah.main.AsteroidGameBoard;
import pl.michalskrzypek.ah.main.Utilities;
import pl.michalskrzypek.ah.objects.Asteroid;

@SuppressWarnings("serial")
public class Freezer extends PolygonPowerUp {
	
	private double yVelocity = 2;
	private static int[] polygonXCoordinates =  { -7, 7, 7, -7, -7 };
	private static int[]  polygonYCoordinates =  { -10, -10, 10, 10, -10 };
	private static int numberOfCorners = 5;
	
	public Freezer(int[] polyXCoordinates) {
		super(polyXCoordinates, polygonYCoordinates, numberOfCorners);
		
	}
	
	
	@Override
	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		return new Rectangle(this.xpoints[0], this.ypoints[0], 14, 20);
	}
	
	public void move() {
		if (this.getOnScreen() == true) {
			Rectangle rectToCheck = this.getBounds();
			Rectangle shipBounds = AsteroidGameBoard.ship.getBounds();

			for (Freezer fr : AsteroidGameBoard.freezers) {
				if (fr.getOnScreen() == true) {
					Rectangle otherRect = fr.getBounds();
					Point p3 = otherRect.getLocation();

					// collision detection with ship
					if (otherRect.intersects(shipBounds)) {
						fr.setOnScreen(false);
						AsteroidGameBoard.freeze = true;
						Utilities.playSound("./sounds/collect.wav");
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
