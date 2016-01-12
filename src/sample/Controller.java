package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static sample.Utils.readableFileSize;

//https://github.com/dmpe/JavaFX

public class Controller implements Initializable {

    @FXML
    private Button startButton;

    @FXML
    private TextField firstFolderField;

    @FXML
    private Label curentFileLabel;

    @FXML
    private Label totalFilesField;

    @FXML
    private Label totalBytesField;

    @FXML
    private Button browseFirstFolder;

    @FXML
    private TextField secondFolderField;

    @FXML
    private Button browseSecondFolder;

    @FXML
    private TextField destFolderField;

    @FXML
    private Button browseDestFolder;

    // MODEL
    private final Model model = new Model();

    int fileProcessed = 0;
    long totalSize = 0;
    long speedBpS = 0;
    long totalTime = 0;

    Walk walk = new Walk();
    List<FileInfo> fileList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bindStartButtonEvents();
        browseButtonsEvents();
    }

    private void browseButtonsEvents() {
        browseFirstFolder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Выбор первой папки");
                if (firstFolderField.getText() != null)
                    directoryChooser.setInitialDirectory(new File(firstFolderField.getText()));
                try {
                    File file = directoryChooser.showDialog(null);
                    if (file != null) {
                        firstFolderField.setText(file.getPath());
                    }
                } catch (IllegalArgumentException ex) {
                    ex.getLocalizedMessage();
                    directoryChooser.setInitialDirectory(null);
                    File file = directoryChooser.showDialog(null);
                    if (file != null) {
                        firstFolderField.setText(file.getPath());
                    }
                }
            }
        });

        browseSecondFolder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Выбор первой папки");
                if (secondFolderField.getText() != null)
                    directoryChooser.setInitialDirectory(new File(secondFolderField.getText()));
                try {
                    File file = directoryChooser.showDialog(null);
                    if (file != null) {
                        secondFolderField.setText(file.getPath());
                    }
                } catch (IllegalArgumentException ex) {
                    ex.getLocalizedMessage();
                    directoryChooser.setInitialDirectory(null);
                    File file = directoryChooser.showDialog(null);
                    if (file != null) {
                        secondFolderField.setText(file.getPath());
                    }
                }
            }
        });

        browseDestFolder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Выбор первой папки");
                if (destFolderField.getText() != null)
                    directoryChooser.setInitialDirectory(new File(destFolderField.getText()));
                try {
                    File file = directoryChooser.showDialog(null);
                    if (file != null) {
                        destFolderField.setText(file.getPath());
                    }
                } catch (IllegalArgumentException ex) {
                    ex.getLocalizedMessage();
                    directoryChooser.setInitialDirectory(null);
                    File file = directoryChooser.showDialog(null);
                    if (file != null) {
                        destFolderField.setText(file.getPath());
                    }
                }
            }
        });
    }

    /**
     * binds events to the start button. by pressing the start button, the game
     * is initialized and the timeline execution is started.
     */
    private void bindStartButtonEvents() {
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Старт");
                //String firstFolder = firstFolderField.getText();
//                preferences.put(FIRST_FOLDER, firstFolder);
//                preferences.put(SECOND_FOLDER, secondFolder.getText());

                Task task = new Task<Void>() {
                    @Override
                    public Void call() throws Exception {
                        long startTimer = System.currentTimeMillis();
                        //walkin(new File(firstFolder));
                        Files.walk(Paths.get(firstFolderField.getText())).forEach(filePath -> {
                            if (Files.isRegularFile(filePath)) {
                                final String fileName = filePath.toString();
                                try {
                                    FileInfo fileInfo = walk.scan(fileName);
                                    if (fileInfo != null) {
                                        fileList.add(fileInfo);

                                        totalSize += fileInfo.size;
                                        fileProcessed++;
                                        totalTime = System.currentTimeMillis() - startTimer;
                                        if (totalTime > 1000) {
                                            totalTime = totalTime / 1000;
                                            speedBpS = totalSize / totalTime;
                                        } else {
                                            speedBpS = totalSize / totalTime;
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        curentFileLabel.setText(fileName);
                                        totalFilesField.setText("Обработано " + fileProcessed + " файлов.");
                                        totalBytesField.setText("Общий объем " + readableFileSize(totalSize) + " Общее время \t" + totalTime + " сек. Скорость: " + readableFileSize(speedBpS) + " / сек.");
                                    }
                                });
                            }
                        });
                        return null;
                    }
                };
                Thread th = new Thread(task);
                th.setDaemon(true);
                th.start();
            }
        });
    }
}
