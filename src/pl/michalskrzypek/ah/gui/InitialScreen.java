package pl.michalskrzypek.ah.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import pl.michalskrzypek.ah.utils.SoundUtil;

/*
 * Class responsible for displaying menu screen
 */
@SuppressWarnings("serial")
public class InitialScreen extends JFrame {

	public static final String GAME_INFORMATION = "GAME CONTROLS:\nw  -  move forward\ns  -  move backward\n"
			+ "d  -  rotate ship clockwise\na  -  rotate ship counter-clockwise\nENTER  -  shoot a bullet\n"
			+ "p - pause the game\n\nINSTRUCTION:\nTry to destroy every asteroid!\n"
			+ "Press ENTER to strike an asteroid.\nCollect green power-ups to freeze asteroids for 5 seconds!\n"
			+ "You can collide with an asteroid up to 5 times, \nso be careful!\n\n"
			+ "Asteroid Hunter® by Michal Skrzypek\nmskrzypek97@gmail.com";
	public static final String[] GAME_LEVELS = { "Easy", "Medium", "Hard" };
	private static Clip clip;
	private static String level = "Easy";
	private JButton startGameButton, exitGameButton, instructionButton, levelButton;
	private Image background;
	
	public static void main(String[] args) {
		InitialScreen is = new InitialScreen();
		is.setVisible(true);
	}

	public InitialScreen() {
		try {
			clip = AudioSystem.getClip();
			SoundUtil.playBackgroundMusic(clip, "./sounds/intro.wav");

			background = ImageIO.read(new File("./images/menu.jpg"));
		} catch (IOException | LineUnavailableException e1) {
			e1.printStackTrace();
		}
		setSize(315, 428);
		setResizable(false);
		setLocation(300, 100);
		setLayout(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setContentPane(new Background());
		setLocationRelativeTo(null);

		add(getStartGameButton());
		add(getInstructionButton());
		add(getLevelButton());
		add(getExitGameButton());
	}

	private JButton getStartGameButton() {
		if (startGameButton == null) {
			startGameButton = new JButton("Start game");
			startGameButton.setBounds(95, 120, 125, 30);
			startGameButton.addActionListener(e -> {
				SoundUtil.playSound("./sounds/click.wav");

				String name = JOptionPane.showInputDialog(InitialScreen.this, "Please enter your name", null,
						JOptionPane.QUESTION_MESSAGE);
				if (name != null) {
					name = name.trim();
					if (name.length() >= 3) {
						InitialScreen.this.dispose();
						SoundUtil.stopBackgroundMusic(clip);
						new AsteroidGameBoard(name);
					} else {
						JOptionPane.showMessageDialog(InitialScreen.this, "Name is too short! (min 3 letters)", null,
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}
		return startGameButton;
	}

	private JButton getInstructionButton() {
		if (instructionButton == null) {
			instructionButton = new JButton("Instruction");
			instructionButton.setBounds(95, 180, 125, 30);
			instructionButton.addActionListener(e -> {
				SoundUtil.playSound("./sounds/click.wav");
				JOptionPane.showMessageDialog(InitialScreen.this, new String(GAME_INFORMATION), "Instruction",
						JOptionPane.INFORMATION_MESSAGE);
			});
		}
		return instructionButton;
	}

	private JButton getLevelButton() {
		if (levelButton == null) {
			levelButton = new JButton("Level: Easy");
			levelButton.addActionListener(e -> {
				SoundUtil.playSound("./sounds/click.wav");

				int option = JOptionPane.showOptionDialog(InitialScreen.this, new String("Select level:"), "",
						JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, GAME_LEVELS, GAME_LEVELS[0]);
				setLevel(option);

				levelButton.setText("Level: " + level);
			});
			levelButton.setBounds(95, 240, 125, 30);
		}
		return levelButton;
	}

	private JButton getExitGameButton() {
		if (exitGameButton == null) {
			exitGameButton = new JButton("Exit game");
			exitGameButton.setBounds(95, 300, 125, 30);
			exitGameButton.addActionListener(e -> {
				SoundUtil.playSound("./sounds/click.wav");
				System.exit(0);
			});
		}
		return exitGameButton;
	}

	public static String getLevel() {
		return level;
	}

	public static void setLevel(int option) {
		switch (option) {
		case 0:
			level = "Easy";
			break;
		case 1:
			level = "Medium";
			break;
		case 2:
			level = "Hard";
			break;
		}
	}

	class Background extends JComponent {

		public Background() {
			setVisible(true);
			setSize(315, 435);
		}

		public void paintComponent(Graphics g) {
			g.drawImage(background, 0, 0, 315, 428, null);
		}
	}

}
