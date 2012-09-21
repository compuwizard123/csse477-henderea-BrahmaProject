package bouncingball;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import plugin.Plugin;

/**
 * An extension plugin.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 *
 */
public class BouncingBallPlugin extends Plugin {
	public static final String PLUGIN_ID = "Bouncing Ball";
	
	private BBPanel panel;
	
	public BouncingBallPlugin() {
		super(PLUGIN_ID);
	}

	@Override
	public void layout(JPanel parentPanel) {
		parentPanel.setLayout(new BorderLayout());
		panel = new BBPanel();
		parentPanel.add(panel);
	}

	@Override
	public void start() {
		// Not much to do here
	}

	@Override
	public void stop() {
		// Not much to do here
	}
	
	// For now we need to declare dummy main method
	// to include in manifest file
	public static void main(String[] args) {
		
	}
}
