package plugin;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

public class FilesystemWatcher {
	private PluginLoader pluginLoader;
	private WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private boolean trace = false;
	
	public FilesystemWatcher(IPluginSubscriber pluginSubscriber) {
		this.pluginLoader = new PluginLoader(pluginSubscriber);
		this.keys = new HashMap<WatchKey,Path>();
        try {
			this.watcher = FileSystems.getDefault().newWatchService();
			register(FileSystems.getDefault().getPath("plugins"));
		} catch (IOException e) {
			System.out.println("Could not initialize the file watcher properly.");
			e.printStackTrace();
		}
        this.trace = true;
	}
	
	private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }
	
    void processEvents() {
        while (true) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);
                
                // C.R. Changes
            	if(this.pluginLoader != null) {
            		try {
                        if(kind == ENTRY_CREATE) {
                        	this.pluginLoader.loadBundle(child);
                        }
                        else if(kind == ENTRY_DELETE) {
                        	this.pluginLoader.unloadBundle(child);
                        }
            		}
            		catch(Exception e) {
            			e.printStackTrace();
            		}
            	}
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
}
