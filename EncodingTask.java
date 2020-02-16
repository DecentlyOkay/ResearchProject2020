package tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JLabel;

/*
 * Task 1: Encoding Task
Overview: Program will prompt students to ‘write’ or ‘draw’ 14 words randomly selected from the list of 80 words (see below). Each of the 14 words must be randomly assigned to a prompt and presented in a random order. A tone will play at certain checkpoints (I have the file for the tone).

Display instructions: “For this encoding task, you will be drawing and writing words that are presented on the screen. A prompt of ‘draw’ or ‘write’ will first appear: the ‘draw’ prompt indicates that you are to create a drawing of the word, and the ‘write’ prompt indicates that you are to clearly and carefully write out the word multiple times. After the prompt, a word will appear briefly on screen. Immediately after seeing the word, begin to draw or write. Continue to draw or write until you hear a tone. After hearing the tone, flip your pad to the next page in preparation for the next prompt. Continue this process until the encoding task is complete.”

Display instructions: “You will now undergo a brief practice phase that will exactly mimic the encoding task. Press the spacebar to begin the practice phase.”

Display the prompt ‘draw’ on the screen for 750 ms, followed by a 500-ms fixation (blank screen), after which display the word ‘sword’ for 750 ms. After 40 seconds of a blank screen, play a 500Hz tone.

Display instructions: “Practice phase now complete. Press the spacebar to begin the encoding task.”

For each trial (14 trials total), a word from the list of 80 must be randomly selected to be studied. Of these 14 trials, 7 words must be randomly assigned to the ‘draw’ task, and 7 must be randomly assigned to the ‘write’ task. Present the words in a randomized order, so that ‘write’ and ‘draw’ prompts are randomly intermixed. Record what words are assigned to which prompt and in what order they are presented.

For each trial, the prompt must in the centre of the screen for 750 ms, followed by a 500-ms fixation (blank screen), after which the word to be encoded appeared for 750 ms. Participants then have 40 s to perform the encoding task, either draw or write (blank screen). Play a 500-ms tone them to alert them the next item is forthcoming. 3s after the tone, begin a new trial.

Display instructions: “You have finished Task 1. Please remove your earbuds and wait silently for everyone else to finish.”

	This program must be able to reset/be reused multiple times on multiple students.

 */

public class EncodingTask extends TaskFrame {

	private static final int NUM_TRIALS = 14;
	
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 700;
	
	private static final File SOUND_500HZ = new File("500Hz.wav");
	
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss MM/dd/yyyy");
	
	private static final int PROMPT_TIME = 40000, WAIT_TIME = 3000;
	
	private BufferedWriter output;
	
	private JLabel textPrompt;
	
	private ArrayList<String> wordList;
	private String[] chosen;
	
	//1 for draw, 2 for write
	private int[] tasks;
	
	private KKeyListener keyboard;
	
