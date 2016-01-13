package sample;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pavel on 13.01.16.
 */
public class SlowDriveCache {

    private Map<String, Long> map = new HashMap<>();
    private final String fileName = "slow.bin";

    public void add(String name, Long crc) {
        map.put(name, crc);
    }

    public Long getCrc(String fileName) {
        return map.get(fileName);
    }

    public void serialize() {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(map);
            out.flush();
            out.close();
            fileOut.close();
            System.out.println("Сохранено "+ map.size() + " элементов кэша в "+ fileName);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public void deserialize() {
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            map = (Map<String, Long>) in.readObject();
            in.close();
            fileIn.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getLocalizedMessage());
            return;
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("Slow drive cache class not found");
            c.printStackTrace();
            return;
        }
        System.out.println("Загружено "+ map.size() + " элементов кэша" );
    }

    public int size() {
        return map.size();
    }

    public String getFileName(Long crc) {
        for (Map.Entry<String, Long> e : map.entrySet()) {
            String key = e.getKey();
            Long value = e.getValue();
            if(value.equals(crc))
                return key;
        }
        return null;
    }
}
