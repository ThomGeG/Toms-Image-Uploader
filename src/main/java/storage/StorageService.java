package main.java.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Storage service for tracking album locations between executions.
 * The system stores a number of files that represent albums to be sync'd by the application.
 * 
 * @author Tom
 */
@Service
public class StorageService {
	
	/** Location of album meta-data on the local file system. */
	private final Path rootLocation;
	
    @Autowired
    public StorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
        
        if(!rootLocation.toFile().exists())
        	rootLocation.toFile().mkdir(); //create directory if it doesn't exist.
    
    }
	
    /**
     * Produces a map of the albums to be sync'd by the application.<br>
     * Album ID's are used as keys to ascertain their corresponding directories.
     */
	public Map<String, String> getLocalAlbums() {

		Map<String, String> albums = new HashMap<String, String>();
		Collection<File> rawAlbumFiles = FileUtils.listFiles(rootLocation.toFile(), new String[] {"txt", "album"}, false);
		
		for(File f : rawAlbumFiles) {
			
			String directory = null;
			String albumID = FilenameUtils.getBaseName(f.getName());
			
			try {
				directory = FileUtils.readFileToString(f, Charset.defaultCharset());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(directory != null)
				albums.put(albumID, directory);
			
		}
		
		return albums;
		
	}
	
	/** Retrieves a collection of image files from the target directory. */
	public Collection<File> getLocalImages(String path) {
		return getLocalImages(new File(path));
	}
	
	/** Retrieves a collection of image files from the target directory. */
	public Collection<File> getLocalImages(File path) {
		return FileUtils.listFiles(path, new String[] {"jpg", "jpeg", "png", "gif", "apng", "tiff", "pdf", "xcf"}, true);
	}

}
