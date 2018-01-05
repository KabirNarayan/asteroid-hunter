package pl.michalskrzypek.ah.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import pl.michalskrzypek.ah.objects.Bullet;

public class KeyManager implements KeyListener{

	

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		AsteroidGameBoard.keyHeld = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (AsteroidGameBoard.currentBullets < AsteroidGameBoard.maxBullets) {
				AsteroidGameBoard.bulletList.add(new Bullet(AsteroidGameBoard.ship.getShipNoseX(), AsteroidGameBoard.ship.getShipNoseY(), AsteroidGameBoard.ship.getRotationAngle()));
				AsteroidGameBoard.currentBullets++;
				AsteroidGameBoard.playSound("./sounds/laser.aiff");
			}
		} 
		
		
		if (e.getKeyChar() == 'p') {
			if (AsteroidGameBoard.pausedTimes % 2 == 0) {
				AsteroidGameBoard.pausedTimes++;
				AsteroidGameBoard.paused= true;
			} else {
				AsteroidGameBoard.paused = false;
				AsteroidGameBoard.pausedTimes--;
			}

		} 
		
		if (e.getKeyChar() == 'w' || e.getKeyChar() == 'a' || e.getKeyChar() == 's'
				|| e.getKeyChar() == 'd') {
			AsteroidGameBoard.getKeyChar = e.getKeyChar();
			AsteroidGameBoard.keyHeld = true;
		}
	}
	
}
