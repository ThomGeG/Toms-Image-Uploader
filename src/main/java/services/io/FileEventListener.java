package main.java.services.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.util.Map;
import java.util.HashMap;

/**
 * A small class for listening for file events on the local file system (Creation, deletion and modification).
 * Utilises a strategy pattern to separate action logic from listener logic.
 * 
 * @see main.java.services.io.FileEventHandler
 * @param <T> Data-type of directory specific data you want associated with the directory.
 * 
 * @author Tom
 */
public class FileEventListener<T> implements Runnable {

	private WatchService watcher;
	private FileEventHandler<T> feh;
	
    private Map<WatchKey, T> data;
	private Map<WatchKey, Path> paths;
	
	private static final Logger log = LoggerFactory.getLogger(FileEventListener.class);

	/** Create a new FileEventListener object, duh. */
	public FileEventListener(FileEventHandler<T> feh) throws IOException {
   	
		this.feh = feh;
		this.data = new HashMap<WatchKey, T>();
		this.paths = new HashMap<WatchKey, Path>();
		this.watcher = FileSystems.getDefault().newWatchService();
            	
	}
    
	/**
	 * Register a new directory to be watched with some associated object/data for use in the callback function.
	 */
	public void register(Path directory, T datum) throws IOException {
		
		WatchKey key = directory.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
	
		data.put(key, datum);
		paths.put(key, directory);
		
		log.info("Registered w/ " + feh.getClass() +": ");
		log.info("\t" + directory.toString());
		log.info("\t" + datum.toString());
        
    }
    
	/**
	 * Initiates the listener.<br>
	 * <b>WARNING</b>: This will lock the thread. Ensure you aren't using the main thread if you plan on executing more logic on it.
	 */
	public void run() {
    	
		log.info("File listener initiated!");
		
		while(true) {
    		
			//Retrieve the triggered key...
			WatchKey triggeredKey;
			
			try {
				triggeredKey = watcher.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}

			//...and it's related data.
			T datum = data.get(triggeredKey);
			Path directory = paths.get(triggeredKey);

			//Iterate through any buffered events.
			for(WatchEvent<?> event : triggeredKey.pollEvents()) {
				
				//Kind of event (Creation, deletion, modification).
				Kind<?> kind = event.kind();

				//Assemble our complete absolute path.
				@SuppressWarnings("unchecked")
				Path filename = ((WatchEvent<Path>) event).context();	//.context only gives the filename and file-type (no directory)...
				Path completePath = directory.resolve(filename);		//...Use the paths/directories we stored during registry to complete the path. 
                
				//Log it!
				log.info(String.format("%s: %s with associated data: %s", kind.name(), completePath, datum.toString()));
				
				try {
					Thread.sleep(1000); //Creation events often occur before files are closed. Wait it out, otherwise there won't be any data in the file!
				} catch (InterruptedException e) {
					//Don't worry about it.
				}
				
				//Send it to the FileEventHandler to execute whatever desired behaviours.
				
				if(kind == ENTRY_CREATE)
					feh.created(completePath, datum);
				if(kind == ENTRY_DELETE)
                	feh.deleted(completePath, datum);
				if(kind == ENTRY_MODIFY)
					feh.modified(completePath, datum);
                
			}

			//Reset key and remove from set if directory no longer accessible
			if(!triggeredKey.reset()) {
               
				data.remove(triggeredKey);
				paths.remove(triggeredKey);

				if(data.isEmpty() || paths.isEmpty())	//All directories are inaccessible
					break;									//Break loop, there's nothing left to listen to.
                
			}
		}
		
		log.info("File listener stopped!");
    	
	}
    
}
