package sample;

import java.util.prefs.Preferences;

/**
 * Created by pavel on 12.01.16.
 */
public class Model {

    final String FIRST_FOLDER = "firstFolder";
    final String SECOND_FOLDER = "secondFolder";
    final String DESTINATION_FOLDER = "destFolder";

    Preferences preferences = Preferences.userNodeForPackage(Main.class);

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

}
