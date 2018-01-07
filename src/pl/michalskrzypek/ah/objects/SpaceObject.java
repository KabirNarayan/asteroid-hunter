package pl.michalskrzypek.ah.objects;

import java.awt.Polygon;
import java.awt.Rectangle;

public interface SpaceObject {

	public abstract Rectangle getBounds();

	public int getWidth();

	public int getHeight();

	public void setOnScreen(boolean bool);

	public boolean getOnScreen();

	public void setXVelocity(double xVel);

	public void setYVelocity(double yVel);

	public double getXVelocity();

	public double getYVelocity();

	public void increaseXVelocity(double incAmt);

	public void decreaseXVelocity(double incAmt);

	public void increaseYVelocity(double incAmt);

	public void decreaseYVelocity(double incAmt);

}
