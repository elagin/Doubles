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
    private Button stopButton;

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

    @FXML
    private Button scanSecondFolder;

    @FXML
    private Button checkButton;

    // MODEL
    private final Model model = new Model();

    private boolean threadIsActive = false;

    private Walk walk = new Walk();
    private Thread filesWalkThread = null;

    private long startFilelistTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bindStartButtonEvents();
        bindStopButtonEvents();
        bindBrowseButtonsEvents();
        bindScanButtonsEvents();
        bindCheckButtonEvents();
        initFields();

        model.loadSlow();
    }

    private void initFields() {
        stopButton.setDisable(true);
        firstFolderField.setText(model.getFirstFolder());
        secondFolderField.setText(model.getSecondFolder());
        destFolderField.setText(model.getDestFolder());
    }

    private void saveFields() {
        model.setFirstFolder(firstFolderField.getText());
        model.setSecondFolder(secondFolderField.getText());
        model.setDestFolder(destFolderField.getText());
    }

    private void bindBrowseButtonsEvents() {
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
     * binds events to the start button.
     */
    private void bindStartButtonEvents() {
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Старт");
                startButton.setDisable(true);
                stopButton.setDisable(false);
                saveFields();
                model.reset();

                Task task = new Task<Void>() {
                    @Override
                    public Void call() throws Exception {
                        try {
                            long startTimer = System.currentTimeMillis();
                            Files.walk(Paths.get(firstFolderField.getText())).forEach(filePath -> {
                                if (Files.isRegularFile(filePath)) {
                                    final String fileName = filePath.toString();
                                    try {
                                        FileInfo fileInfo = walk.checksumMappedFile(fileName);
                                        if (fileInfo != null) {
                                            model.addFile(fileInfo);
                                        }
                                    } catch (InterruptedException e) {
                                        if (e.getLocalizedMessage().equals("Stop by User"))
                                            throw new RuntimeException("Stop by User");
                                        else
                                            e.printStackTrace();
                                    }

                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            curentFileLabel.setText(fileName);
                                            totalFilesField.setText("Обработано " + model.getFileProcessed() + " файлов.");
                                            totalBytesField.setText("Общий объем " + readableFileSize(model.getTotalSize()) + " Общее время \t" + model.getTotalTime() + " сек. Скорость: " + readableFileSize(model.getSpeedBpS()) + " / сек.");
                                        }
                                    });
                                }
                            });

                        } catch (RuntimeException e) {
                            if (e.getLocalizedMessage().equals("Stop by User"))
                                System.out.println(e.getLocalizedMessage());
                            else
                                e.printStackTrace();
                        } finally {
                            resetButtons();
                        }
                        return null;
                    }
                };
                filesWalkThread = new Thread(task);
                filesWalkThread.setDaemon(true);
                filesWalkThread.start();
                threadIsActive = true;
            }
        });
    }

    private void resetButtons() {
        System.out.println("Стоп");
        threadIsActive = false;
        startButton.setDisable(false);
        stopButton.setDisable(true);
        model.saveSlow();
    }

    private void bindStopButtonEvents() {
        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (filesWalkThread != null && threadIsActive)
                    filesWalkThread.interrupt();
            }
        });
    }

    private void bindScanButtonsEvents() {
        scanSecondFolder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Старт second folder");
                model.setSecondFolder(secondFolderField.getText());
                startFilelistTimer = System.currentTimeMillis();
                //startButton.setDisable(true);
                //stopButton.setDisable(false);
                //saveFields();
                //model.reset();

                Task task = new Task<Void>() {
                    @Override
                    public Void call() throws Exception {
                        List<String> fileList = new ArrayList<>();
                        try {
                            Files.walk(Paths.get(secondFolderField.getText())).forEach(filePath -> {
                                if (Files.isRegularFile(filePath)) {
                                    String fileName = filePath.toString();
                                    fileList.add(fileName);
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            curentFileLabel.setText(fileName);
                                            totalFilesField.setText("Обработано " + fileList.size() + " файлов.");
                                            totalBytesField.setText(Utils.workTimeToString(startFilelistTimer));
                                        }
                                    });
                                }
                            });
                        } catch (RuntimeException e) {
                            if (e.getLocalizedMessage().equals("Stop by User"))
                                System.out.println(e.getLocalizedMessage());
                            else
                                e.printStackTrace();
                        } finally {
                            System.out.println("Обработано " + fileList.size() + " файлов за" + Utils.workTimeToString(startFilelistTimer));
                            scanSecondFolder(fileList);
                        }
                        return null;
                    }
                };
                filesWalkThread = new Thread(task);
                filesWalkThread.setDaemon(true);
                filesWalkThread.start();
                //threadIsActive = true;
            }
        });
    }

    private void scanSecondFolder(List<String> fileList) {
        long startTimer = System.currentTimeMillis();
        System.out.println("Start scanSecondFolder");
        model.reset();
        for (String fileName : fileList) {
            try {
                FileInfo fileInfo = walk.checksumMappedFile(fileName);
                if (fileInfo != null) {
                    model.addToCache(fileInfo);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            curentFileLabel.setText(fileName);
                            totalFilesField.setText("Обработано " + model.getCacheSize() + " файлов.");
                            //totalBytesField.setText(Utils.workTimeToString(startFilelistTimer));
                            totalBytesField.setText("Общий объем " + readableFileSize(model.getTotalSize()) + " Общее время \t" + model.getTotalTime() + " сек. Скорость: " + readableFileSize(model.getSpeedBpS()) + " / сек.");
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Stop scanSecondFolder");
        System.out.println(Utils.workTimeToString(startTimer));
        model.saveSlow();
    }


    private void bindCheckButtonEvents() {
        checkButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Старт check folder and cache");
                //model.setSecondFolder(firstFolderField.getText());
                startFilelistTimer = System.currentTimeMillis();
                //startButton.setDisable(true);
                //stopButton.setDisable(false);
                //saveFields();
                //model.reset();

                Task task = new Task<Void>() {
                    @Override
                    public Void call() throws Exception {
                        //List<String> fileList = new ArrayList<>();
                        final long[] processedFiles = {0};
                        try {
                            Files.walk(Paths.get(firstFolderField.getText())).forEach(filePath -> {
                                if (Files.isRegularFile(filePath)) {
                                    String fileName = filePath.toString();
                                    try {
                                        FileInfo fileInfo = walk.checksumMappedFile(fileName);
                                        String oldFileName = model.getFileNameFromCache(fileInfo.crc);
                                        if (oldFileName != null)
                                            System.out.println(String.format("cached: %s <-> %s", oldFileName, fileName));
                                        processedFiles[0]++;
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                curentFileLabel.setText(fileName);
                                                totalFilesField.setText("Обработано " + processedFiles[0] + " файлов.");
                                                totalBytesField.setText(Utils.workTimeToString(startFilelistTimer));
                                            }
                                        });
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (RuntimeException e) {
                            if (e.getLocalizedMessage().equals("Stop by User"))
                                System.out.println(e.getLocalizedMessage());
                            else
                                e.printStackTrace();
                        } finally {
                            System.out.println("Обработано " + processedFiles[0] + " файлов.");
                        }
                        return null;
                    }
                };
                filesWalkThread = new Thread(task);
                filesWalkThread.setDaemon(true);
                filesWalkThread.start();
                //threadIsActive = true;
            }
        });
    }

}
