package asteroidHunter;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import powerUps.SlowTimer;

public class AsteroidGameBoard extends JFrame {

	public static SpaceShip ship;
	public static int frameWidth = 1000;
	public static int frameHeight = 600;
	public static boolean keyHeld = false;
	public static char getKeyChar;
	public static final int maxBullets = 5;
	public static int currentBullets = 0;
	public ScheduledThreadPoolExecutor executor;
	public static ScheduledThreadPoolExecutor executor1;
	public static JMenuBar menuBar;
	public JMenu fileMenu;
	public JMenuItem playAgainItem, exitItem;
	public static boolean gameOver;
	public ComponentCreator comp;
	public Image spaceImage;
	public static String livesLeft;
	public static ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
	public static ArrayList<Bullet> bulletList = new ArrayList<Bullet>();
	public static ArrayList<SlowTimer> slowTimers = new ArrayList<SlowTimer>();
	public static boolean paused = false;
	public static int pausedTimes = 0;
	public static int aLeft = 10;
	public static boolean slowTime = false;
	public static float timePassed = 0;
	public String timePassedString;
	private String playerName;
	public static Planet thePlanet = null;
	private File highScores;
	private FileWriter fw;
	private BufferedWriter bw;
	private FileReader fr;
	private BufferedReader br;
	private ArrayList<String> scoreNames;
	private ArrayList<Double> scoreTimes;
	private ArrayList<String> scoresTotal;
	static boolean closed;

