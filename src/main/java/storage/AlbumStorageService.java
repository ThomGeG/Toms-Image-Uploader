package main.java.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
public class AlbumStorageService {
	
	/** Location of album meta-data on the local file system. */
	private final Path rootLocation;
	
	/** TODO */
	public final Map<String, Path> albums;
	
    @Autowired
    public AlbumStorageService(StorageProperties properties) {
    	
        this.rootLocation = Paths.get(properties.getLocation());
        
        //Create the album directory if it doesn't exist..
        if(!rootLocation.toFile().exists())
        	rootLocation.toFile().mkdir(); //create directory if it doesn't exist.
    
        albums = retrieveAlbums();
        
    }
    
    /** Creates a new album file on the local file system and adds it to the lookup map. */
    public void addAlbum(String albumID, Path directory) {
    
    	try {
			FileUtils.writeStringToFile(rootLocation.resolve(albumID + ".album").toFile(), directory.toString(), Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	albums.put(albumID, directory);
    	
    }
    
    /** Deletes an album file from the local file system and removes it from the lookup map. */
    public void removeAlbum(String albumID) {
    	
    	try {
    		Files.delete(rootLocation.resolve(albumID + ".album"));
			Files.delete(rootLocation.resolve(albumID + ".txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	albums.remove(albumID);
    	
    }
	
    /** Converts the album files on disk into a usable map of albumID -> directory. */
	private Map<String, Path> retrieveAlbums() {

		Map<String, Path> albums = new HashMap<String, Path>();
		Collection<File> rawAlbumFiles = FileUtils.listFiles(rootLocation.toFile(), new String[] {"txt", "album"}, false); //Collect all the album files for processing.
		
		for(File f : rawAlbumFiles) {
			
			Path directory = null;
			String albumID = FilenameUtils.getBaseName(f.getName()); //Get the albumID (which should be the filename).
			
			try {
				directory = Paths.get(FileUtils.readFileToString(f, Charset.defaultCharset())); //Convert the directory inside the file into a path.
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
		return getLocalImages(Paths.get(path));
	}
	
	/** Retrieves a collection of image files from the target directory. */
	public Collection<File> getLocalImages(Path path) {
		return FileUtils.listFiles(path.toFile(), new String[] {"jpg", "jpeg", "png", "gif", "apng", "tiff", "pdf", "xcf"}, true);
	}

}
