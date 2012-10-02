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

import plugin.FilesystemWatcher;
import plugin.IPluginHost;
import plugin.IPluginSubscriber;
import plugin.Plugin;

public class MainUIWindow implements IPluginHost {
		// GUI Widgets that we will need
		private JPanel contentPane;
		private JLabel bottomLabel;
		private JPanel centerEnvelope;
		
	public static void main(String[] args) {
		new MainUIWindow();
	}
	
	public MainUIWindow(){
		contentPane = createMainWindow();
		setupContentPane();
		IPluginSubscriber pluginSubscriber = new PluginListView(contentPane, this);
		new FilesystemWatcher(pluginSubscriber);
	}
	
	private JPanel createMainWindow() {
		final JFrame frame = new JFrame("Pluggable Board Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				frame.pack();
				frame.setVisible(true);
			}
		});
		return((JPanel) frame.getContentPane());
	}
	
	private void setupContentPane() {
		contentPane.setPreferredSize(new Dimension(700, 500));
		setupCenter();
		setupLabel();
		contentPane.add(centerEnvelope, BorderLayout.CENTER);
		contentPane.add(bottomLabel, BorderLayout.SOUTH);
	}
	
	private void setupCenter() {
		centerEnvelope = new JPanel(new BorderLayout());
		centerEnvelope.setBorder(BorderFactory.createLineBorder(Color.black, 5));
	}
	
	private void setupLabel() {
		bottomLabel = new JLabel("No plugins registered yet!");
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
	
	public void setStatusText(String text) {
		bottomLabel.setText(text);
	}
}
