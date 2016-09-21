package main.other;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Forrest Jones on 6/7/2016.
 */
public class FileOperation {

    private String previousPath;
    public String getPreviousPath() { return previousPath; }
    private String newPath;
    public String getNewPath() { return newPath; }
    private Date lastModified;
    public Date getLastModified() { return lastModified; }
    private boolean disabled = false;
    public boolean isDisabled() { return disabled; }
    public void setDisabled(boolean value) { disabled = value; }

    public FileOperation(String previousPath, String newPath, Date lastModified) {
        this.previousPath = previousPath;
        this.newPath = newPath;
        this.lastModified = lastModified;
    }
}
