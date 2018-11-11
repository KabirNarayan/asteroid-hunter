package pl.michalskrzypek.ah.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import pl.michalskrzypek.ah.gui.InitialScreen;

public class HighScoreUtil {

	private static File highScores;
	private static FileWriter fw;
	private static BufferedWriter bw;
	private static FileReader fr;
	private static BufferedReader br;
	private static ArrayList<Double> scoreTimes;
	private static ArrayList<String> scoresTotal;

	public static ArrayList<String> getScoresTotal() {
		return scoresTotal;
	}

	public static void initiateHighScoresFiles() {
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
	}

	public static void getHighScores() {
		try {
			fr = new FileReader(highScores);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		scoreTimes = new ArrayList<Double>();
		scoresTotal = new ArrayList<String>();
		HashMap<Double, String> map = new HashMap<Double, String>();
		String line = null;
		String[] playerScore = new String[2]; // 0 for name, 1 for score

		try {
			while ((line = br.readLine()) != null) {
				playerScore = line.split("Time:");
				String pName = playerScore[0];
				double pScore = Double.parseDouble(playerScore[1]);
				scoreTimes.add(pScore);
				map.put(pScore, pName);
			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

		Collections.sort(scoreTimes);
		for (double score : scoreTimes) {
			int index = scoreTimes.indexOf(score);
			scoresTotal.add(index + 1 + ". " + map.get(score) + " " + String.format("%.2fs", score / 1000));
		}
	}// END of getHighScores method

	public static void appendScore(String playerName, float timePassed) {
		try {
			fw = new FileWriter(highScores, true);
			bw = new BufferedWriter(fw);
			bw.write(playerName + "Time:" + timePassed);
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
