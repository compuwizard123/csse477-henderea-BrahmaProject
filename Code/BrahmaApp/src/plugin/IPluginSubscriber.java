package plugin;

public interface IPluginSubscriber {

	public void addPlugin(Plugin plugin);
	
	public void removePlugin(String id);
}
