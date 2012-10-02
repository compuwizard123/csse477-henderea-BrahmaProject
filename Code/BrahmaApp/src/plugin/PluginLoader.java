package plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class PluginLoader {
	private IPluginSubscriber pluginSubscriber;
	private HashMap<Path, Plugin> pathToPlugin;
	
	public PluginLoader(IPluginSubscriber pluginSubscriber) {
		this.pluginSubscriber = pluginSubscriber;
		this.pathToPlugin = new HashMap<Path, Plugin>();
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
        this.pluginSubscriber.addPlugin(plugin);
        this.pathToPlugin.put(bundlePath, plugin);

        // Release the jar resources
        jarFile.close();
	}
	
	void unloadBundle(Path bundlePath) {
		Plugin plugin = this.pathToPlugin.remove(bundlePath);
		if(plugin != null) {
			this.pluginSubscriber.removePlugin(plugin);
		}
	}
}
