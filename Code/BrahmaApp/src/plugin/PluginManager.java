package plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


public class PluginManager implements Runnable {
	private IPluginSubscriber core;
	private IPluginHost pluginHost;
	private WatchDir watchDir;
	private HashMap<Path, Plugin> pathToPlugin;

	// For holding registered plugin
	private HashMap<String, Plugin> idToPlugin;
	private Plugin currentPlugin;

	public PluginManager(IPluginSubscriber core, IPluginHost pluginHost) {
		this.core = core;
		this.pluginHost = pluginHost;
		idToPlugin = new HashMap<String, Plugin>();
		this.pathToPlugin = new HashMap<Path, Plugin>();
		try {
			watchDir = new WatchDir(this, FileSystems.getDefault().getPath("plugins"), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// First load existing plugins if any
		try {
			Path pluginDir = FileSystems.getDefault().getPath("plugins");
			File pluginFolder = pluginDir.toFile();
			File[] files = pluginFolder.listFiles();
			if(files != null) {
				for(File f : files) {
					this.loadBundle(f.toPath());
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		// Listen for newly added plugins
		watchDir.processEvents();
	}

	void loadBundle(Path bundlePath) throws Exception {
		// Get hold of the jar file
		File jarBundle = bundlePath.toFile();
		JarFile jarFile = new JarFile(jarBundle);
		
		// Get the manifest file in the jar file
		Manifest mf = jarFile.getManifest();
        Attributes mainAttribs = mf.getMainAttributes();
        
        // Get hold of the Plugin-Class attribute and load the class
        String className = mainAttribs.getValue("Plugin-Class");
        URL[] urls = new URL[]{bundlePath.toUri().toURL()};
        ClassLoader classLoader = new URLClassLoader(urls);
        Class<?> pluginClass = classLoader.loadClass(className);
        
        // Create a new instance of the plugin class and add to the core
        Plugin plugin = (Plugin)pluginClass.newInstance();
        this.addPlugin(plugin);
        this.core.addPlugin(plugin);
        this.pathToPlugin.put(bundlePath, plugin);

        // Release the jar resources
        jarFile.close();
	}
	
	void unloadBundle(Path bundlePath) {
		Plugin plugin = this.pathToPlugin.remove(bundlePath);
		if(plugin != null) {
			this.removePlugin(plugin.getId());
			this.core.removePlugin(plugin.getId());
		}
	}

	public void setPlugin(String id) {
		Plugin plugin = idToPlugin.get(id);
		
		if(plugin == null || plugin.equals(currentPlugin))
			return;
		
		// Stop previously running plugin
		if(currentPlugin != null)
			currentPlugin.stop();
		
		// The newly selected plugin is our current plugin
		currentPlugin = plugin;
		
		pluginHost.setPlugin(currentPlugin);
	}
	
	public void addPlugin(Plugin plugin) {
		this.idToPlugin.put(plugin.getId(), plugin);
	}
	
	public void removePlugin(String id) {
		Plugin plugin = this.idToPlugin.remove(id);
		plugin.stop();
	}
}
