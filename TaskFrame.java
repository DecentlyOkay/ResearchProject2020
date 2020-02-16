package tasks;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class TaskFrame {

	final int WIDTH = 1000;
	final int HEIGHT = 700;
	
	JFrame frame;
	JPanel taskPanel;
	
	public TaskFrame(int width, int height, String title) {
		frame = new JFrame();
		frame.setSize(width, height);
		frame.setTitle(title);
		//creates frame, other things will be done with method initialize frame which will be called after all components added
		
		taskPanel = new JPanel(new GridBagLayout());
		taskPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		frame.setContentPane(taskPanel);
		frame.setResizable(false);
		
	}
	
	
	void addComponent(JComponent comp, int x, int y, int w, int h, int place, int stretch)
	{
		GridBagConstraints gridC = new GridBagConstraints();
		gridC.gridx = x;
		gridC.gridy = y;
		gridC.gridwidth = w;
		gridC.gridheight = h;
		gridC.insets = new Insets(5, 5, 5, 5);
		gridC.anchor = place;
		gridC.fill = stretch;
		frame.getContentPane().add(comp, gridC);
	}
	
	void addComponent2(JComponent comp, int x, int y, int w, int h) {
		frame.getContentPane().add(comp);
	}
	
	void initializeFrame() {
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
