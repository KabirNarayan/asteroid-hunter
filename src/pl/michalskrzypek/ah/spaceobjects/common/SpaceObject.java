package pl.michalskrzypek.ah.spaceobjects.common;

import java.awt.Polygon;
import java.awt.Rectangle;

public abstract class SpaceObject extends Polygon {

	private double xCenter;
	private double yCenter;
	private double xVelocity;
	private double yVelocity;
	private int width;
	private int height;
	private boolean onScreen = true;

	public SpaceObject(int[] polyXArray, int[] polyYArray, int numbOfCorners) {
		super(polyXArray, polyYArray, numbOfCorners);
	}

	public abstract Rectangle getBounds();

	public double getXCenter() {
		return xCenter;
	}

	public double getYCenter() {
		return yCenter;
	}

	public void setXCenter(double xCent) {
		this.xCenter = xCent;
	}

	public void setYCenter(double yCent) {
		this.yCenter = yCent;
	}

	public double getXVelocity() {
		return xVelocity;
	}

	public double getYVelocity() {
		return yVelocity;
	}

	public void setXVelocity(double xVel) {
		this.xVelocity = xVel;
	}

	public void setYVelocity(double yVel) {
		this.yVelocity = yVel;
	}

	public void setOnScreen(boolean onScr) {
		this.onScreen = onScr;
	}

	public boolean getOnScreen() {
		return this.onScreen;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setWidth(int w) {
		this.width = w;
	}

	public void setHeight(int h) {
		this.height = h;
	}

	public void increaseXVelocity(double incAmt) {
		this.xVelocity += incAmt;
	}

	public void decreaseXVelocity(double incAmt) {
		this.xVelocity -= incAmt;
	}

	public void increaseYVelocity(double incAmt) {
		this.yVelocity += incAmt;
	}

	public void decreaseYVelocity(double incAmt) {
		this.yVelocity -= incAmt;
	}
}