	public AsteroidGameBoard(String playerName) {
		closed = false;
		this.playerName = playerName;
		try {
			spaceImage = ImageIO.read(new File("./images/space.jpeg"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.setSize(frameWidth + 15, frameHeight + 60);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("Asteroid Hunter");
		try {
			highScores = new File("./src/high_scores.txt");
			fr = new FileReader(highScores);
			br = new BufferedReader(fr);
			fw = new FileWriter(highScores, true);
			bw = new BufferedWriter(fw);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		scoreNames = new ArrayList<String>();
		scoreTimes = new ArrayList<Double>();
		scoresTotal = new ArrayList<String>();
		gameOver = false;
		comp = new ComponentCreator();
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				keyHeld = false;
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					System.out.println("Fire in the hole!");
					if (currentBullets < maxBullets) {
						bulletList.add(new Bullet(ship.getShipNoseX(), ship.getShipNoseY(), ship.getRotationAngle()));
						currentBullets++;
						playSound("./sounds/laser.aiff");

					}
				} else if (e.getKeyChar() == 'p') {
					if (pausedTimes % 2 == 0) {
						paused = true;
						pausedTimes++;
					} else {
						paused = false;
						pausedTimes--;
					}

				} else if (e.getKeyChar() == 'w' || e.getKeyChar() == 'a' || e.getKeyChar() == 's'
						|| e.getKeyChar() == 'd') {
					getKeyChar = e.getKeyChar();
					keyHeld = true;
				}
			}
		});

		this.add(comp, BorderLayout.CENTER);

		executor = new ScheduledThreadPoolExecutor(5);
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				comp.repaint();
				timePassed += 15;
			}
		}, 0L, 15L, TimeUnit.MILLISECONDS);

		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		playAgainItem = new JMenuItem(new AbstractAction("Play Again") {

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(AsteroidGameBoard.this, "Please enter your name",
						"Enter player name", JOptionPane.QUESTION_MESSAGE);
				if (name != null) {
					setPlayerName(name);
					playAgain();
				}

			}
		});
		fileMenu.add(playAgainItem);
		exitItem = new JMenuItem(new AbstractAction("Exit game") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);

		this.setJMenuBar(menuBar);
		this.setVisible(true);
	}

	public static void playSound(String soundPath) {
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(soundPath));
			clip.open(inputStream);
			clip.loop(0);
			clip.start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public static void playAgain() {
		asteroids.clear();
		timePassed = 0;
		generateAsteroids();
		generateSlowTimers();
		ship.setXVelocity(2);
		ship.setYVelocity(0);
		ship.setXCenter(frameWidth / 2);
		ship.setYCenter(frameHeight / 2 - 70);
		ship.setMovingAngle(0);
		ship.setRotationAngle(0);
		ship.setLives(5);
		gameOver = false;
		closed = false;
		// playAgainButton.setVisible(false);

	}

	public static void generateAsteroids() {

		int numb = 10;
		if (InitialScreen.getLevel().equals("Easy")) {
			numb = 8;
			Asteroid.setSpeed(2);
		} else if (InitialScreen.getLevel().equals("Medium")) {
			numb = 10;
			Asteroid.setSpeed(3);
		} else if (InitialScreen.getLevel().equals("Hard")) {
			numb = 12;
			Asteroid.setSpeed(4);
		}
		for (int i = 0; i < 0; i++) {
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
	}

	// Method generateSlowTimers() is used to generate a slow timer power up
	// every 5 seconds
	public static void generateSlowTimers() {

		executor1 = new ScheduledThreadPoolExecutor(5);
		executor1.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int randomXInitialPos = (int) (Math.random() * (frameWidth - 50)) + 21;

				while ((randomXInitialPos >= frameWidth / 2 - 50 && randomXInitialPos <= frameWidth / 2 + 50)) {
					randomXInitialPos = (int) (Math.random() * (frameWidth - 50)) + 21;
				}

				SlowTimer st = new SlowTimer(SlowTimer.getInitialXPosition(randomXInitialPos),
						SlowTimer.polygonYCoordinates, 5);

				slowTimers.add(st);
			}
		}, 0, 5, TimeUnit.SECONDS);
	}

	public static void gravityForce(SpaceShip theShip, Planet thePlanet) {

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
					theShip.decreaseYVelocity(theShip.shipYMoveAngle(theShip.getMovingAngle()) * 0.1);
				}
			} else {
				if (theShip.getXVelocity() <= theShip.MAX_VELOCITY
						&& theShip.getYVelocity() >= (-1) * theShip.MAX_VELOCITY) {
					theShip.decreaseXVelocity(theShip.shipXMoveAngle(theShip.getMovingAngle()) * 0.1);
					theShip.decreaseYVelocity(theShip.shipYMoveAngle(theShip.getMovingAngle()) * 0.1);
				}
			}
		} else {
			if (angleDegrees <= 270) {
				if (theShip.getXVelocity() <= theShip.MAX_VELOCITY && theShip.getYVelocity() <= theShip.MAX_VELOCITY) {
					theShip.decreaseXVelocity(theShip.shipXMoveAngle(theShip.getMovingAngle()) * 0.1);
					theShip.decreaseYVelocity(theShip.shipYMoveAngle(theShip.getMovingAngle()) * 0.1);
				}
			} else {
				if (theShip.getXVelocity() >= (-1) * theShip.MAX_VELOCITY
						&& theShip.getYVelocity() <= theShip.MAX_VELOCITY) {
					theShip.decreaseXVelocity(theShip.shipXMoveAngle(theShip.getMovingAngle()) * 0.1);
					theShip.decreaseYVelocity(theShip.shipYMoveAngle(theShip.getMovingAngle()) * 0.1);
				}
			}
		}

		if (thePlanet.contains(theShip.getBounds2D())) {
			theShip.setXVelocity(0);
			theShip.setYVelocity(0);
		}
	}

	public void getHighScores(BufferedReader bReader) {
		String line = null;
		String[] playerScore = new String[2];
		HashMap<Double, String> map = new HashMap<Double, String>();
		try {
			line = bReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (line != null) {
			playerScore = line.split("Time:");
			scoreNames.add(playerScore[0]);
			scoreTimes.add(Double.parseDouble(playerScore[1]));
			map.put(Double.parseDouble(playerScore[1]), playerScore[0]);
			try {
				line = bReader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		StringBuffer sb = new StringBuffer("");
		String lol = "";

		Collections.sort(scoreTimes);
		int i = 1;
		for (Double score : scoreTimes) {
			if(i<=10) {
				scoresTotal.add(i + ". " + map.get(score) + " " + String.format("%.2fs", score/1000));
				i++;
}}
		}
	

	class ComponentCreator extends JComponent {

		JLabel gameOverLabel = new JLabel("GAME OVER");

		public ComponentCreator() {

			ship = new SpaceShip();
			ship.setYCenter(ship.getYCenter() - 70);
			thePlanet = new Planet("Earth", 60, Color.BLUE, 1);
			generateAsteroids();
			generateSlowTimers();

		}

		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			AffineTransform identity = new AffineTransform();
			g2.drawImage(spaceImage, 0, 0, frameWidth, frameHeight, this);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// Informing player about remaining lives
			g2.setPaint(Color.GRAY);
			g2.drawRect(frameWidth - 172, 20, 152, 22);
			g2.setPaint(Color.RED);
			g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
			g2.drawString("HP", frameWidth - 200, 37);
			Rectangle lifeRect = new Rectangle(frameWidth - 171, 21, 30 * ship.getLives(), 20);
			g2.draw(lifeRect);
			g2.fill(lifeRect);

			g2.setPaint(Color.GRAY);
			g2.drawString("Asteroids left: " + Integer.toString(aLeft), 30, 37);

			// checking if a game is paused
			if (!paused) {

				if (!gameOver && ship.getLives() > 0 && ifAnyAsteroidsLeft(asteroids)) {

					g2.setPaint(thePlanet.getPlanetColor());
					g2.draw(thePlanet);
					g2.fill(thePlanet);
					g2.setPaint(Color.RED);
					int stringWidth = g2.getFontMetrics().stringWidth(thePlanet.getPlanetName());
					g2.drawString(thePlanet.getPlanetName(), (int) thePlanet.getxCenter() - stringWidth / 2,
							(int) thePlanet.getyCenter());
					/*
					 * Image earth=null; try { earth = ImageIO.read(new File("./images/earth.png"));
					 * } catch (IOException e) { // TODO Auto-generated catch block
					 * e.printStackTrace(); } g2.drawImage(earth, (int)thePlanet.getX(),
					 * (int)thePlanet.getY(), (int)thePlanet.getWidth(),
					 * (int)thePlanet.getHeight(),null);
					 */

					aLeft = 0;
					for (Asteroid asteroid : asteroids) {
						if (asteroid.getOnScreen()) {
							if (!slowTime) {
								asteroid.move();
							}
							g2.setPaint(Color.LIGHT_GRAY);
							g2.draw(asteroid);
							g2.setPaint(new Color(108, 67, 40));
							g2.fill(asteroid);
							aLeft++;
						}
					}

					for (SlowTimer st : AsteroidGameBoard.slowTimers) {
						if (st.getOnScreen()) {
							st.move();
							g2.setPaint(Color.GREEN);
							g2.draw(st);
							g2.fill(st);
						}
					}
					g2.setPaint(Color.GRAY);
					timePassedString = String.format("%.2fs", timePassed / 1000);
					g2.drawString(timePassedString, frameWidth - 100, frameHeight - 30);

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

					g2.setTransform(identity);

					g2.translate(ship.getXCenter(), ship.getYCenter());
					// Rotates the ship

					g2.rotate(Math.toRadians(ship.getRotationAngle()));
					g2.setPaint(new Color(32, 32, 32));
					g2.draw(ship);
					g2.setPaint(new Color(152, 152, 152));
					g2.fill(ship);
					g2.setPaint(Color.RED);
					for (Bullet bullet : AsteroidGameBoard.bulletList) {

						if (bullet.onScreen) {
							bullet.move();
							g2.setTransform(identity);
							g2.translate(bullet.getXCenter(), bullet.getYCenter());
							g2.draw(bullet);
							g2.setPaint(new Color(255, 252, 0));
							g2.fill(bullet);

							for (Asteroid asteroid : asteroids) {

								if (asteroid.getBounds().contains(bullet.getBounds()) && asteroid.getOnScreen()) {
									asteroid.setOnScreen(false);
									bullet.onScreen = false;
									aLeft--;
									currentBullets--;
								}
							}
						}
					}

				} else if (ship.getLives() <= 0) {
					gameOverLabel.setText("Game over!");
					executor1.shutdown();
					g2.setColor(Color.RED);
					g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 46));
					int stringWidth = g2.getFontMetrics().stringWidth(gameOverLabel.getText());
					g2.drawString(gameOverLabel.getText(), (int) ((frameWidth / 2) - (stringWidth / 2)),
							(int) (frameHeight / 2));
					gameOver = true;
					// playAgainButton.setVisible(true);

				} else if (!ifAnyAsteroidsLeft(asteroids)) {
					if (!closed) {
						try {
							bw.write(playerName + "Time:" + timePassed);
							bw.newLine();
							bw.flush();
							closed = true;
							executor1.shutdown();
							
							getHighScores(br);
							//stringWidth = g2.getFontMetrics().stringWidth(gameOverLabel.getText());

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					g2.setColor(Color.GREEN);
					g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
					// gameOverLabel.setText("You won!\n " + playerName + " in " +
					// timePassedString);
					for (int i = 0; i < scoresTotal.size(); i++) {
						g2.drawString(scoresTotal.get(i), (int) (frameWidth / 2 - 80),
								(int) (frameHeight / 4 + i * 30));
					}
					gameOver = true;
				}
			}
			// if game is paused
			else {
				g2.setPaint(Color.YELLOW);
				g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 46));
				int stringWidth = g2.getFontMetrics().stringWidth("Paused");
				g2.drawString("Paused", (int) ((frameWidth / 2) - (stringWidth / 2)), (int) (frameHeight / 2));
			}
		}
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
}