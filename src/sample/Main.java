package sample;

import com.sun.javafx.font.freetype.HBGlyphLayout;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import static sample.Utils.readableFileSize;

public class Main extends Application {

    Walk walk = new Walk();
    //Preferences preferences = Preferences.userNodeForPackage(Main.class);

    List<FileInfo> fileList = new ArrayList<>();

    int fileProcessed = 0;
    long totalSize = 0;
    long speedBpS = 0;
    long totalTime = 0;

    Label curentFileLabel;

    private VBox infoBar;
    private HBox buttonBar;
    private Label totalFilesField;
    private Label totalBytesField;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");

        String dir = "/home/pavel";
        //dir = "/home/pavel/.thunderbird";
        //dir = "/media/pavel/80A89BFAA89BED42/Фото";
        //dir = "/mnt/nas/D/фото";
        //dir = "/media/pavel/80A89BFAA89BED42/Фото";
        //dir = "/home/pavel/.thumbnails/normal";
        //dir = "/home/pavel/Загрузки";
        //dir = "/home/pavel/Изображения/мото";
//        StackPane root = new StackPane();

//Creating a GridPane container
//        GridPane grid = new GridPane();
//        grid.setPadding(new Insets(10, 10, 10, 10));
//        grid.setVgap(5);
//        grid.setHgap(5);
//        grid.setGridLinesVisible(true);

//        VBox foldersBlock = new VBox(10);

//        HBox firstBox = new HBox(10);
//        foldersBlock.getChildren().add(firstBox);
//Defining the Name text field
        final TextField firstFolderField = new TextField();
        firstFolderField.setPromptText("Enter your first folder.");
        firstFolderField.setPrefColumnCount(10);
        //firstFolderField.setText(preferences.get(FIRST_FOLDER, ""));
        firstFolderField.getText();
        GridPane.setConstraints(firstFolderField, 0, 0);
        //firstBox.getChildren().add(firstFolderField);

        Button browseFirstFolder = new Button("Обзор...");
        GridPane.setConstraints(browseFirstFolder, 1, 0);
        //firstBox.getChildren().add(browseFirstFolder);
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

//        HBox secondBox = new HBox(10);
//        foldersBlock.getChildren().add(secondBox);
//        final TextField secondFolder = new TextField();
//        secondFolder.setPromptText("Enter your second folder.");
//        GridPane.setConstraints(secondFolder, 0, 1);
//        secondFolder.setText(preferences.get(SECOND_FOLDER, ""));
//        secondBox.getChildren().add(secondFolder);

//        Button browseSecondFolder = new Button("Обзор...");
//        GridPane.setConstraints(browseSecondFolder, 1, 1);
//        browseSecondFolder.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent e) {
//                DirectoryChooser directoryChooser = new DirectoryChooser();
//                directoryChooser.setTitle("Выбор второй папки");
//                if (secondFolder.getText() != null)
//                    directoryChooser.setInitialDirectory(new File(secondFolder.getText()));
//                try {
//                    File file = directoryChooser.showDialog(null);
//                    if (file != null) {
//                        secondFolder.setText(file.getPath());
//                    }
//                } catch (IllegalArgumentException ex) {
//                    ex.getLocalizedMessage();
//                    directoryChooser.setInitialDirectory(null);
//                    File file = directoryChooser.showDialog(null);
//                    if (file != null) {
//                        secondFolder.setText(file.getPath());
//                    }
//                }
//            }
//        });
//        secondBox.getChildren().add(browseSecondFolder);

//        HBox destBox = new HBox(10);
//        foldersBlock.getChildren().add(destBox);

//        final TextField distFolder = new TextField();
//        distFolder.setPrefColumnCount(15);
//        distFolder.setPromptText("Enter your dist folder");
//        GridPane.setConstraints(distFolder, 0, 2);
//        destBox.getChildren().add(distFolder);

        Button startBtn = new Button();
        startBtn.setText("Старт");
        startBtn.setOnAction(new EventHandler<ActionEvent>() {
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
                        Files.walk(Paths.get(dir)).forEach(filePath -> {
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

        //root.getChildren().add(foldersBlock);

        Button stopBtn = new Button();
        stopBtn.setText("Стоп");

        VBox publicBlock = new VBox(10);
        publicBlock.setPadding(new Insets(10, 10, 10, 10));
        publicBlock.setPrefHeight(150);
        publicBlock.setAlignment(Pos.CENTER);

        buttonBar = new HBox(10);
        buttonBar.setPadding(new Insets(10, 10, 10, 10));
        buttonBar.setPrefHeight(150);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.getChildren().add(startBtn);
        buttonBar.getChildren().add(stopBtn);
        publicBlock.getChildren().add(buttonBar);

        infoBar = new VBox(10);
        infoBar.setPadding(new Insets(10, 30, 50, 30));
        infoBar.setPrefHeight(150);
        infoBar.setAlignment(Pos.BOTTOM_LEFT);
        publicBlock.getChildren().add(infoBar);

        totalFilesField = new Label("Обработано 0 файлов.");
//        infoBar.getChildren().add(totalFilesField);

        totalBytesField = new Label("Обработано 0 байт.");
//        infoBar.getChildren().add(totalBytesField);

        //root.getChildren().add(publicBlock);

        //root.getChildren().add(stopBtn);
/*
        curentFileLabel = new Label();
        infoBar.getChildren().add(curentFileLabel);

//        TextField textField = new TextField ();
//        HBox hb = new HBox();
//        hb.getChildren().addAll(label1, textField);
//        hb.setSpacing(10);
//
//        GridPane.setConstraints(curentFileLabel, 0, 3);
//        GridPane.setColumnSpan(curentFileLabel, 2);
//        grid.getChildren().add(curentFileLabel);
//
//        curentFileLabel.setText("ssssss");
*/
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void walkin(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null) {
            for (int i=0; i<listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    walkin(listFile[i]);
                } else {
                        System.out.println(listFile[i].getPath());
                }
            }
        }
    }
}
