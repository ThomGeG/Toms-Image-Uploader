package main.java.services.io;

import java.nio.file.Path;

/**
 * Interface for implementation of strategies with the FileEventListener.
 * Separates action logic from listening logic.
 * 
 * @see main.java.services.io.FileEventListener
 *
 * @param <T> Data-type of associated data.
 * @author Tom
 */
public interface FileEventHandler<T> {
	
	public void created(Path p, T datum);
	
	public void deleted(Path p, T datum);
	
	public void modified(Path p, T datum);

}
