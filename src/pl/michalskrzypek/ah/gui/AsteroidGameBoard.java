package pl.michalskrzypek.ah.gui;

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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
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

import pl.michalskrzypek.ah.gui.events.KeyManager;
import pl.michalskrzypek.ah.logic.Collision;
import pl.michalskrzypek.ah.spaceobjects.Asteroid;
import pl.michalskrzypek.ah.spaceobjects.Bullet;
import pl.michalskrzypek.ah.spaceobjects.Planet;
import pl.michalskrzypek.ah.spaceobjects.SpaceShip;
import pl.michalskrzypek.ah.spaceobjects.powerups.Freezer;
import pl.michalskrzypek.ah.spaceobjects.powerups.SlowTimer;
import pl.michalskrzypek.ah.utils.SoundUtil;

/*
 * Class responsible for the main game functions and thread managing
 */
public class AsteroidGameBoard extends JFrame {

	public static final String GAME_TITLE = "Asteroid Hunter";
	public static final int FRAME_WIDTH = 1000;
	public static final int FRAME_HEIGHT = 600;

	public static SpaceShip ship;
	public static Planet thePlanet = null;
	public static ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
	public static ArrayList<Bullet> bulletList = new ArrayList<Bullet>();
	public static ArrayList<Freezer> freezers = new ArrayList<Freezer>();
	public static ArrayList<SlowTimer> slowTimers = new ArrayList<SlowTimer>();
	public static boolean keyHeld = false;
	public static char keyChar;
	public static boolean gameOver;
	public static boolean paused;
	public static int pausedTimes = 0;
	public static int asteroidsLeft = 8;
	public static float timePassed = 0;
	public static String livesLeft;
	public static String timePassedString;

	private static String playerName;
	private static ArrayList<Double> scoreTimes;
	private static ArrayList<String> scoresTotal;
	private static boolean closed;
	private static boolean generate;
	private static Point collisionPoint;
	private static float startTime;
	private static ScheduledThreadPoolExecutor executor, executorPU;
	private static File highScores;
	private static FileWriter fw;
	private static BufferedWriter bw;
	private static FileReader fr;
	private static BufferedReader br;
	private static Clip clip;
	private Image spaceImage, explosionImage, asteroidImage, spaceShipImage, earthImage;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem playAgainItem, goToMenuItem, exitItem;
	private ComponentCreator comp;

	public AsteroidGameBoard(String pName) {
		playBackgroundMusic();

		playerName = pName;
		generate = true;
		paused = false;
		closed = false;
		gameOver = false;
		timePassed = 0;
		startTime = 0;

		// initializing executor for generating power ups
		executorPU = new ScheduledThreadPoolExecutor(5);

		addKeyListener(new KeyManager());

		comp = new ComponentCreator();
		add(comp, BorderLayout.CENTER);

		// initializing executor for repeating paint method in ComponentCreator class
		executor = new ScheduledThreadPoolExecutor(5);
		executor.scheduleAtFixedRate(() -> {
			comp.repaint();
		}, 0L, 15L, TimeUnit.MILLISECONDS);

		initializeMenu();
		initiateHighScoresFiles();

		setTitle(GAME_TITLE);
		setSize(FRAME_WIDTH + 15, FRAME_HEIGHT + 60);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private static void playBackgroundMusic() {
		try {
			clip = AudioSystem.getClip();
			SoundUtil.playBackgroundMusic(clip, "./sounds/background.wav");
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		}
	}

	private void initializeMenu() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		playAgainItem = new JMenuItem(new AbstractAction("Play Again") {
			public void actionPerformed(ActionEvent e) {
//				SoundUtil.stopBackgroundMusic();
				playBackgroundMusic();
				playAgain();
			}
		});
		fileMenu.add(playAgainItem);

		goToMenuItem = new JMenuItem(new AbstractAction("Go back") {
			public void actionPerformed(ActionEvent e) {
				AsteroidGameBoard.this.dispose();
				SoundUtil.stopBackgroundMusic(clip);
				executor.shutdown();
				executorPU.shutdown();
				InitialScreen initScreen = new InitialScreen();
				initScreen.setVisible(true);
			}
		});
		fileMenu.add(goToMenuItem);
		fileMenu.addSeparator();

		exitItem = new JMenuItem(new AbstractAction("Exit game") {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);
		this.setJMenuBar(menuBar);
	}

	public static boolean ifAnyAsteroidsLeft(ArrayList<Asteroid> asteroidList) {
		long asteroidsOnScreen = asteroidList.stream().filter(a -> a.getOnScreen()).count();
		if (asteroidsOnScreen == 0) {
			return false;
		}
		return true;
	}

	public void playAgain() {
		paused = true;
		Optional<String> name = Optional.ofNullable(JOptionPane.showInputDialog(this, "Please enter your name",
				"Enter player name", JOptionPane.QUESTION_MESSAGE));

		if (name.isPresent()) {
			name.map(n -> n.trim()).filter(n -> n.length() >= 3).ifPresentOrElse(n -> {
				int option = JOptionPane.showOptionDialog(AsteroidGameBoard.this, new String("Select level:"), "",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, InitialScreen.GAME_LEVELS,
						InitialScreen.GAME_LEVELS[0]);
				if (option != -1) {
					InitialScreen.setLevel(option);
					playerName = n;
					initiateHighScoresFiles();
					initiateInitialValues();
				} else {
					paused = false;
				}
			}, () -> {
				JOptionPane.showMessageDialog(AsteroidGameBoard.this, "Name is too short! (min 3 letters)", null,
						JOptionPane.ERROR_MESSAGE);
				paused = false;
			});
		} else {
			paused = false;
		}
	}

	private static void initiateHighScoresFiles() {
		File highScoresEasy = new File("./scores/high_scores_easy.txt");
		File highScoresMedium = new File("./scores/high_scores_medium.txt");
		File highScoresHard = new File("./scores/high_scores_hard.txt");

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
			e1.printStackTrace();
		}
	}

