package sample;

import java.text.DecimalFormat;

/**
 * Created by pavel on 12.01.16.
 */
public class Utils {
    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"Б", "кБ", "МБ", "ГБ", "ТБ"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String workTimeToString(long start) {
        long totalTime = System.currentTimeMillis() - start;
        if(totalTime > 1000)
            totalTime = totalTime / 1000;
        else
            totalTime = 0;
        return String.format("Общее время %d сек.", totalTime);
    }
}
