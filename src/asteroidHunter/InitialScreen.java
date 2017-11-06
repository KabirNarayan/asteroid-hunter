package asteroidHunter;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class InitialScreen extends JFrame {

	JButton startGameButton, exitGameButton, instructionButton;
	Image background;
	JDialog instructionDialog;
	private String informationString;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
new InitialScreen();
	}

	public InitialScreen() {
try {
	background = ImageIO.read(new File("./images/menu.jpg"));
} catch (IOException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
}
		setSize(315, 428);
		setResizable(false);
		setLocation(300, 100);
		setLayout(null);
		setContentPane(new Background());
		startGameButton = new JButton("Start game");
		exitGameButton = new JButton("Exit game");
		startGameButton.setBounds(95, 150, 125, 30);
		startGameButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			
				new AsteroidGameBoard();
			}
		});
		
		informationString = "GAME CONTROLS:\nw  -  move forward\ns  -  move backward\nd  -  rotate ship clockwise\na  -  rotate ship counter-clockwise\nENTER  -  shoot a bullet\n\nINSTRUCTION:\nTry to destroy every asteroid!\nPress ENTER to shoot an asteroid and \ntry not to hit any of them. You can only \nhit an asteroid 5 times in your road to\nwin, so be careful!\n\nAsteroid Hunter® by Michal Skrzypek\nmskrzypek97@gmail.com";
		instructionButton = new JButton("Instruction");
		instructionButton.setBounds(95, 210, 125, 30);
		instructionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JOptionPane.showMessageDialog(InitialScreen.this,new String(informationString), "Information", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		this.add(instructionButton);
		
		exitGameButton.setBounds(95, 270, 125, 30);
		exitGameButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		
		this.add(startGameButton);
		this.add(exitGameButton);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	
	
	class Background extends JComponent{
		
		public Background(){
			setVisible(true);
			setSize(315, 435);
		}
		
		public void paintComponent(Graphics g){
			g.drawImage(background,  0, 0, 315, 428, null);
		}
		
	}
	

}
