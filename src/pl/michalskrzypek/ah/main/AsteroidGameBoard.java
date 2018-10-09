package pl.michalskrzypek.ah.main;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import pl.michalskrzypek.ah.objects.Asteroid;
import pl.michalskrzypek.ah.objects.Bullet;
import pl.michalskrzypek.ah.objects.Planet;
import pl.michalskrzypek.ah.objects.SpaceShip;
import pl.michalskrzypek.ah.powerups.Freezer;
import pl.michalskrzypek.ah.powerups.SlowTimer;

/*
 * Class responsible for the main game functions and thread managing
 */
public class AsteroidGameBoard extends JFrame {

	public static SpaceShip ship;
	public static int frameWidth = 1000;
	public static int frameHeight = 600;
	public static boolean keyHeld = false;
	public static char getKeyChar;
	public static final int maxBullets = 5;
	public static int currentBullets = 0;
	public static ScheduledThreadPoolExecutor executor, executorPU;
	public static JMenuBar menuBar;
	public static JMenu fileMenu;
	public static JMenuItem playAgainItem, goToMenuItem, exitItem;
	public static ComponentCreator comp;
	public static Image spaceImage, explosionImage, asteroidImage, spaceShipImage, earthImage;
	public static ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
	public static ArrayList<Bullet> bulletList = new ArrayList<Bullet>();
	public static ArrayList<Freezer> freezers = new ArrayList<Freezer>();
	public static ArrayList<SlowTimer> slowTimers = new ArrayList<SlowTimer>();
	public static int pausedTimes = 0;
	public static int aLeft = 8;
	public static float timePassed = 0;
	public static String livesLeft;
	public static String timePassedString;
	private static String playerName;
	public static Planet thePlanet = null;
	private static File highScores;
	private static FileWriter fw;
	private static BufferedWriter bw;
	private static FileReader fr;
	private static BufferedReader br;
	private static ArrayList<Double> scoreTimes;
	private static ArrayList<String> scoresTotal;
	public static boolean freeze;
	public static boolean slowTime;
	public static boolean gameOver;
	public static boolean paused;
	private static boolean closed;
	private static boolean generate;
	private static Point collisionPoint = null;
	private static float startTime;
	private Clip clip = null;

