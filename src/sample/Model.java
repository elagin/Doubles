package sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Map<Long, FileInfo> fileMap = new HashMap<>();
    private SlowDriveCache slowDriveCache = new SlowDriveCache();

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
        try {
            fileList.add(fileInfo);
            FileInfo old = fileMap.get(fileInfo.crc);
            if(old != null) {
                System.out.println("Уже существует " + old.name + " <-> " + fileInfo.name + " CRC: " + fileInfo.crc);
            }
            fileMap.put(fileInfo.crc, fileInfo);
            updateStatistics(fileInfo.size);
        } catch (Exception ex) {
            ex.getLocalizedMessage();
        }
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
        startTime = System.currentTimeMillis();
        fileList.clear();
        fileMap.clear();
        totalSize = 0;
        speedBpS = 0;
        totalTime = 0;
    }

    private void updateStatistics(long fileSize) {
        totalSize += fileSize;
        this.totalTime = System.currentTimeMillis() - startTime;
        if (totalTime > 1000) {
            totalTime = totalTime / 1000;
            speedBpS = totalSize / totalTime;
        }
    }

    public void checkDupes(FileInfo fileInfo) {
//        HashMap<Long, Long> hmFiles = new HashMap<Long, Long>();
//        for(FileInfo item : fileList) {
//            Long x = hmFiles.get(item.crc);
//        }
    }

    public void loadSlow() {
        slowDriveCache.deserialize();
    }

    public void saveSlow() {
        slowDriveCache.serialize();
    }

    public void addToCache(FileInfo fileInfo) {
        slowDriveCache.add(fileInfo.name, fileInfo.crc);
        updateStatistics(fileInfo.size);
    }

    public int getCacheSize() {
        return slowDriveCache.size();
    }

    public Long getCrcFromCache(String name) {
        return slowDriveCache.getCrc(name);
    }

    public String getFileNameFromCache(Long crc) {
        return slowDriveCache.getFileName(crc);
    }
}