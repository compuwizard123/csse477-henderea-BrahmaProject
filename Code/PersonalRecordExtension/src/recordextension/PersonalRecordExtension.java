package recordextension;

import java.awt.BorderLayout;

import javax.swing.JPanel;


import plugin.Plugin;

public class PersonalRecordExtension extends Plugin {
	public static final String PLUGIN_ID = "Personal Record";

	JPanel panel;
	
	public PersonalRecordExtension() {
		super(PLUGIN_ID);
	}

	@Override
	public void layout(JPanel parentPanel) {
		parentPanel.setLayout(new BorderLayout());
		panel = new PersonalRecordPanel();
		parentPanel.add(panel);
	}

	@Override
	public void start() {
		// Nothing to initialize
	}

	@Override
	public void stop() {
		// Nothing to finalize
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