	public AsteroidGameBoard(String pName) {
		this.setSize(frameWidth + 15, frameHeight + 60);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Asteroid Hunter");

		playerName = pName;
		initiateHighScoresFiles();

		generate = true;
		freeze = false;
		slowTime = false;
		paused = false;
		closed = false;
		gameOver = false;
		timePassed = 0;
		startTime = 0;
		// initializing executor for generating power ups
		executorPU = new ScheduledThreadPoolExecutor(5);

		// initializing key listener
		this.addKeyListener(new KeyManager());

		comp = new ComponentCreator();
		this.add(comp, BorderLayout.CENTER);
		// initializing executor for repeating paint method in ComponentCreator class
		executor = new ScheduledThreadPoolExecutor(5);
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				comp.repaint();
			}
		}, 0L, 15L, TimeUnit.MILLISECONDS);

		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		playAgainItem = new JMenuItem(new AbstractAction("Play Again") {

			@Override
			public void actionPerformed(ActionEvent e) {
				Utilities.stopBackgroundMusic(clip);
				playAgain();
			}
		});
		fileMenu.add(playAgainItem);

		goToMenuItem = new JMenuItem(new AbstractAction("Go back") {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				AsteroidGameBoard.this.dispose();
				Utilities.stopBackgroundMusic(clip);
				executor.shutdown();
				executorPU.shutdown();
				new InitialScreen();

			}

		});
		fileMenu.add(goToMenuItem);
		fileMenu.addSeparator();

		exitItem = new JMenuItem(new AbstractAction("Exit game") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);
		this.setJMenuBar(menuBar);
		try {
			clip = AudioSystem.getClip();
		} catch (LineUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Utilities.playBackgroundMusic(clip, "./sounds/background.wav");
		this.setVisible(true);
	}

	private static void initiateHighScoresFiles() {
		File highScoresEasy = new File("./scores/high_scores_easy.txt");
		File highScoresMedium = new File("./scores/high_scores_medium.txt");
		File highScoresHard = new File("./scores/high_scores_hard.txt");
		// readers and writers needed to save and read the high scores

		if (InitialScreen.getLevel().equals("Easy")) {
			highScores = highScoresEasy;
		} else if (InitialScreen.getLevel().equals("Medium")) {
			highScores = highScoresMedium;
		} else if (InitialScreen.getLevel().equals("Hard")) {
			highScores = highScoresHard;
		}

		try {
			fr = new FileReader(highScores);
			br = new BufferedReader(fr);
			fw = new FileWriter(highScores, true);
			bw = new BufferedWriter(fw);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static boolean ifAnyAsteroidsLeft(ArrayList<Asteroid> asteroidList) {
		int asteroidsOnScreen = 0;

		for (Asteroid asteroid : asteroidList) {
			if (asteroid.getOnScreen()) {
				asteroidsOnScreen++;
			}
		}

		if (asteroidsOnScreen == 0) {
			return false;
		} else {
			return true;
		}
	}

	public void playAgain() {
		paused = true;
		String name = JOptionPane.showInputDialog(AsteroidGameBoard.this, "Please enter your name", "Enter player name",
				JOptionPane.QUESTION_MESSAGE);

		String[] options = { "Easy", "Medium", "Hard" };

		if (name != null) {
			name = name.trim();

			if (name.length() >= 3) {

				int option = JOptionPane.showOptionDialog(AsteroidGameBoard.this, new String("Select level:"), "",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

				if (option == 0) {
					InitialScreen.setLevel("Easy");
				}
				if (option == 1) {
					InitialScreen.setLevel("Medium");
				}
				if (option == 2) {
					InitialScreen.setLevel("Hard");
				}

				if (option != -1) {
					playerName = name;
					initiateHighScoresFiles();
					try {
						clip = AudioSystem.getClip();
					} catch (LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Utilities.playBackgroundMusic(clip, "./sounds/background.wav");
					freezers.clear();
					slowTimers.clear();
					generateAsteroids();
					paused = false;
					timePassed = 0;
					generate = true;
					gameOver = false;
					closed = false;
					collisionPoint = null;
					ship = new SpaceShip();
				}

				else {
					paused = false;
				}
			} else {
				JOptionPane.showMessageDialog(AsteroidGameBoard.this, "Name is too short! (min 3 letters)", null,
						JOptionPane.ERROR_MESSAGE);
				paused = false;
			}
		} else {
			paused = false;
		}

	}

	public static void generateAsteroids() {

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
			int randomXInitialPos = (int) (Math.random() * (frameWidth - 50)) + 21;
			int randomYInitialPos = (int) (Math.random() * (frameHeight - 40)) + 16;

			while ((randomXInitialPos >= frameWidth / 2 - 50 && randomXInitialPos <= frameWidth / 2 + 50)
					&& (randomYInitialPos >= frameHeight / 2 - 50 && randomYInitialPos <= frameHeight / 2 + 50)) {
				randomXInitialPos = (int) (Math.random() * (frameWidth - 50)) + 21;
				randomYInitialPos = (int) (Math.random() * (frameHeight - 40)) + 16;
			}

			Asteroid theasteroid = new Asteroid(Asteroid.getInitialXPosition(randomXInitialPos),
					Asteroid.getInitialYPosition(randomYInitialPos), 11);

			asteroids.add(theasteroid);

		}
	}// END of generateAsteroids method

	// Generates a freezer power up every 7 seconds
	public static void generateSlowTimers() {

		executorPU.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				int counter = 0;

				// checking if there is any slow timer on the screen. If not it generates one
				// (every 10 seconds)
				for (SlowTimer s : AsteroidGameBoard.slowTimers) {
					if (s.getOnScreen()) {
						counter++;
					}
				}

				if (counter == 0 && generate) {
					int randomXInitialPos = (int) (Math.random() * (frameWidth - 50)) + 21;

					while ((randomXInitialPos >= frameWidth / 2 - 50 && randomXInitialPos <= frameWidth / 2 + 50)) {
						randomXInitialPos = (int) (Math.random() * (frameWidth - 50)) + 21;
					}

					SlowTimer st = new SlowTimer(SlowTimer.getInitialXPosition(randomXInitialPos));
					slowTimers.add(st);
				}
				counter = 0;
			}
		}, 3, 10, TimeUnit.SECONDS);
	}

	// Generates a freezer power up every 10 seconds
	public static void generateFreezers() {

		executorPU.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				int counter = 0;

				// checking if there is any freezer on the screen. If not it generates one
				// (every 14 seconds)
				for (Freezer f : AsteroidGameBoard.freezers) {
					if (f.getOnScreen()) {
						counter++;
					}
				}

				if (counter == 0 && generate) {
					int randomXInitialPos = (int) (Math.random() * (frameWidth - 50)) + 21;

					while ((randomXInitialPos >= frameWidth / 2 - 50 && randomXInitialPos <= frameWidth / 2 + 50)) {
						randomXInitialPos = (int) (Math.random() * (frameWidth - 50)) + 21;
					}

					Freezer fr = new Freezer(Freezer.getInitialXPosition(randomXInitialPos));
					freezers.add(fr);
				}
				counter = 0;
			}
		}, 7, 14, TimeUnit.SECONDS);
	}// END of generateFreezers method

	/*
	 * Adding gravity force to every asteroid on the board
	 * 
	 * @param thePlanet the planet around which asteroids float
	 **/
	/*
	 * public static void asteroidGravityForce(Planet thePlanet) {
	 * 
	 * Rectangle2D planetBounds = thePlanet.getBounds2D();
	 * 
	 * for (Asteroid asteroid : AsteroidGameBoard.asteroids) { double angleRadians =
	 * Math.atan2(asteroid.getBounds().getCenterY() - thePlanet.getyCenter(),
	 * asteroid.getBounds().getCenterX() - thePlanet.getxCenter()); double
	 * angleDegrees = Math.toDegrees(angleRadians); if (angleDegrees < 0) {
	 * angleDegrees = angleDegrees + 360; }
	 * 
	 * double force = 0.05;
	 * 
	 * if (angleDegrees <= 180) { if (angleDegrees <= 90) { if
	 * (asteroid.getXVelocity() >= -3 && asteroid.getYVelocity() >= -3) {
	 * asteroid.decreaseXVelocity(asteroid.asteroidXMoveAngle(angleDegrees)*force);
	 * asteroid.decreaseYVelocity(asteroid.asteroidYMoveAngle(angleDegrees)*force);
	 * } } else { if (asteroid.getXVelocity() <= 3 && asteroid.getYVelocity() >= -3)
	 * {
	 * asteroid.decreaseXVelocity(asteroid.asteroidXMoveAngle(angleDegrees)*force);
	 * asteroid.decreaseYVelocity(asteroid.asteroidYMoveAngle(angleDegrees)*force);
	 * } } } else if (angleDegrees <= 360) { if (angleDegrees <= 270) { if
	 * (asteroid.getXVelocity() <= 3 && asteroid.getYVelocity() <= 3) {
	 * asteroid.decreaseXVelocity(asteroid.asteroidXMoveAngle(angleDegrees)*force);
	 * asteroid.decreaseYVelocity(asteroid.asteroidYMoveAngle(angleDegrees)*force);
	 * } } else { if (asteroid.getXVelocity() >= -3 && asteroid.getYVelocity() <= 3)
	 * {
	 * asteroid.decreaseXVelocity(asteroid.asteroidXMoveAngle(angleDegrees)*force);
	 * asteroid.decreaseYVelocity(asteroid.asteroidYMoveAngle(angleDegrees)*force);
	 * } } }
	 * 
	 * }
	 * 
	 * }
	 */

	/*
	 * Adds gravity force to a ship
	 * 
	 * @param theShip the ship we add a gravity force to
	 * 
	 * @param thePlanet the planet around which theShip flies
	 */
	public static void gravityForce(SpaceShip theShip, Planet thePlanet) {

		Rectangle2D shipBounds = theShip.getBounds2D();
		Rectangle2D planetBounds = thePlanet.getBounds2D();

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
				if (theShip.getXVelocity() >= (-1) * theShip.MAX_VELOCITY
						&& theShip.getYVelocity() >= (-1) * theShip.MAX_VELOCITY) {
					theShip.decreaseXVelocity(theShip.shipXMoveAngle(theShip.getMovingAngle()) * 0.1);
					theShip.decreaseYVelocity(theShip.shipYMoveAngle(theShip.getMovingAngle()) * force);
				}
			} else {
				if (theShip.getXVelocity() <= theShip.MAX_VELOCITY
						&& theShip.getYVelocity() >= (-1) * theShip.MAX_VELOCITY) {
					theShip.decreaseXVelocity(theShip.shipXMoveAngle(theShip.getMovingAngle()) * 0.1);
					theShip.decreaseYVelocity(theShip.shipYMoveAngle(theShip.getMovingAngle()) * force);
				}
			}
		} else {
			if (angleDegrees <= 270) {
				if (theShip.getXVelocity() <= theShip.MAX_VELOCITY && theShip.getYVelocity() <= theShip.MAX_VELOCITY) {
					theShip.decreaseXVelocity(theShip.shipXMoveAngle(theShip.getMovingAngle()) * 0.1);
					theShip.decreaseYVelocity(theShip.shipYMoveAngle(theShip.getMovingAngle()) * force);
				}
			} else {
				if (theShip.getXVelocity() >= (-1) * theShip.MAX_VELOCITY
						&& theShip.getYVelocity() <= theShip.MAX_VELOCITY) {
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

	public static void getHighScores() {
		try {

			fr = new FileReader(highScores);
			br = new BufferedReader(fr);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		scoreTimes = new ArrayList<Double>();
		scoresTotal = new ArrayList<String>();
		HashMap<Double, String> map = new HashMap<Double, String>();

		String line = null;
		String[] playerScore = new String[2]; // 0 for name, 1 for score

		try {
			line = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (line != null) {
			playerScore = line.split("Time:");
			scoreTimes.add(Double.parseDouble(playerScore[1]));
			map.put(Double.parseDouble(playerScore[1]), playerScore[0]);
			try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Collections.sort(scoreTimes);
		int i = 1;
		for (Double score : scoreTimes) {
			if (i <= 10) {
				scoresTotal.add(i + ". " + map.get(score) + " " + String.format("%.2fs", score / 1000));
				i++;
			}
		}
	}// END of getHighScores method

	// MAIN CODE FOR MAKING A SHIP FLOAT
	public void shipFly() {
		if (AsteroidGameBoard.keyHeld == true && AsteroidGameBoard.getKeyChar == 'd') {
			ship.increaseRotationAngle();
			gravityForce(AsteroidGameBoard.ship, AsteroidGameBoard.thePlanet);
		} else if (AsteroidGameBoard.keyHeld == true && AsteroidGameBoard.getKeyChar == 'a') {
			ship.decreaseRotationAngle();
			gravityForce(AsteroidGameBoard.ship, AsteroidGameBoard.thePlanet);
		} else if (AsteroidGameBoard.keyHeld == true && AsteroidGameBoard.getKeyChar == 'w') {
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
		} else if (AsteroidGameBoard.keyHeld == true && AsteroidGameBoard.getKeyChar == 's') {
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
			gravityForce(AsteroidGameBoard.ship, AsteroidGameBoard.thePlanet);
		}
		ship.move();
	}// END OF THE shipFly method

	/*
	 * Inner class responsible for displaying game as a JComponent
	 */
	class ComponentCreator extends JComponent {

		JLabel gameOverLabel = new JLabel("GAME OVER");

		public ComponentCreator() {

			ship = new SpaceShip();
			ship.setYCenter(ship.getYCenter() - 70);
			thePlanet = new Planet("Venus", 60, Color.BLUE, 0.1);
			generateAsteroids();
			generateFreezers();
			generateSlowTimers();

			// reading background image
			try {
				spaceImage = ImageIO.read(new File("./images/space.jpeg"));
				explosionImage = ImageIO.read(new File("./images/explosion.gif"));
				asteroidImage = ImageIO.read(new File("./images/asteroid.gif"));
				spaceShipImage = ImageIO.read(new File("./images/spaceship.gif"));
				earthImage = ImageIO.read(new File("./images/earth.gif"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		/*
		 * Overrided method from JComponent class that is refreshed all game long to
		 * presents current game's stage (non-Javadoc)
		 * 
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 * 
		 * @param g Graphics object essential to display graphical content
		 */
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			AffineTransform identity = new AffineTransform();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// draw the background
			g2.drawImage(spaceImage, 0, 0, frameWidth, frameHeight, this);

			// Informing a player about remaining lives
			g2.setPaint(Color.GRAY);
			g2.drawRect(frameWidth - 172, 20, 152, 22);
			g2.setPaint(Color.RED);
			g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
			g2.drawString("HP", frameWidth - 200, 37);
			Rectangle lifeRect = new Rectangle(frameWidth - 171, 21, 30 * ship.getLives(), 20);
			g2.draw(lifeRect);
			g2.fill(lifeRect);

			// Informing a player about asteroid left
			g2.setPaint(Color.GRAY);
			g2.drawString("Asteroids left: " + Integer.toString(aLeft), 30, 37);

			// checking if a game is paused
			if (!paused) {
				if (!gameOver && ship.getLives() > 0 && ifAnyAsteroidsLeft(asteroids)) {
					timePassed += 15;
					clip.loop(1);
					// Drawing a planet
					/*
					 * g2.setPaint(thePlanet.getPlanetColor()); g2.draw(thePlanet);
					 * g2.fill(thePlanet); g2.setPaint(Color.RED); int stringWidth =
					 * g2.getFontMetrics().stringWidth(thePlanet.getPlanetName());
					 * g2.drawString(thePlanet.getPlanetName(), (int) thePlanet.getxCenter() -
					 * stringWidth / 2 + 2, (int) thePlanet.getyCenter() + 5);
					 */

					g2.drawImage(earthImage, (int) thePlanet.getX(), (int) thePlanet.getY(), (int) thePlanet.getWidth(),
							(int) thePlanet.getHeight(), null);

					aLeft = 0;

					for (Asteroid asteroid : asteroids) {
						if (asteroid.getOnScreen()) {
							if (!freeze) {

								// don't generate new powerups while using one
								generate = true;

								Point pt = Collision.AsteroidShipDetection(asteroid, ship, asteroids);
								if (pt != null) {
									collisionPoint = pt;
									startTime = timePassed;
								}

								asteroid.move();

								// asteroidGravityForce(thePlanet);
							} else {
								// don't generate new powerups while using one
								generate = false;
							}
							g2.setPaint(Color.LIGHT_GRAY);
							// g2.draw(asteroid);
							// g2.setPaint(new Color(108, 67, 40));
							// g2.fill(asteroid);
							g2.drawImage(asteroidImage, asteroid.getBounds().x, asteroid.getBounds().y,
									asteroid.getWidth(), asteroid.getHeight(), this);
							aLeft++;
						}
					}

					if (collisionPoint != null && timePassed - startTime < 1000) {
						float time = 1000 - (timePassed - startTime);
						float opacity = time / (float) 1000;
						g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
						g2.drawImage(explosionImage, collisionPoint.x, collisionPoint.y, 50, 50, this);
					}

					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					for (Freezer fr : AsteroidGameBoard.freezers) {
						if (fr.getOnScreen()) {
							fr.move();
							g2.setPaint(Color.GREEN);
							g2.draw(fr);
							g2.fill(fr);
						}
					}

					for (SlowTimer st : AsteroidGameBoard.slowTimers) {
						if (st.getOnScreen()) {
							st.move();
							g2.setPaint(Color.BLUE);
							g2.draw(st);
							g2.fill(st);
						}
					}

					// Informing a player about time that have passed since the beginning
					g2.setPaint(Color.GRAY);
					timePassedString = String.format("%.2fs", timePassed / 1000);
					g2.drawString(timePassedString, frameWidth - 100, frameHeight - 30);

					shipFly();

					// Informing player about the speed of a ship
					g2.setPaint(Color.GRAY);
					g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
					g2.drawString("Speed:", 20, frameHeight - 20);
					g2.drawRect(95, frameHeight - 37, 162, 22);
					g2.setPaint(Color.CYAN);
					Rectangle speedRect = new Rectangle(96, frameHeight - 36,
							(int) Math.max(Math.abs((int) ship.getXVelocity()), Math.abs((int) ship.getYVelocity()))
									* 40,
							20);
					g2.draw(speedRect);
					g2.fill(speedRect);

					// making moving smooth
					g2.setTransform(identity);

					// drawing a ship in different places
					g2.translate(ship.getXCenter(), ship.getYCenter());

					// Rotates the ship
					g2.rotate(Math.toRadians(ship.getRotationAngle()));

					/*
					 * g2.setPaint(new Color(32, 32, 32)); g2.draw(ship); g2.setPaint(new Color(152,
					 * 152, 152)); g2.fill(ship);
					 */

					g2.drawImage(spaceShipImage, ship.xpoints[0], ship.ypoints[0], ship.getWidth(), ship.getHeight(),
							this);
					// Drawing bullets
					g2.setPaint(Color.RED);
					for (Bullet bullet : AsteroidGameBoard.bulletList) {

						if (bullet.getOnScreen()) {
							bullet.move();
							g2.setTransform(identity);
							g2.translate(bullet.getXCenter(), bullet.getYCenter());
							g2.draw(bullet);
							g2.setPaint(new Color(255, 252, 0));
							g2.fill(bullet);

							Collision.BulletDetection(bullet, asteroids);

						}
					}

				} else if (ship.getLives() <= 0) {
					Utilities.stopBackgroundMusic(clip);

					gameOverLabel.setText("Game over!");

					g2.setColor(Color.RED);
					g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 46));
					int stringWidth = g2.getFontMetrics().stringWidth(gameOverLabel.getText());
					g2.drawString(gameOverLabel.getText(), (int) ((frameWidth / 2) - (stringWidth / 2)),
							(int) (frameHeight / 2));
					gameOver = true;

				} else if (!ifAnyAsteroidsLeft(asteroids)) {
					Utilities.stopBackgroundMusic(clip);

					if (!closed) {
						try {
							bw.write(playerName + "Time:" + timePassed);
							bw.newLine();
							bw.flush();
							closed = true;

							getHighScores();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					g2.setColor(new Color(103, 255, 103));
					g2.setFont(new Font("Aharoni", Font.BOLD, 30));
					g2.drawString("HIGH SCORES", frameWidth / 2 - 80, frameHeight / 4);

					// g2.setColor(new Color(170, 250, 170));
					g2.setColor(new Color(240, 240, 240));
					g2.setFont(new Font("Aharoni", Font.PLAIN, 20));
					g2.drawString(InitialScreen.getLevel() + " level", frameWidth / 2 - 80, frameHeight / 4 + 20);

					g2.setColor(new Color(50, 200, 50));
					g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
					for (int i = 0; i < scoresTotal.size(); i++) {
						g2.drawString(scoresTotal.get(i), (int) (frameWidth / 2 - 75),
								(int) (frameHeight / 3 + i * 30));
					}
					gameOver = true;
				}
			}

			// if game is paused
			else {
				clip.stop();
				g2.setPaint(Color.YELLOW);
				g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 46));
				int stringWidth = g2.getFontMetrics().stringWidth("Paused");
				g2.drawString("Paused", (int) ((frameWidth / 2) - (stringWidth / 2)), (int) (frameHeight / 2));
			}
		}
	}

}