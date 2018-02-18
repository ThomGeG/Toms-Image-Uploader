package main.java.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** 
 * Configuration file for the storage location of album data.
 * 
 * @author Tom
 */
@ConfigurationProperties("storage")
public class StorageProperties {

    /** Folder location for storing album meta-data. */
	@Value("${ALBUM_FOLDER}")
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}