	public EncodingTask() throws IOException {
		super(WIDTH, HEIGHT, "Task 1");
		
		wordList = new ArrayList<>();
		chosen = new String[NUM_TRIALS];
		tasks = new int[NUM_TRIALS];
		
		//Generate NUM_TRIALS random unique words from the list
		Scanner file;
		try {
			file = new Scanner(new File("words"));
			while(file.hasNext()) {
				wordList.add(file.next());
			}
			file.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int index = 0;
		for(int i : RNG.randomList(0, wordList.size(), NUM_TRIALS)) {
			chosen[index++] = wordList.get(i);
		}
		
		
		//Randomly assign tasks, NUM_TRIALS/2 of draw and NUM_TRIALS/2 of write
		//1 = draw	2 = write
		Arrays.fill(tasks, 1);
		for(int i : RNG.randomList(0, tasks.length, NUM_TRIALS/2)) {
			tasks[i] = 2;
		}
		keyboard = new KKeyListener();
		
		output = new BufferedWriter(new FileWriter("Encoding_Task_Results.txt", true));
		
		textPrompt = new JLabel();
		textPrompt.setText("<html><body style=\"text-align:center\"><font size=\"5\"><p>For this task, you will be drawing and writing words that are presented on the screen.<br>A prompt of ‘draw’ or ‘write’ will first appear: the ‘draw’ prompt indicates that you are to create a <br>drawing of the word, and the ‘write’ prompt indicates that you are to clearly and carefully <br>write out the word multiple times. After the prompt, a word will appear briefly on screen.<br>Immediately after seeing the word, begin to draw or write. Continue to draw or write until you hear a tone.<br>After hearing the tone, flip your pad to the next page in preparation for the next prompt. <br>Continue this process until the task is complete.<br>You will now undergo a brief practice phase that will exactly mimic the task. <br>Press the spacebar to begin the practice phase.</p></font></body></html>");
		textPrompt.setOpaque(false);
		textPrompt.setFocusable(false);
		frame.getContentPane().add(textPrompt);
		
		keyboard = new KKeyListener();
		frame.addKeyListener(keyboard);
		
		initializeFrame();
	}
	
	public static void main(String[] args) throws InterruptedException, IOException, UnsupportedAudioFileException, LineUnavailableException {
		EncodingTask task = new EncodingTask();
		task.runTask();
	}
	
	private void runTask() throws InterruptedException, IOException, UnsupportedAudioFileException, LineUnavailableException {	
		//Record data
		output.newLine();
		output.write("NEW TRIAL: " + dtf.format(LocalDateTime.now()));
		for(int i = 0; i < NUM_TRIALS; i++) {
			output.newLine();
			output.write((tasks[i] == 1 ? "draw" : "write") + " " + chosen[i]);
		}
		output.flush();
		
		//Wait for space to be pressed
		while(true) {
			if(keyboard.spacePressed) {
				keyboard.spacePressed = false;
				break;
			}
			Thread.sleep(10);
		}
		
		//Practice Phase
		textPrompt.setText("<html><font size=\"5\">draw</font></html>");
		Thread.sleep(750);
		textPrompt.setText("");
		Thread.sleep(500);
		textPrompt.setText("<html><font size=\"5\">sword</font></html>");
		Thread.sleep(750);
		textPrompt.setText("");
		Thread.sleep(PROMPT_TIME);
		playClip(SOUND_500HZ);
		textPrompt.setText("<html><font size=\"5\">Practice phase now complete. Press the spacebar to begin the task.</font></html>");
		
		//Wait for space to be pressed
		keyboard.spacePressed = false;
		while(true) {
			if(keyboard.spacePressed) {
				keyboard.spacePressed = false;
				break;
			}
			Thread.sleep(10);
		}
		
		//Show the prompts
		for(int i = 0; i < NUM_TRIALS; i++) {
			textPrompt.setText("<html><font size=\"5\">" + (tasks[i] == 1 ? "draw" : "write") + "</font></html>");
			Thread.sleep(750);
			textPrompt.setText("");
			Thread.sleep(500);
			textPrompt.setText("<html><font size=\"5\">" + chosen[i] + "</font></html>");
			Thread.sleep(750);
			textPrompt.setText("");
			Thread.sleep(PROMPT_TIME);
			playClip(SOUND_500HZ);
			Thread.sleep(WAIT_TIME);
		}
		
		//End
		textPrompt.setText("<html><body style=\"text-align:center\"><font size=\"5\">You have finished Task 1.<br>Please remove your earbuds and wait silently for everyone else to finish.</font><body></html>");
		output.close();
	}
	
	private static void playClip(File clipFile) throws IOException, 
	  UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
	  class AudioListener implements LineListener {
	    private boolean done = false;
	    @Override public synchronized void update(LineEvent event) {
	      LineEvent.Type eventType = event.getType();
	      if (eventType == LineEvent.Type.STOP || eventType == LineEvent.Type.CLOSE) {
	        done = true;
	        notifyAll();
	      }
	    }
	    public synchronized void waitUntilDone() throws InterruptedException {
	      while (!done) { wait(); }
	    }
	  }
	  AudioListener listener = new AudioListener();
	  AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(clipFile);
	  try {
	    Clip clip = AudioSystem.getClip();
	    clip.addLineListener(listener);
	    clip.open(audioInputStream);
	    try {
	      clip.start();
	      listener.waitUntilDone();
	    } finally {
	      clip.close();
	    }
	  } finally {
	    audioInputStream.close();
	  }
	}

}
