package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Created by pavel on 12.01.16.
 */
public class Model {

    private final String FIRST_FOLDER = "firstFolder";
    private final String SECOND_FOLDER = "secondFolder";
    private final String DESTINATION_FOLDER = "destFolder";

    private long totalSize = 0;
    private long speedBpS = 0;
    private long totalTime = 0;
    private long startTime = 0;

    private Preferences preferences = Preferences.userNodeForPackage(Main.class);
    private List<FileInfo> fileList = new ArrayList<>();

    public String getFirstFolder() {
        return preferences.get(FIRST_FOLDER, "");
    }

    public void setFirstFolder(String value) {
        preferences.put(FIRST_FOLDER, value);
    }

    public String getSecondFolder() {
        return preferences.get(SECOND_FOLDER, "");
    }

    public void setSecondFolder(String value) {
        preferences.put(SECOND_FOLDER, value);
    }

    public String getDestFolder() {
        return preferences.get(DESTINATION_FOLDER, "");
    }

    public void setDestFolder(String value) {
        preferences.put(DESTINATION_FOLDER, value);
    }

    public void addFile(FileInfo fileInfo) {
        fileList.add(fileInfo);
        updateStatistics(fileInfo.size);
    }

    public int getFileProcessed() {
        return fileList.size();
    }

    public long getSpeedBpS() {
        return speedBpS;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void reset() {
        startTime =  System.currentTimeMillis();
        fileList.clear();
        totalSize = 0;
        speedBpS = 0;
        totalTime = 0;
    }

    protected void updateStatistics(long fileSize) {
        totalSize += fileSize;
        this.totalTime = System.currentTimeMillis() - startTime;
        if (totalTime > 1000) {
            totalTime = totalTime / 1000;
            speedBpS = totalSize / totalTime;
        } else {
            speedBpS = totalSize / totalTime;
        }
    }
}