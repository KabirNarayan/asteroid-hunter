package pl.michalskrzypek.ah.logic;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import pl.michalskrzypek.ah.gui.AsteroidGameBoard;
import pl.michalskrzypek.ah.spaceobjects.Asteroid;
import pl.michalskrzypek.ah.spaceobjects.Bullet;
import pl.michalskrzypek.ah.spaceobjects.SpaceShip;
import pl.michalskrzypek.ah.utils.SoundUtil;

public class Collision {

	// Collision detection for a bullet and an asteroid
	public static void BulletDetection(Bullet bullet, ArrayList<Asteroid> asteroids) {
		for (Asteroid asteroid : asteroids) {
			if (asteroid.getBounds().contains(bullet.getBounds()) && asteroid.getOnScreen()) {
				asteroid.setOnScreen(false);
				bullet.setOnScreen(false);
				AsteroidGameBoard.aLeft--;
				AsteroidGameBoard.currentBullets--;
			}
		}
	}

	public static Point AsteroidShipDetection(Asteroid currentAsteroid, SpaceShip ship, ArrayList<Asteroid> asteroids) {
		Point collisionPoint = null;

		if (currentAsteroid.getOnScreen() == true) {
			Rectangle rectToCheck = currentAsteroid.getBounds();
			Rectangle shipBounds = ship.getBounds();
			Point p1 = rectToCheck.getLocation();
			Point p2 = new Point(p1.x + 20, p1.y + 15); // center of currentAsteroid asteroid

			for (Asteroid asteroid : asteroids) {
				if (asteroid.getOnScreen() == true) {
					Rectangle otherRect = asteroid.getBounds();
					Point p3 = otherRect.getLocation();
					Point p4 = new Point(p3.x + 20, p3.y + 15);// center of the other
																// asteroid

					// collision detection for asteroid and ship
					if (otherRect.intersects(shipBounds)) {

						collisionPoint = shipBounds.getLocation();

						int randomXInitialPos = 0;
						int randomYInitialPos = 0;
						for (int i = 0; i < 10; i++) {
							randomXInitialPos = (int) (Math.random() * (AsteroidGameBoard.frameWidth - 50)) + 21;
							randomYInitialPos = (int) (Math.random() * (AsteroidGameBoard.frameHeight - 40)) + 16;
						}

						asteroid.xpoints = Asteroid.getInitialXPosition(randomXInitialPos);
						asteroid.ypoints = Asteroid.getInitialYPosition(randomYInitialPos);

						SoundUtil.playSound("./sounds/explode.wav");
						ship.takeLife();

					}

					if (currentAsteroid != asteroid
							&& (otherRect.intersects(rectToCheck) || rectToCheck.intersects(otherRect))) {
						double tempXDir = currentAsteroid.getXVelocity();
						double tempYDir = currentAsteroid.getYVelocity();

						currentAsteroid.setXVelocity(asteroid.getXVelocity());
						currentAsteroid.setYVelocity(asteroid.getYVelocity());

						asteroid.setXVelocity(tempXDir);
						asteroid.setYVelocity(tempYDir);
					}

					if (currentAsteroid != asteroid && (rectToCheck.contains(p4) || otherRect.contains(p2))) {
						int randomXInitialPos = 0;
						int randomYInitialPos = 0;
						for (int i = 0; i < 10; i++) {
							randomXInitialPos = (int) (Math.random() * (AsteroidGameBoard.frameWidth - 50)) + 21;
							randomYInitialPos = (int) (Math.random() * (AsteroidGameBoard.frameHeight - 40)) + 16;
						}

						currentAsteroid.xpoints = Asteroid.getInitialXPosition(randomXInitialPos);
						currentAsteroid.ypoints = Asteroid.getInitialYPosition(randomYInitialPos);

					}
				}
			}
			if (rectToCheck.getLocation().x < -5
					|| rectToCheck.getLocation().x + 40 > AsteroidGameBoard.frameWidth + 5) {
				int randomXInitialPos = 0;
				int randomYInitialPos = 0;
				for (int i = 0; i < 10; i++) {
					randomXInitialPos = (int) (Math.random() * (AsteroidGameBoard.frameWidth - 50)) + 21;
					randomYInitialPos = (int) (Math.random() * (AsteroidGameBoard.frameHeight - 40)) + 16;
				}

				currentAsteroid.xpoints = Asteroid.getInitialXPosition(randomXInitialPos);
				currentAsteroid.ypoints = Asteroid.getInitialYPosition(randomYInitialPos);

			}
			if (rectToCheck.getLocation().y < -5
					|| rectToCheck.getLocation().y + 30 > AsteroidGameBoard.frameHeight + 10) {
				int randomXInitialPos = 0;
				int randomYInitialPos = 0;
				for (int i = 0; i < 10; i++) {
					randomXInitialPos = (int) (Math.random() * (AsteroidGameBoard.frameWidth - 50)) + 21;
					randomYInitialPos = (int) (Math.random() * (AsteroidGameBoard.frameHeight - 40)) + 16;
				}

				currentAsteroid.xpoints = Asteroid.getInitialXPosition(randomXInitialPos);
				currentAsteroid.ypoints = Asteroid.getInitialYPosition(randomYInitialPos);

			}
		}

		return collisionPoint;
	}
}
