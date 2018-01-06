package pl.michalskrzypek.ah.main;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Utilities {

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

	public static void playBackgroundMusic(Clip clip, String musicSource) {
		try {
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(musicSource));
			clip.open(inputStream);
			clip.loop(1);
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
	
	public static void stopBackgroundMusic(Clip clip) {
			clip.stop();
	}
}
