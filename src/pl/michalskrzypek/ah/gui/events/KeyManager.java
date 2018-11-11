package pl.michalskrzypek.ah.gui.events;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import pl.michalskrzypek.ah.gui.AsteroidGameBoard;
import pl.michalskrzypek.ah.spaceobjects.Bullet;
import pl.michalskrzypek.ah.utils.SoundUtil;

/**
 * Class responsible for handling key events during the game.
 * @author mskrzypek
 *
 */
public class KeyManager implements KeyListener {

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {
		AsteroidGameBoard.keyHeld = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (Bullet.numberOfBullets < Bullet.MAX_BULLETS) {
				AsteroidGameBoard.bulletList.add(new Bullet(AsteroidGameBoard.ship.getShipNoseX(),
						AsteroidGameBoard.ship.getShipNoseY(), AsteroidGameBoard.ship.getRotationAngle()));
				Bullet.numberOfBullets++;
				SoundUtil.playSound("./sounds/laser.aiff");
			}
		}
		
		if (e.getKeyChar() == 'p') {
			if (!(AsteroidGameBoard.gameOver == true
					|| !AsteroidGameBoard.ifAnyAsteroidsLeft(AsteroidGameBoard.asteroids))) {
				if (AsteroidGameBoard.pausedTimes % 2 == 0) {
					AsteroidGameBoard.pausedTimes++;
					AsteroidGameBoard.paused = true;
				} else {
					AsteroidGameBoard.paused = false;
					AsteroidGameBoard.pausedTimes--;
				}
			}
		}

		if (e.getKeyChar() == 'w' || e.getKeyChar() == 'a' || e.getKeyChar() == 's' || e.getKeyChar() == 'd') {
			AsteroidGameBoard.keyChar = e.getKeyChar();
			AsteroidGameBoard.keyHeld = true;
		}
	}
}
