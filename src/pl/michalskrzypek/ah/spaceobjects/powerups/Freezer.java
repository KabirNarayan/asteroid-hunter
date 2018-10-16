package pl.michalskrzypek.ah.spaceobjects.powerups;

import java.awt.Rectangle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import pl.michalskrzypek.ah.gui.AsteroidGameBoard;
import pl.michalskrzypek.ah.utils.SoundUtil;

@SuppressWarnings("serial")
public class Freezer extends PolygonPowerUp {

	public static final int WIDTH = 14;
	public static final int HEIGHT = 20;
	public static boolean freeze;
	private static final int NUMBER_OF_CORNERS = 5;
	private static int[] freezerXCoordinates = { -7, 7, 7, -7, -7 };
	private static int[] freezerYCoordinates = { -10, -10, 10, 10, -10 };
	


	public Freezer(int[] initialXCoordinates) {
		super(initialXCoordinates, freezerYCoordinates, NUMBER_OF_CORNERS);
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(this.xpoints[0], this.ypoints[0], WIDTH, HEIGHT);
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
						freeze = true;
						SoundUtil.playSound("./sounds/collect.wav");
						ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
						executor.schedule(() -> {
								freeze = false;
						}, 5000, TimeUnit.MILLISECONDS);
					}
				}
				if (fr.ypoints[0] > AsteroidGameBoard.FRAME_HEIGHT) {
					fr.setOnScreen(false);
				}
			}

			for (int i = 0; i < super.ypoints.length; i++) {
				super.ypoints[i] += this.getYVelocity();
			}
		}
	}

	public static int[] getInitialXPosition(int randomInitialXPosition) {
		int[] tempXPosition = (int[]) freezerXCoordinates.clone();
		for (int i = 0; i < tempXPosition.length; i++) {
			tempXPosition[i] += randomInitialXPosition;
		}
		return tempXPosition;
	}
}
