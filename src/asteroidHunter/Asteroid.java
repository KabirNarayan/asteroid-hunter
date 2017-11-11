package asteroidHunter;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Asteroid extends Polygon {

	private static int speed;
	private boolean onScreen;
	private static int[] polygonXCoordinates = { -20, -10, -1, 0, 10, 8, 20, 4, -2,
			-10, -20 };
	private static int[] polygonYCoordinates = { -5, -13, -10, -15, -7, -3, 5, 15, 8,
			12, -5 };

	private double xVelocity = 0; 
	private double yVelocity = 0;

	public static int brokenAsteroids = 0;
public static int howMany = 0;
	public int which = 0;
	
	public Asteroid(int[] polygonXCoordinates, int[] polygonYCoordinates,
			int numberOfCorners) {
		super(polygonXCoordinates, polygonYCoordinates, numberOfCorners);

		this.xVelocity = (Math.random() * speed) + 1;
		this.yVelocity = (Math.random() * speed) + 1;
		onScreen = true;
	}

	//Bounds for collision detection
	public Rectangle getBounds() {
		return new Rectangle(this.xpoints[0], this.ypoints[3], 40, 30);
	}

	public void setOnScreen(boolean bool){
		this.onScreen = bool;
	}
	
	public boolean getOnScreen(){
		return this.onScreen;
	}
	
	public void setXVelovity(double xVel){
		this.xVelocity = xVel;
	}
	
	public void setYVelovity(double yVel){
		this.yVelocity = yVel;
	}
	
	public double getXVelocity(){
		return this.xVelocity;
	}
	
	public double getYVelocity(){
		return this.yVelocity;
	}
	
	public void move() {
if (this.onScreen == true){
		Rectangle rectToCheck = this.getBounds();
		Rectangle shipBounds = AsteroidGameBoard.ship.getBounds();
		Point p1 = rectToCheck.getLocation();
		Point p2 = new Point(p1.x + 20, p1.y + 15); // center of this asteroid

		for (Asteroid asteroid : AsteroidGameBoard.asteroids) {
			if(asteroid.onScreen ==true){
			Rectangle otherRect = asteroid.getBounds();
			Point p3 = otherRect.getLocation();
			Point p4 = new Point(p3.x + 20, p3.y + 15);// center of the other
														// asteroid

			//collision detection for asteroid and ship
			if (otherRect.intersects(shipBounds)) {
				int randomXInitialPos = 0;
				int randomYInitialPos = 0;
				for (int i = 0; i < 10; i++) {
					randomXInitialPos = (int) (Math.random() * (AsteroidGameBoard.frameWidth - 50)) + 21;
					randomYInitialPos = (int) (Math.random() * (AsteroidGameBoard.frameHeight - 40)) + 16;
				}

				asteroid.xpoints = Asteroid.getInitialXPosition(randomXInitialPos);

				asteroid.ypoints = Asteroid.getInitialYPosition(randomYInitialPos);

				AsteroidGameBoard.playSound("./sounds/explode.wav");
				AsteroidGameBoard.ship.takeLife();

			}

			if (this != asteroid
					&& (otherRect.intersects(rectToCheck) || rectToCheck
							.intersects(otherRect))) {
				double tempXDir = this.xVelocity;
				double tempYDir = this.yVelocity;

				this.xVelocity = asteroid.xVelocity;
				this.yVelocity = asteroid.yVelocity;

				asteroid.xVelocity = tempXDir;
				asteroid.yVelocity = tempYDir;
			}

			if (this != asteroid
					&& (rectToCheck.contains(p4) || otherRect.contains(p2))) {
				int randomXInitialPos = 0;
				int randomYInitialPos = 0;
				for (int i = 0; i < 10; i++) {
					randomXInitialPos = (int) (Math.random() * (AsteroidGameBoard.frameWidth - 50)) + 21;
					randomYInitialPos = (int) (Math.random() * (AsteroidGameBoard.frameHeight - 40)) + 16;
				}

				this.xpoints = Asteroid.getInitialXPosition(randomXInitialPos);

				this.ypoints = Asteroid.getInitialYPosition(randomYInitialPos);

				brokenAsteroids++;
				System.out.println("Broken asteroids: " + brokenAsteroids);

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

			this.xpoints = Asteroid.getInitialXPosition(randomXInitialPos);

			this.ypoints = Asteroid.getInitialYPosition(randomYInitialPos);

			brokenAsteroids++;
			System.out.println("Broken asteroids: " + brokenAsteroids);
		}
		if (rectToCheck.getLocation().y < -5
				|| rectToCheck.getLocation().y + 30 > AsteroidGameBoard.frameHeight + 10) {
			int randomXInitialPos = 0;
			int randomYInitialPos = 0;
			for (int i = 0; i < 10; i++) {
				randomXInitialPos = (int) (Math.random() * (AsteroidGameBoard.frameWidth - 50)) + 21;
				randomYInitialPos = (int) (Math.random() * (AsteroidGameBoard.frameHeight - 40)) + 16;
			}

			super.xpoints = Asteroid.getInitialXPosition(randomXInitialPos);

			super.ypoints = Asteroid.getInitialYPosition(randomYInitialPos);

			brokenAsteroids++;
			System.out.println("Broken asteroids: " + brokenAsteroids);
		}

		if (super.xpoints[0] <= 0
				|| super.xpoints[6] >= AsteroidGameBoard.frameWidth) {
			xVelocity = -xVelocity;
		}

		if (super.ypoints[3] <= 0
				|| super.ypoints[7] >= AsteroidGameBoard.frameHeight) {
			yVelocity = -yVelocity;
		}

		for (int i = 0; i < super.xpoints.length; i++) {
			super.xpoints[i] += xVelocity;
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

	public static int[] getInitialYPosition(int randomInitialYPosition) {
		int[] tempYPosition = (int[]) polygonYCoordinates.clone();
		for (int i = 0; i < tempYPosition.length; i++) {
			tempYPosition[i] += randomInitialYPosition;
		}
		return tempYPosition;
	}

	public static int getSpeed() {
		return speed;
	}

	public static void setSpeed(int theSpeed) {
		speed = theSpeed;
	}

}
