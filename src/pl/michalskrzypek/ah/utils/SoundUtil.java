package pl.michalskrzypek.ah.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundUtil {

	public static void playSound(String soundSource) {
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(soundSource));
			clip.open(inputStream);
			clip.loop(0);
			clip.start();
		} catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}

	public static void playBackgroundMusic(Clip clip, String musicSource) {
		try {
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(musicSource));
			clip.open(inputStream);
			clip.loop(1);
			clip.start();
		} catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}

	public static void stopBackgroundMusic(Clip clip) {
		clip.stop();
	}
}
