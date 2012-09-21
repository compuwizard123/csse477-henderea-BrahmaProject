package plugin.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import plugin.Plugin;
import plugin.PluginManager;

public class PluginListView {
	private JList sideList;
	private DefaultListModel<String> listModel;
	private JLabel bottomLabel;
	
	// Plugin manager
	PluginManager pluginManager;
	
	// For holding registered plugin
	private HashMap<String, Plugin> idToPlugin;
	private Plugin currentPlugin;
	
	public PluginListView(final JPanel contentPane, final MainUIWindow mainWindow){
		idToPlugin = new HashMap<String, Plugin>();
		listModel = new DefaultListModel<String>();
		sideList = new JList(listModel);
		sideList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sideList.setLayoutOrientation(JList.VERTICAL);
		bottomLabel = mainWindow.getBottomLabel();
		
		JScrollPane scrollPane = new JScrollPane(sideList);
		scrollPane.setPreferredSize(new Dimension(100, 50));
		
		contentPane.add(scrollPane, BorderLayout.EAST);
		
		// Add action listeners
		sideList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// If the list is still updating, return
				if(e.getValueIsAdjusting())
					return;
				
				// List has finalized selection, let's process further
				int index = sideList.getSelectedIndex();
				String id = listModel.elementAt(index);
				Plugin plugin = idToPlugin.get(id);
				
				if(plugin == null || plugin.equals(currentPlugin))
					return;
				
				// Stop previously running plugin
				if(currentPlugin != null)
					currentPlugin.stop();
				
				// The newly selected plugin is our current plugin
				currentPlugin = plugin;
				
				mainWindow.setPlugin(currentPlugin);
			}
		});
		// Start the plugin manager now that the core is ready
		try {
			this.pluginManager = new PluginManager(this);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		Thread thread = new Thread(this.pluginManager);
		thread.start();
	}
	
	public void addPlugin(Plugin plugin) {
		this.idToPlugin.put(plugin.getId(), plugin);
		this.listModel.addElement(plugin.getId());
		this.bottomLabel.setText("The " + plugin.getId() + " plugin has been recently added!");
	}
	
	public void removePlugin(String id) {
		Plugin plugin = this.idToPlugin.remove(id);
		this.listModel.removeElement(id);
		
		// Stop the plugin if it is still running
		plugin.stop();

		this.bottomLabel.setText("The " + plugin.getId() + " plugin has been recently removed!");
	}
}
