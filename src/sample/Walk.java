package sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.CRC32;

/**
 * Created by pavel on 11.01.16.
 */
public class Walk {

    Map<Long, String> fileMap = new HashMap<Long, String>();
    List<FileInfo> fileList = new ArrayList<>();

    int fileProcessed = 0;
    long totalSize = 0;
    long speedBpS = 0;

    public FileInfo scan(String fileName) throws IOException, InterruptedException {
//        long startTimer = System.currentTimeMillis();
//        Files.walk(Paths.get(dir)).forEach(filePath -> {
//            if (Files.isRegularFile(filePath)) {
//                final String fileName = filePath.toString();
        return getCheckSumFile(fileName);
//        FileInfo fileInfo = getCheckSumFile(fileName);
//        if (fileInfo != null) {
//            totalSize += fileInfo.size;
            //long totalTime = System.currentTimeMillis() - startTimer;
//            fileProcessed++;
//                    if (totalTime > 1000) {
//                        totalTime = totalTime / 1000;
//                        speedBpS = totalSize / totalTime;
//                        System.out.println(" Общий объем " + readableFileSize(totalSize) + " Общее время \t" + totalTime + " сек. Скорость: " + readableFileSize(speedBpS) + " / сек.");
//                    } else {
//                        speedBpS = totalSize / totalTime;
//                        System.out.println(" Общий объем " + readableFileSize(totalSize) + " Общее время \t" + totalTime + " мсек. Скорость: " + readableFileSize(speedBpS) + " / сек.");
//                    }

//            fileInfo.setName(fileName);
//            fileList.add(fileInfo);

//                    String oldFile = fileMap.get(fileInfo.crc);
//                    if (oldFile == null)
//                        fileMap.put(fileInfo.crc, fileName);
//                    else {
//                        System.out.println("Одинаковые файлы:");
//                        getFileDetails(fileName);
//                        getFileDetails(oldFile);
//                        System.out.println("-----------------");
//                    }

//        }

//            }
//        });
//        long totalTime = System.currentTimeMillis() - startTimer;
//        totalTime = totalTime / 1000;
//        speedBpS = totalSize / totalTime;
//        System.out.println("Обработано "+ fileProcessed + " файлов. Общий объем " + readableFileSize(totalSize) + " Общее время \t" + totalTime + " сек. Скорость: " + readableFileSize(speedBpS) + " / сек.");
    }

    private String cutFileName(String filename) {
        final int size = 60;
        if (filename.length() > size)
            return filename.substring(filename.length() - size - 1, filename.length());
        else
            return filename;
    }

    private String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"Б", "кБ", "МБ", "ГБ", "ТБ"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public FileInfo getCheckSumFile(String filepath) throws InterruptedException {
        //System.out.print(cutFileName(filepath));
        return checksumMappedFile(filepath);
        //long start_timer = System.currentTimeMillis();
/*
        try {
            FileInfo fileInfo = checksumMappedFile(filepath);
            String oldFile = fileMap.get(fileInfo.crc);
            if (oldFile == null)
                fileMap.put(fileInfo.crc, filepath);
            else {
                System.out.println("Одинаковые файлы:");
                getFileDetails(filepath);
//                System.out.println(filepath + " но уже есть => " + oldFile + "!!!!!!");
                getFileDetails(oldFile);
                System.out.println("-----------------");
            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println(e.getLocalizedMessage());
        } catch (UncheckedIOException ue) {
            ue.printStackTrace();
            System.out.println(ue.getLocalizedMessage());
        }
//        long end_timer = System.currentTimeMillis();
        //long timeDiff = end_timer - start_timer;
//        File file = new File(filepath);
//        long speed = 0;
//        long fileSizeKb = file.length() / 1024;
//        if (timeDiff != 0) {
//            timeDiff = timeDiff / 1000;
//            speed = fileSizeKb / timeDiff;
//        }
//        System.out.println(" : " + (timeDiff) + " ms. Size: " + fileSizeKb + "кб. Speed " + speed + "кб.сек.");
*/
    }

    public FileInfo checksumMappedFile(String filepath) throws InterruptedException {
        FileInputStream inputStream = null;
        FileChannel fileChannel = null;
        CRC32 crc = new CRC32();
        try {
            inputStream = new FileInputStream(filepath);
            fileChannel = inputStream.getChannel();
            int len = (int) fileChannel.size();
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, len);

            for (int cnt = 0; cnt < len; cnt++) {
                int i = buffer.get(cnt);
                crc.update(i);
            }
            FileInfo fileInfo = new FileInfo(crc.getValue(), len, filepath);
            return fileInfo;
        } catch (FileNotFoundException e) {
            System.out.print("name: " + filepath);
            e.printStackTrace();
        } catch (ClosedByInterruptException e) {
            throw new InterruptedException("Stop by User");
        } catch (IOException e) {
            System.out.print(e.getLocalizedMessage() + filepath);
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
//            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
//            long maxMemory = heapUsage.getMax() / MEGABYTE;
//            long usedMemory = heapUsage.getUsed() / MEGABYTE;
//            System.out.println(i + " : Memory Use :" + usedMemory + "M/" + maxMemory + "M");
            System.out.print("Очень большой файл: " + filepath);
//        } catch (IOException e) {
//            System.out.print("name: " + filepath);
//            e.printStackTrace();
        } finally {
            if (fileChannel != null)
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    public void getFileDetails(String filename) {
        File file = new File(filename);
        System.out.print("name: " + filename);
        System.out.print(" size: " + file.length());
        System.out.println(" last modified: " + new Date(file.lastModified()));
    }

}
