package plugin;

import java.util.HashMap;

public class RuntimeManager {
	private IPluginHost pluginHost;
	private Plugin currentPlugin;

	
	public RuntimeManager(IPluginHost pluginHost) {
		this.pluginHost = pluginHost;
	}
	
	public void setPlugin(Plugin plugin) {
		
		if(plugin == null || plugin.equals(currentPlugin))
			return;
		
		// Stop previously running plugin
		if(currentPlugin != null)
			currentPlugin.stop();
		
		// The newly selected plugin is our current plugin
		currentPlugin = plugin;
		
		pluginHost.setPlugin(currentPlugin);
	}
}
