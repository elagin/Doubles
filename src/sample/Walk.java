package sample;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileChannel;
import java.util.zip.CRC32;

/**
 * Created by pavel on 11.01.16.
 */
public class Walk {

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
            System.out.print("Очень большой файл: " + filepath);
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
}
