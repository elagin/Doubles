package sample;

/**
 * Created by pavel on 11.01.16.
 */
public class FileInfo {
    public Long crc;
    public Long size;
    public String name;

    public FileInfo(long crc, String name) {
        this.crc = crc;
        this.name = name;
    }

    public FileInfo(long crc, long size, String name) {
        this.crc = crc;
        this.size = size;
        this.name = name;
    }
}
