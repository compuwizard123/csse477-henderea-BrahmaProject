package plugin;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class DependencyResolver {
	private PluginLoader pluginLoader;
	private Map<String, Boolean> pluginStatusMap;
	private Map<String, List<String>> dependencyMap;
	private Map<String, Path> pathToPluginMap;

	public DependencyResolver(IPluginSubscriber pluginSubscriber) {
		this.pluginLoader = new PluginLoader(pluginSubscriber);
		this.pluginStatusMap = new HashMap<String, Boolean>();
		this.dependencyMap = new HashMap<String, List<String>>();
		this.pathToPluginMap = new HashMap<String, Path>();
		try {
			Path pluginDir = FileSystems.getDefault().getPath("plugins");
			File pluginFolder = pluginDir.toFile();
			File[] files = pluginFolder.listFiles();
			if(files != null) {
				for(File f : files) {
					this.registerPlugin(f.toPath());
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void registerPlugin(Path bundlePath) {
		try {
			File jarBundle = bundlePath.toFile();
			JarFile jarFile = new JarFile(jarBundle);
			Manifest mf = jarFile.getManifest();
			Attributes mainAttribs = mf.getMainAttributes();
			String dependencies = mainAttribs.getValue("Dependencies");
			String pluginClass = mainAttribs.getValue("Plugin-Class");
			System.out.println("Plugin class: " + pluginClass);
			if (dependencies != null) {
				String[] dependencyList = dependencies.split("\\s+");
				this.dependencyMap.put(pluginClass, Arrays.asList(dependencyList));
			} else {
				this.dependencyMap.put(pluginClass, null);
			}
			this.pathToPluginMap.put(pluginClass, bundlePath);
			jarFile.close();
			loadBundleAndDependants(pluginClass);
			
		}
		catch(Exception e) {
			System.out.println("Unable to resolve dependencies.");
			e.printStackTrace();
		}
	}
	
	public void loadBundleAndDependants(String pluginClass) {
		if (dependencyMap.get(pluginClass) != null) {
			for (String dependency : dependencyMap.get(pluginClass)) {
				if (!pluginStatusMap.containsKey(dependency) || !pluginStatusMap.get(dependency)) {
					pluginStatusMap.put(pluginClass, false);
					return;
				}
			}
		}
		loadBundle(pathToPluginMap.get(pluginClass));
		pluginStatusMap.put(pluginClass, true);
		
		for (String dependantClass : pluginStatusMap.keySet()) {
			if (!pluginStatusMap.get(dependantClass) && dependencyMap.get(dependantClass).contains(pluginClass)) {
				loadBundleAndDependants(dependantClass);
			}
		}
	}
	
	
	private void loadBundle(Path bundlePath) {
		try {
			this.pluginLoader.loadBundle(bundlePath);
		} catch (Exception e) {
			System.out.printf("There was a problem loading the plugin at %s\n", bundlePath.toString());
			e.printStackTrace();
		}
	}
	
	public void unloadBundle(Path bundlePath) {
		//add dependency resolution
		this.pluginLoader.unloadBundle(bundlePath);
	}
}
