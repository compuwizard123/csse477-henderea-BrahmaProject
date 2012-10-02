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

import plugin.IPluginHost;
import plugin.IPluginSubscriber;
import plugin.Plugin;
import plugin.RuntimeManager;

public class PluginListView implements IPluginSubscriber {
	private JList sideList;
	private DefaultListModel<Plugin> listModel;
	private IPluginHost pluginHost;
	
	public PluginListView(JPanel contentPane, IPluginHost pluginHost){
		listModel = new DefaultListModel<Plugin>();
		sideList = new JList(listModel);
		this.pluginHost = pluginHost;
		sideList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sideList.setLayoutOrientation(JList.VERTICAL);
		final RuntimeManager runtimeManager = new RuntimeManager(pluginHost);
		
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
				Plugin plugin = listModel.elementAt(index);
				
				runtimeManager.setPlugin(plugin);
			}
		});
	}
	
	public void addPlugin(Plugin plugin) {
		this.listModel.addElement(plugin);
		this.pluginHost.setStatusText("The " + plugin.getId() + " plugin has been recently added!");
	}
	
	public void removePlugin(Plugin id) {
		this.listModel.removeElement(id);
		this.pluginHost.setStatusText("The " + id + " plugin has been recently removed!");
	}
}
