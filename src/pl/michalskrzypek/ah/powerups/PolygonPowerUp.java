package pl.michalskrzypek.ah.powerups;


import java.awt.Polygon;
import java.awt.Rectangle;


@SuppressWarnings("serial")
public abstract class PolygonPowerUp extends Polygon implements PowerUp {

	private boolean onScreen;
	private static int[] polygonXCoordinates;
	private static int[] polygonYCoordinates;
	private double centerX;
	private double centerY;
	private double yVelocity = 2;
	private double xVelocity = 0;
	
	public PolygonPowerUp(int[] polyXCoordinates, int[] polyYCoordinates, int numberOfCorners) {
		super(polyXCoordinates, polyYCoordinates, numberOfCorners);
		
		polygonXCoordinates = polyXCoordinates;
		polygonYCoordinates = polyYCoordinates;
		
		
		onScreen = true;

	}

	// Bounds for collision detection
	public abstract Rectangle getBounds();
	
	public abstract void move();
	
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
	
	public void setXVelovity(double xVel) {
		this.xVelocity = xVel;
	}

	public double getXVelocity() {
		return this.xVelocity;
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
	
	public int[] getPolyXCoordinates() {
		return polygonXCoordinates;
	}
}
