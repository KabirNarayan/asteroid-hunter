package pl.michalskrzypek.ah.powerups;

import java.awt.Rectangle;

public interface PowerUp {

	public void move();

	// Bounds for collision detection
	public Rectangle getBounds();

	public void setYVelovity(double yVel);

	public double getYVelocity();

	public double getXCenter();

	public double getYCenter();

	public void setXCenter(double xCent);

	public void setYCenter(double yCent);

	public void setOnScreen(boolean bool);

	public boolean getOnScreen();

}
