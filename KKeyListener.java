package tasks;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KKeyListener extends KeyAdapter {
	
	public boolean spacePressed = false, _123Pressed = false;
	 
    @Override
    public void keyPressed(KeyEvent event) {
    	char ch = event.getKeyChar();
    	if (ch == '1' || ch == '2'|| ch == '3') {
    		System.out.println(event.getKeyChar());
    		_123Pressed = true;
    	}
    	if(ch == ' ') {
    		System.out.println("space pressed");
    		spacePressed = true;
    	}
    }
}