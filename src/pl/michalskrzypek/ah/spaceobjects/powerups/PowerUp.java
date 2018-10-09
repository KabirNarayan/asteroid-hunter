package pl.michalskrzypek.ah.spaceobjects.powerups;

import java.awt.Rectangle;

public interface PowerUp {

	// Bounds for collision detection
	public Rectangle getBounds();

	public void setYVelovity(double yVel);

	public double getYVelocity();
	
	public void setXVelovity(double yVel);

	public double getXVelocity();

	public double getXCenter();

	public double getYCenter();

	public void setXCenter(double xCent);

	public void setYCenter(double yCent);

	public void setOnScreen(boolean bool);

	public boolean getOnScreen();

}
