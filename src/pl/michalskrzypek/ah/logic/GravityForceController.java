package pl.michalskrzypek.ah.logic;

import pl.michalskrzypek.ah.gui.AsteroidGameBoard;
import pl.michalskrzypek.ah.spaceobjects.Planet;
import pl.michalskrzypek.ah.spaceobjects.SpaceShip;

public class GravityForceController {
	
	/*
	 * Adds gravity force to a ship
	 * 
	 * @param theShip the ship we add a gravity force to
	 * 
	 * @param thePlanet the planet around which theShip flies
	 */
	public static void addGravityForce(SpaceShip theShip, Planet thePlanet) {
		double dx = theShip.getXCenter() - thePlanet.getxCenter();
		double dy = theShip.getYCenter() - thePlanet.getyCenter();

		double distance = Math.sqrt(dx * dx + dy * dy);
		double force = 0.1;

		if (distance <= 50) {
			force = thePlanet.getGravityForce() * 2;
		} else if (distance <= 100) {
			force = thePlanet.getGravityForce() * 2;
		} else if (distance <= 150) {
			force = thePlanet.getGravityForce() * 1;
		} else if (distance <= 200) {
			force = thePlanet.getGravityForce() * 1;
		} else if (distance <= 250) {
			force = thePlanet.getGravityForce() * 1;
		} else if (distance <= 300) {
			force = thePlanet.getGravityForce() * 1;
		} else {
			force = thePlanet.getGravityForce() * 1;
		}

		double angleRadians = Math.atan2(theShip.getYCenter() - thePlanet.getyCenter(),
				theShip.getXCenter() - thePlanet.getxCenter());
		double angleDegrees = Math.toDegrees(angleRadians);
		if (angleDegrees < 0) {
			angleDegrees = angleDegrees + 360;
		}
		theShip.setMovingAngle(angleDegrees);

		if (angleDegrees <= 180) {
			if (angleDegrees <= 90) {
				if (theShip.getXVelocity() >= (-1) * SpaceShip.MAX_VELOCITY
						&& theShip.getYVelocity() >= (-1) * SpaceShip.MAX_VELOCITY) {
					theShip.decreaseXVelocity(theShip.shipXMoveAngle(theShip.getMovingAngle()) * 0.1);
					theShip.decreaseYVelocity(theShip.shipYMoveAngle(theShip.getMovingAngle()) * force);
				}
			} else {
				if (theShip.getXVelocity() <= SpaceShip.MAX_VELOCITY
						&& theShip.getYVelocity() >= (-1) * SpaceShip.MAX_VELOCITY) {
					theShip.decreaseXVelocity(theShip.shipXMoveAngle(theShip.getMovingAngle()) * 0.1);
					theShip.decreaseYVelocity(theShip.shipYMoveAngle(theShip.getMovingAngle()) * force);
				}
			}
		} else {
			if (angleDegrees <= 270) {
				if (theShip.getXVelocity() <= SpaceShip.MAX_VELOCITY
						&& theShip.getYVelocity() <= SpaceShip.MAX_VELOCITY) {
					theShip.decreaseXVelocity(theShip.shipXMoveAngle(theShip.getMovingAngle()) * 0.1);
					theShip.decreaseYVelocity(theShip.shipYMoveAngle(theShip.getMovingAngle()) * force);
				}
			} else {
				if (theShip.getXVelocity() >= (-1) * SpaceShip.MAX_VELOCITY
						&& theShip.getYVelocity() <= SpaceShip.MAX_VELOCITY) {
					theShip.decreaseXVelocity(theShip.shipXMoveAngle(theShip.getMovingAngle()) * 0.1);
					theShip.decreaseYVelocity(theShip.shipYMoveAngle(theShip.getMovingAngle()) * force);
				}
			}
		}

		if (thePlanet.contains(theShip.getBounds2D())) {
			theShip.setXVelocity(0);
			theShip.setYVelocity(0);
		}

	}// END of gravityForce method

	// MAIN CODE FOR MAKING A SHIP FLOAT
	public static void controlShip(SpaceShip ship) {
		if (AsteroidGameBoard.keyHeld == true && AsteroidGameBoard.keyChar == 'd') {
			ship.increaseRotationAngle();
			addGravityForce(AsteroidGameBoard.ship, AsteroidGameBoard.thePlanet);
		} else if (AsteroidGameBoard.keyHeld == true && AsteroidGameBoard.keyChar == 'a') {
			ship.decreaseRotationAngle();
			addGravityForce(AsteroidGameBoard.ship, AsteroidGameBoard.thePlanet);
		} else if (AsteroidGameBoard.keyHeld == true && AsteroidGameBoard.keyChar == 'w') {
			ship.setMovingAngle(ship.getRotationAngle());
			if (((ship.getRotationAngle() < 90 || ship.getRotationAngle() > 270)
					&& ship.getXVelocity() <= SpaceShip.MAX_VELOCITY)
					|| ((ship.getRotationAngle() >= 90 && ship.getRotationAngle() <= 270)
							&& ship.getXVelocity() >= (-1) * SpaceShip.MAX_VELOCITY)) {
				ship.increaseXVelocity(ship.shipXMoveAngle(ship.getMovingAngle()) * 0.1);

			}

			if (((ship.getRotationAngle() >= 0 && ship.getRotationAngle() <= 180)
					&& ship.getYVelocity() <= SpaceShip.MAX_VELOCITY)
					|| ((ship.getRotationAngle() > 180 && ship.getRotationAngle() <= 359)
							&& ship.getYVelocity() >= (-1) * SpaceShip.MAX_VELOCITY)) {
				ship.increaseYVelocity(ship.shipYMoveAngle(ship.getMovingAngle()) * 0.1);
			}
		} else if (AsteroidGameBoard.keyHeld == true && AsteroidGameBoard.keyChar == 's') {
			ship.setMovingAngle(ship.getRotationAngle());
			if (((ship.getRotationAngle() < 90 || ship.getRotationAngle() > 270)
					&& ship.getXVelocity() > (-1) * SpaceShip.MAX_VELOCITY)
					|| ((ship.getRotationAngle() >= 90 && ship.getRotationAngle() <= 270)
							&& ship.getXVelocity() <= SpaceShip.MAX_VELOCITY)) {
				ship.decreaseXVelocity(ship.shipXMoveAngle(ship.getMovingAngle()) * 0.1);
			}
			if (((ship.getRotationAngle() >= 0 && ship.getRotationAngle() <= 180)
					&& ship.getYVelocity() > (-1) * SpaceShip.MAX_VELOCITY)
					|| ((ship.getRotationAngle() > 180 && ship.getRotationAngle() <= 359)
							&& ship.getYVelocity() <= SpaceShip.MAX_VELOCITY)) {
				ship.decreaseYVelocity(ship.shipYMoveAngle(ship.getMovingAngle()) * 0.1);
			}
		} else {
			addGravityForce(AsteroidGameBoard.ship, AsteroidGameBoard.thePlanet);
		}
		ship.move();
	}// END OF THE shipFly method

}