	private void initiateInitialValues() {
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
			int randomXInitialPos = (int) (Math.random() * (FRAME_WIDTH - 50)) + 21;
			int randomYInitialPos = (int) (Math.random() * (FRAME_HEIGHT - 40)) + 16;

			while ((randomXInitialPos >= FRAME_WIDTH / 2 - 50 && randomXInitialPos <= FRAME_WIDTH / 2 + 50)
					&& (randomYInitialPos >= FRAME_HEIGHT / 2 - 50 && randomYInitialPos <= FRAME_HEIGHT / 2 + 50)) {
				randomXInitialPos = (int) (Math.random() * (FRAME_WIDTH - 50)) + 21;
				randomYInitialPos = (int) (Math.random() * (FRAME_HEIGHT - 40)) + 16;
			}

			Asteroid theasteroid = new Asteroid(Asteroid.getInitialXPosition(randomXInitialPos),
					Asteroid.getInitialYPosition(randomYInitialPos));

			asteroids.add(theasteroid);
		}
	}// END of generateAsteroids method

	// Generates a freezer power up every 7 seconds
	public static void generateSlowTimers() {
		executorPU.scheduleAtFixedRate(() -> {
			long counter = slowTimers.stream().filter(s -> s.getOnScreen()).count();

			if (counter == 0 && generate) {
				int randomXInitialPos = (int) (Math.random() * (FRAME_WIDTH - 50)) + 21;

				while ((randomXInitialPos >= FRAME_WIDTH / 2 - 50 && randomXInitialPos <= FRAME_WIDTH / 2 + 50)) {
					randomXInitialPos = (int) (Math.random() * (FRAME_WIDTH - 50)) + 21;
				}

				SlowTimer st = new SlowTimer(SlowTimer.getInitialXPosition(randomXInitialPos));
				slowTimers.add(st);
			}
			counter = 0;
		}, 3, 10, TimeUnit.SECONDS);
	}

	// Generates a freezer power up every 10 seconds
	public static void generateFreezers() {
		executorPU.scheduleAtFixedRate(() -> {
			long counter = freezers.stream().filter(f -> f.getOnScreen()).count();
			if (counter == 0 && generate) {
				int randomXInitialPos = (int) (Math.random() * (FRAME_WIDTH - 50)) + 21;

				while ((randomXInitialPos >= FRAME_WIDTH / 2 - 50 && randomXInitialPos <= FRAME_WIDTH / 2 + 50)) {
					randomXInitialPos = (int) (Math.random() * (FRAME_WIDTH - 50)) + 21;
				}
				Freezer fr = new Freezer(Freezer.getInitialXPosition(randomXInitialPos));
				freezers.add(fr);
			}
			counter = 0;
		}, 7, 14, TimeUnit.SECONDS);
	}// END of generateFreezers method

	/*
	 * Adds gravity force to a ship
	 * 
	 * @param theShip the ship we add a gravity force to
	 * 
	 * @param thePlanet the planet around which theShip flies
	 */
	public static void gravityForce(SpaceShip theShip, Planet thePlanet) {
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

	public static void getHighScores() throws NumberFormatException, IOException {
		fr = new FileReader(highScores);
		br = new BufferedReader(fr);
		scoreTimes = new ArrayList<Double>();
		scoresTotal = new ArrayList<String>();
		HashMap<Double, String> map = new HashMap<Double, String>();
		String line = null;
		String[] playerScore = new String[2]; // 0 for name, 1 for score

		while ((line = br.readLine()) != null) {
			playerScore = line.split("Time:");
			String pName = playerScore[0];
			double pScore = Double.parseDouble(playerScore[1]);
			scoreTimes.add(pScore);
			map.put(pScore, pName);
		}

		Collections.sort(scoreTimes);
		for (double score : scoreTimes) {
			int index = scoreTimes.indexOf(score);
			scoresTotal.add(index + 1 + ". " + map.get(score) + " " + String.format("%.2fs", score / 1000));
		}
	}// END of getHighScores method

	// MAIN CODE FOR MAKING A SHIP FLOAT
	public void shipFly() {
		if (AsteroidGameBoard.keyHeld == true && AsteroidGameBoard.keyChar == 'd') {
			ship.increaseRotationAngle();
			gravityForce(AsteroidGameBoard.ship, AsteroidGameBoard.thePlanet);
		} else if (AsteroidGameBoard.keyHeld == true && AsteroidGameBoard.keyChar == 'a') {
			ship.decreaseRotationAngle();
			gravityForce(AsteroidGameBoard.ship, AsteroidGameBoard.thePlanet);
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
			gravityForce(AsteroidGameBoard.ship, AsteroidGameBoard.thePlanet);
		}
		ship.move();
	}// END OF THE shipFly method

	class ComponentCreator extends JComponent {

		private Graphics2D g2;
		private AffineTransform identity;
		private JLabel gameOverLabel;

		public ComponentCreator() {
			gameOverLabel = new JLabel("GAME OVER");
			identity = new AffineTransform();

			ship = new SpaceShip();
			ship.setYCenter(ship.getYCenter() - 70);
			thePlanet = new Planet("Venus", 60, Color.BLUE, 0.1);
			generateAsteroids();
			generateFreezers();
			generateSlowTimers();

			try {
				spaceImage = ImageIO.read(new File("./images/space.jpeg"));
				explosionImage = ImageIO.read(new File("./images/explosion.gif"));
				asteroidImage = ImageIO.read(new File("./images/asteroid.gif"));
				spaceShipImage = ImageIO.read(new File("./images/spaceship.gif"));
				earthImage = ImageIO.read(new File("./images/earth.gif"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		/**
		 * Overrided method from JComponent class that is refreshed all game long to
		 * presents current game's stage (non-Javadoc)
		 * 
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 * 
		 * @param g Graphics object essential to display graphical content
		 */
		public void paintComponent(Graphics g) {
			g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// draw the background
			g2.drawImage(spaceImage, 0, 0, FRAME_WIDTH, FRAME_HEIGHT, this);

			drawRemainingLives();
			drawAsteroidsLeft();

			if (!paused) {
				if (!gameOver && ship.getLives() > 0 && ifAnyAsteroidsLeft(asteroids)) {
					timePassed += 15;
					clip.loop(1);
					g2.drawImage(earthImage, (int) thePlanet.getX(), (int) thePlanet.getY(), (int) thePlanet.getWidth(),
							(int) thePlanet.getHeight(), null);

					asteroidsLeft = 0;

					drawAsteroids();
					drawExplossion();

					drawFreezers();
					drawSlowTimers();
					drawTimePassed();

					shipFly();
					drawSpeedOfShip();

					// making movement smooth
					g2.setTransform(identity);
					// drawing a ship in different places
					g2.translate(ship.getXCenter(), ship.getYCenter());
					// Rotates the ship
					g2.rotate(Math.toRadians(ship.getRotationAngle()));
					g2.drawImage(spaceShipImage, ship.xpoints[0], ship.ypoints[0], ship.getWidth(), ship.getHeight(),
							this);

					drawBullets();

				} else if (ship.getLives() <= 0) {
					Thread th = new Thread(() -> {
						SoundUtil.stopBackgroundMusic(clip);
					});
					th.start();

					gameOverLabel.setText("Game over!");

					g2.setColor(Color.RED);
					g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 46));
					int stringWidth = g2.getFontMetrics().stringWidth(gameOverLabel.getText());
					g2.drawString(gameOverLabel.getText(), (int) ((FRAME_WIDTH / 2) - (stringWidth / 2)),
							(int) (FRAME_HEIGHT / 2));
					gameOver = true;

				} else if (!ifAnyAsteroidsLeft(asteroids)) {
					Thread th = new Thread(() -> {
						SoundUtil.stopBackgroundMusic(clip);
					});
					th.start();

					if (!closed) {
						try {
							bw.write(playerName + "Time:" + timePassed);
							bw.newLine();
							bw.flush();
							closed = true;

							getHighScores();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					g2.setColor(new Color(103, 255, 103));
					g2.setFont(new Font("Aharoni", Font.BOLD, 30));
					g2.drawString("HIGH SCORES", FRAME_WIDTH / 2 - 80, FRAME_HEIGHT / 4);

					g2.setColor(new Color(240, 240, 240));
					g2.setFont(new Font("Aharoni", Font.PLAIN, 20));
					g2.drawString(InitialScreen.getLevel() + " level", FRAME_WIDTH / 2 - 80, FRAME_HEIGHT / 4 + 20);

					g2.setColor(new Color(50, 200, 50));
					g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 17));
					for (int i = 0; i < scoresTotal.size(); i++) {
						g2.drawString(scoresTotal.get(i), (int) (FRAME_WIDTH / 2 - 75),
								(int) (FRAME_HEIGHT / 3 + i * 30));
					}
					gameOver = true;
				}
			}
			// if game is paused
			else {
				SoundUtil.stopBackgroundMusic(clip);
				g2.setPaint(Color.YELLOW);
				g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 46));
				int stringWidth = g2.getFontMetrics().stringWidth("Paused");
				g2.drawString("Paused", (int) ((FRAME_WIDTH / 2) - (stringWidth / 2)), (int) (FRAME_HEIGHT / 2));
			}
		}

		private void drawRemainingLives() {
			g2.setPaint(Color.GRAY);
			g2.drawRect(FRAME_WIDTH - 172, 20, 152, 22);
			g2.setPaint(Color.RED);
			g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
			g2.drawString("HP", FRAME_WIDTH - 200, 37);
			Rectangle lifeRect = new Rectangle(FRAME_WIDTH - 171, 21, 30 * ship.getLives(), 20);
			g2.draw(lifeRect);
			g2.fill(lifeRect);
		}

		private void drawAsteroidsLeft() {
			g2.setPaint(Color.GRAY);
			g2.drawString("Asteroids left: " + Integer.toString(asteroidsLeft), 30, 37);
		}

		private void drawAsteroids() {
			for (Asteroid asteroid : asteroids) {
				if (asteroid.getOnScreen()) {
					if (!Freezer.freeze) {
						// don't generate new powerups while using one
						generate = true;

						Point pt = Collision.AsteroidShipDetection(asteroid, ship, asteroids);
						if (pt != null) {
							collisionPoint = pt;
							startTime = timePassed;
						}
						asteroid.move();
					} else {
						// don't generate new powerups while using one
						generate = false;
					}
					g2.setPaint(Color.LIGHT_GRAY);
					g2.drawImage(asteroidImage, asteroid.getBounds().x, asteroid.getBounds().y, asteroid.getWidth(),
							asteroid.getHeight(), this);
					asteroidsLeft++;
				}
			}
		}

		private void drawFreezers() {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			for (Freezer fr : AsteroidGameBoard.freezers) {
				if (fr.getOnScreen()) {
					fr.move();
					g2.setPaint(Color.GREEN);
					g2.draw(fr);
					g2.fill(fr);
				}
			}
		}

		private void drawSlowTimers() {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			for (SlowTimer st : AsteroidGameBoard.slowTimers) {
				if (st.getOnScreen()) {
					st.move();
					g2.setPaint(Color.BLUE);
					g2.draw(st);
					g2.fill(st);
				}
			}
		}

		private void drawTimePassed() {
			g2.setPaint(Color.GRAY);
			timePassedString = String.format("%.2fs", timePassed / 1000);
			g2.drawString(timePassedString, FRAME_WIDTH - 100, FRAME_HEIGHT - 30);
		}

		private void drawSpeedOfShip() {
			g2.setPaint(Color.GRAY);
			g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
			g2.drawString("Speed:", 20, FRAME_HEIGHT - 20);
			g2.drawRect(95, FRAME_HEIGHT - 37, 162, 22);
			g2.setPaint(Color.CYAN);
			Rectangle speedRect = new Rectangle(96, FRAME_HEIGHT - 36,
					(int) Math.max(Math.abs((int) ship.getXVelocity()), Math.abs((int) ship.getYVelocity())) * 40, 20);
			g2.draw(speedRect);
			g2.fill(speedRect);
		}

		private void drawBullets() {
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
		}

		private void drawExplossion() {
			if (collisionPoint != null && timePassed - startTime < 1000) {
				float time = 1000 - (timePassed - startTime);
				float opacity = time / (float) 1000;
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
				g2.drawImage(explosionImage, collisionPoint.x, collisionPoint.y, 50, 50, this);
			}
		}
	}
}