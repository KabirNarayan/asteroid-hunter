package pl.michalskrzypek.ah.spaceobjects.powerups;

import java.awt.Rectangle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import pl.michalskrzypek.ah.gui.AsteroidGameBoard;
import pl.michalskrzypek.ah.gui.Utilities;

@SuppressWarnings("serial")
public class Freezer extends PolygonPowerUp {

	private double yVelocity = 2;
	private static int[] polygonXCoordinates = { -7, 7, 7, -7, -7 };
	private static int[] polygonYCoordinates = { -10, -10, 10, 10, -10 };
	private static final int NUMBER_OF_CORNERS = 5;

	public Freezer(int[] polyXCoordinates) {
		super(polyXCoordinates, polygonYCoordinates, NUMBER_OF_CORNERS);
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(this.xpoints[0], this.ypoints[0], 14, 20);
	}

	public void move() {
		if (this.getOnScreen() == true) {
			Rectangle shipBounds = AsteroidGameBoard.ship.getBounds();

			for (Freezer fr : AsteroidGameBoard.freezers) {
				if (fr.getOnScreen() == true) {
					Rectangle otherRect = fr.getBounds();

					// collision detection with ship
					if (otherRect.intersects(shipBounds)) {
						fr.setOnScreen(false);
						AsteroidGameBoard.freeze = true;
						Utilities.playSound("./sounds/collect.wav");
						ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
						executor.schedule(() -> {
								AsteroidGameBoard.freeze = false;
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
