package tasks;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JLabel;

/*
 * Task 2: Filler Task

Overview: Program will play three different tones, which participants will classify as low, medium, or high by pressing a 1, 2, or 3 key. Responses and accuracy do not need to be recorded. I have the files for each tone and can export them however needed. The files each play for 500ms.

Display instructions: “For this encoding task, you will be classifying three sound tones as low, medium, and high pitched. For each trial, you will hear one of three tones, selected at random, and classify it as low, medium, or high by pressing the 1, 2, or 3 key on the keyboard (1=low, 2=medium, 3=high). Please wait for the tone to completely finish playing before pressing a key. You will now hear a sample of each tone.”
Play a sample of each tone.
Display instructions: “Press the spacebar to begin the encoding task.”
For each trial (30 trials total), a random tone will play for 500ms, and participants must press the 1, 2, or 3 key before proceeding to the next trial/hearing the next tone. 
Display instructions: “You have finished Task 2! Task 3 will be completed one-on-one with the researcher. Please take out your earbuds and wait silently for your turn.”

It doesn’t matter if the responses are right or wrong, and answers do not need to be stored. The program just needs to proceed after participants press a 1, 2, or 3 key.
 */

public class FillerTask extends TaskFrame {

	private static final int NUM_TRIALS = 30;
	
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 700;
	
	private JLabel textPrompt;
	
	private static final File[] SOUND_FILES = {new File("350Hz.wav"), new File("500Hz.wav"), new File("650Hz.wav")};
	
	private KKeyListener keyboard;

	
	//Frame Properties
	public FillerTask() {
		super(WIDTH, HEIGHT, "Task 2");

		textPrompt = new JLabel();
		textPrompt.setText("<html><body style=\"text-align:center\"><font size=\"5\"><p>For this task, you will be classifying three sound tones as low, medium, and high pitched. <br>For each trial, you will hear one of three tones, selected at random, and classify it as <br>low, medium, or high by pressing the 1, 2, or 3 key on the keyboard (1=low, 2=medium, 3=high). <br>Please wait for the tone to completely finish playing before pressing a key.<br>Press space to hear a sample of each tone.</font></body></html>");
		textPrompt.setOpaque(false);
	    textPrompt.setFocusable(false);
	    frame.getContentPane().add(textPrompt);
		keyboard = new KKeyListener();
		frame.addKeyListener(keyboard);
		initializeFrame();
	}
	
	public static void main(String[] args) throws InterruptedException, IOException, UnsupportedAudioFileException, LineUnavailableException {
		FillerTask task = new FillerTask();
		task.runTask();
	}
	
	public void runTask() throws InterruptedException, IOException, UnsupportedAudioFileException, LineUnavailableException {
		//Wait for space to be pressed
		while(true) {
			if(keyboard.spacePressed) {
				keyboard.spacePressed = false;
				break;
			}
			Thread.sleep(10);
		}
		
		//Play the three tones
		for(int i = 0; i < 3; i++) {
			playClip(SOUND_FILES[i]);
		}
		
		textPrompt.setText("<html><font size=\"5\">Press the spacebar to begin the task.</font></html>");
		while(true) {
			if(keyboard.spacePressed) {
				keyboard.spacePressed = false;
				break;
			}
			Thread.sleep(10);
		}
		textPrompt.setText("");
		
		//Play NUM_TRIALS random tones
		int count = 0;
		while(count++<NUM_TRIALS) {
			int randTone = RNG.randomInt(0, 2);
			playClip(SOUND_FILES[randTone]);
			keyboard._123Pressed = false;
			while(true) {
				if(keyboard._123Pressed) {
					keyboard._123Pressed = false;
					break;
				}
				Thread.sleep(10);
			}
		}
		
		//End
		textPrompt.setText("<html><body style=\"text-align:center\"><font size=\"5\">You have finished Task 2! Task 3 will be completed one-on-one with the researcher.<br> Please take out your earbuds and wait silently for your turn.</font></body</html>");
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

