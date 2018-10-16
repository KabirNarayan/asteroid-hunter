package pl.michalskrzypek.ah.spaceobjects.powerups;

import java.awt.Rectangle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import pl.michalskrzypek.ah.gui.AsteroidGameBoard;
import pl.michalskrzypek.ah.spaceobjects.Asteroid;
import pl.michalskrzypek.ah.utils.SoundUtil;

@SuppressWarnings("serial")
public class SlowTimer extends PolygonPowerUp {

	public static final int WIDTH = 16;
	public static final int HEIGHT = 16;
	private static int[] polygonXCoordinates = { -8, 8, 8, -8, -8 };
	private static int[] polygonYCoordinates = { -8, -8, 8, 8, -8 };
	private static final int NUMBER_OF_CORNERS = 5;

	private double yVelocity = 2;

	public SlowTimer(int[] initialXCoordinates) {
		super(initialXCoordinates, polygonYCoordinates, NUMBER_OF_CORNERS);
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(this.xpoints[0], this.ypoints[0], WIDTH, HEIGHT);
	}

	public void move() {
		if (this.getOnScreen() == true) {
			Rectangle shipBounds = AsteroidGameBoard.ship.getBounds();

			for (SlowTimer st : AsteroidGameBoard.slowTimers) {
				if (st.getOnScreen() == true) {
					Rectangle otherRect = st.getBounds();

					// collision detection with ship
					if (otherRect.intersects(shipBounds)) {
						st.setOnScreen(false);
						AsteroidGameBoard.slowTime = true;
						SoundUtil.playSound("./sounds/collect.wav");

						for (Asteroid as : AsteroidGameBoard.asteroids) {
							as.setXVelocity(as.getXVelocity() * 0.5);
							as.setYVelocity(as.getYVelocity() * 0.5);
						}

						ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
						executor.schedule(() -> {
							AsteroidGameBoard.slowTime = false;
							for (Asteroid as : AsteroidGameBoard.asteroids) {
								as.setXVelocity(as.getXVelocity() * 2);
								as.setYVelocity(as.getYVelocity() * 2);
							}
						}, 5000, TimeUnit.MILLISECONDS);

					}
				}
				if (st.ypoints[0] > AsteroidGameBoard.FRAME_HEIGHT) {
					st.setOnScreen(false);
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
