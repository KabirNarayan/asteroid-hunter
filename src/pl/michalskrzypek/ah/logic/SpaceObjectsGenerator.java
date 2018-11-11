package pl.michalskrzypek.ah.logic;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pl.michalskrzypek.ah.gui.AsteroidGameBoard;
import pl.michalskrzypek.ah.gui.InitialScreen;
import pl.michalskrzypek.ah.spaceobjects.Asteroid;
import pl.michalskrzypek.ah.spaceobjects.powerups.Freezer;
import pl.michalskrzypek.ah.spaceobjects.powerups.SlowTimer;

public class SpaceObjectsGenerator {

	public static void generateAsteroids(ArrayList<Asteroid> asteroids) {
		asteroids.clear();
		int numb = 6;

		// setting proper asteroids number (regarding to the chosen level)
		if (InitialScreen.getLevel().equals("Easy")) {
			numb = 6;
			Asteroid.setSpeed(2);
		} else if (InitialScreen.getLevel().equals("Medium")) {
			numb = 8;
			Asteroid.setSpeed(3);
		} else if (InitialScreen.getLevel().equals("Hard")) {
			numb = 10;
			Asteroid.setSpeed(4);
		}
		for (int i = 0; i < numb; i++) {
			int randomXInitialPos = (int) (Math.random() * (AsteroidGameBoard.FRAME_WIDTH - 50)) + 21;
			int randomYInitialPos = (int) (Math.random() * (AsteroidGameBoard.FRAME_HEIGHT - 40)) + 16;

			while ((randomXInitialPos >= AsteroidGameBoard.FRAME_WIDTH / 2 - 50
					&& randomXInitialPos <= AsteroidGameBoard.FRAME_WIDTH / 2 + 50)
					&& (randomYInitialPos >= AsteroidGameBoard.FRAME_HEIGHT / 2 - 50
							&& randomYInitialPos <= AsteroidGameBoard.FRAME_HEIGHT / 2 + 50)) {
				randomXInitialPos = (int) (Math.random() * (AsteroidGameBoard.FRAME_WIDTH - 50)) + 21;
				randomYInitialPos = (int) (Math.random() * (AsteroidGameBoard.FRAME_HEIGHT - 40)) + 16;
			}

			Asteroid theasteroid = new Asteroid(Asteroid.getInitialXPosition(randomXInitialPos),
					Asteroid.getInitialYPosition(randomYInitialPos));

			asteroids.add(theasteroid);
		}
	}// END of generateAsteroids method

	// Generates a freezer power up every 7 seconds
	public static void generateSlowTimers(ScheduledThreadPoolExecutor executorPU, ArrayList<SlowTimer> slowTimers) {
		executorPU.scheduleAtFixedRate(() -> {
			long counter = slowTimers.stream().filter(s -> s.getOnScreen()).count();

			if (counter == 0 && AsteroidGameBoard.generate) {
				int randomXInitialPos = (int) (Math.random() * (AsteroidGameBoard.FRAME_WIDTH - 50)) + 21;

				while ((randomXInitialPos >= AsteroidGameBoard.FRAME_WIDTH / 2 - 50
						&& randomXInitialPos <= AsteroidGameBoard.FRAME_WIDTH / 2 + 50)) {
					randomXInitialPos = (int) (Math.random() * (AsteroidGameBoard.FRAME_WIDTH - 50)) + 21;
				}

				SlowTimer st = new SlowTimer(SlowTimer.getInitialXPosition(randomXInitialPos));
				slowTimers.add(st);
			}
			counter = 0;
		}, 3, 10, TimeUnit.SECONDS);
	}

	// Generates a freezer power up every 10 seconds
	public static void generateFreezers(ScheduledThreadPoolExecutor executorPU, ArrayList<Freezer> freezers) {
		executorPU.scheduleAtFixedRate(() -> {
			long counter = freezers.stream().filter(f -> f.getOnScreen()).count();
			if (counter == 0 && AsteroidGameBoard.generate) {
				int randomXInitialPos = (int) (Math.random() * (AsteroidGameBoard.FRAME_WIDTH - 50)) + 21;

				while ((randomXInitialPos >= AsteroidGameBoard.FRAME_WIDTH / 2 - 50
						&& randomXInitialPos <= AsteroidGameBoard.FRAME_WIDTH / 2 + 50)) {
					randomXInitialPos = (int) (Math.random() * (AsteroidGameBoard.FRAME_WIDTH - 50)) + 21;
				}
				Freezer fr = new Freezer(Freezer.getInitialXPosition(randomXInitialPos));
				freezers.add(fr);
			}
			counter = 0;
		}, 7, 14, TimeUnit.SECONDS);
	}// END of generateFreezers method

}
