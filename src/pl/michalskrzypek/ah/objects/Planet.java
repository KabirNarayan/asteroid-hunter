package pl.michalskrzypek.ah.objects;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import pl.michalskrzypek.ah.main.AsteroidGameBoard;

public class Planet extends Ellipse2D{
	
	private String planetName;
	private double planetRadius = 100;

	private int xCenter = AsteroidGameBoard.frameWidth/2;
	private int yCenter = AsteroidGameBoard.frameHeight/2;
	
	private Color planetColor = null;
	
	private double gravityForce;
	
	public Planet(String planetName, double planetRadius, Color planetColor, double gravityForce) {
		super();
		this.planetName = planetName;
		this.planetRadius= planetRadius;
		this.planetColor= planetColor;
		this.gravityForce= gravityForce;
	}
	
	public double getPlanetRadius() {
		return planetRadius;
	}

	public void setPlanetRadius(double planetRadius) {
		this.planetRadius = planetRadius;
	}

	public int getxCenter() {
		return xCenter;
	}

	public void setxCenter(int xCenter) {
		this.xCenter = xCenter;
	}

	public int getyCenter() {
		return yCenter;
	}

	public void setyCenter(int yCenter) {
		this.yCenter = yCenter;
	}


	public Color getPlanetColor() {
		return planetColor;
	}

	public void setPlanetColor(Color planetColor) {
		this.planetColor = planetColor;
	}

	public double getGravityForce() {
		return gravityForce;
	}

	public void setGravityForce(double gravityForce) {
		this.gravityForce = gravityForce;
	}
	

	public String getPlanetName() {
		return planetName;
	}

	public void setPlanetName(String planetName) {
		this.planetName = planetName;
	}
	
	@Override
	public Rectangle2D getBounds2D() {
		// TODO Auto-generated method stub
		Rectangle2D rect = new Rectangle((int)(this.xCenter-this.planetRadius),(int)(this.yCenter-this.planetRadius),(int)this.planetRadius, (int)this.planetRadius);
		return rect;
	}

	@Override
	public double getHeight() {
		// TODO Auto-generated method stub
		return this.planetRadius*2;
	}

	@Override
	public double getWidth() {
		// TODO Auto-generated method stub
		return this.planetRadius*2;
	}


	@Override
	public double getX() {
		// TODO Auto-generated method stub
		return this.xCenter-this.planetRadius;
	}

	@Override
	public double getY() {
		// TODO Auto-generated method stub
		return this.yCenter-this.planetRadius;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFrame(double arg0, double arg1, double arg2, double arg3) {
		// TODO Auto-generated method stub
		
	}
}
