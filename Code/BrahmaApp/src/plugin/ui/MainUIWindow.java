package plugin.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import plugin.IPluginHost;
import plugin.Plugin;
import plugin.PluginManager;

public class MainUIWindow implements IPluginHost {
	// GUI Widgets that we will need
		private JFrame frame;
		private JPanel contentPane;
		private JLabel bottomLabel;
		private JPanel centerEnvelope;
		
	public static void main(String[] args) {
		new MainUIWindow();
	}
	
	public MainUIWindow(){
		// Lets create the elements that we will need
		frame = new JFrame("Pluggable Board Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPane = (JPanel)frame.getContentPane();
		contentPane.setPreferredSize(new Dimension(700, 500));
		bottomLabel = new JLabel("No plugins registered yet!");
		
		// Create center display area
		centerEnvelope = new JPanel(new BorderLayout());
		centerEnvelope.setBorder(BorderFactory.createLineBorder(Color.black, 5));
		
		// Lets lay them out, contentPane by default has BorderLayout as its layout manager
		contentPane.add(centerEnvelope, BorderLayout.CENTER);
		
		contentPane.add(bottomLabel, BorderLayout.SOUTH);
		
		new PluginListView(contentPane, this);
		this.start();
	}
	

	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	public void stop() {
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				frame.setVisible(false);
			}
		});
	}
	
	@Override
	public void setPlugin(Plugin currentPlugin) {
		// Clear previous working area
		centerEnvelope.removeAll();
		
		// Create new working area
		JPanel centerPanel = new JPanel();
		centerEnvelope.add(centerPanel, BorderLayout.CENTER); 
		
		// Ask plugin to layout the working area
		currentPlugin.layout(centerPanel);
		contentPane.revalidate();
		contentPane.repaint();
		
		// Start the plugin
		currentPlugin.start();
		
		bottomLabel.setText("The " + currentPlugin.getId() + " is running!");
	}
	
	public JLabel getBottomLabel() {
		return bottomLabel;
	}
	
	public void setStatusText(String text) {
		bottomLabel.setText(text);
	}
}
