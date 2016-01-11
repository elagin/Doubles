package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class Main extends Application {

    Walk walk = new Walk();
    Preferences preferences = Preferences.userNodeForPackage(Main.class);

    final String FIRST_FOLDER = "firstFolder";
    final String SECOND_FOLDER = "secondFolder";
    Label curentFileLabel;

    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");

        String dir = "/home/pavel";
        //dir = "/home/pavel/.thunderbird";
        //dir = "/media/pavel/80A89BFAA89BED42/Фото";
        //dir = "/mnt/nas/D/фото";
        //dir = "/media/pavel/80A89BFAA89BED42/Фото";
        //dir = "/home/pavel/.thumbnails/normal";
        //dir = "/home/pavel/Загрузки";
        //dir = "/home/pavel/Изображения/мото";
        StackPane root = new StackPane();

//Creating a GridPane container
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);
//Defining the Name text field
        final TextField firstFolderField = new TextField();
        firstFolderField.setPromptText("Enter your first folder.");
        firstFolderField.setPrefColumnCount(10);
        firstFolderField.setText(preferences.get(FIRST_FOLDER, ""));
        firstFolderField.getText();
        GridPane.setConstraints(firstFolderField, 0, 0);
        grid.getChildren().add(firstFolderField);
//Defining the Last Name text field
        final TextField secondFolder = new TextField();
        secondFolder.setPromptText("Enter your second folder.");
        GridPane.setConstraints(secondFolder, 0, 1);
        secondFolder.setText(preferences.get(SECOND_FOLDER, ""));
        grid.getChildren().add(secondFolder);
//Defining the Comment text field
        final TextField distFolder = new TextField();
        distFolder.setPrefColumnCount(15);
        distFolder.setPromptText("Enter your dist folder");
        GridPane.setConstraints(distFolder, 0, 2);
        grid.getChildren().add(distFolder);

        Button browseFirstFolder = new Button("Обзор...");
        GridPane.setConstraints(browseFirstFolder, 1, 0);
        grid.getChildren().add(browseFirstFolder);
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

//Defining the Clear button
        Button browseSecondFolder = new Button("Обзор...");
        GridPane.setConstraints(browseSecondFolder, 1, 1);
        browseSecondFolder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Выбор второй папки");
                if (secondFolder.getText() != null)
                    directoryChooser.setInitialDirectory(new File(secondFolder.getText()));
                try {
                    File file = directoryChooser.showDialog(null);
                    if (file != null) {
                        secondFolder.setText(file.getPath());
                    }
                } catch (IllegalArgumentException ex) {
                    ex.getLocalizedMessage();
                    directoryChooser.setInitialDirectory(null);
                    File file = directoryChooser.showDialog(null);
                    if (file != null) {
                        secondFolder.setText(file.getPath());
                    }
                }
            }
        });


        grid.getChildren().add(browseSecondFolder);

        root.getChildren().add(grid);

        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
//                try {
                String firstFolder = firstFolderField.getText();
                preferences.put(FIRST_FOLDER, firstFolder);
                preferences.put(SECOND_FOLDER, secondFolder.getText());

                Task task = new Task<Void>() {
                    @Override
                    public Void call() throws Exception {
                        int i = 0;
                        Files.walk(Paths.get(dir)).forEach(filePath -> {
                            if (Files.isRegularFile(filePath)) {
                                final String fileName = filePath.toString();
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        curentFileLabel.setText(fileName);
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
        root.getChildren().add(btn);

        curentFileLabel = new Label();
//        TextField textField = new TextField ();
//        HBox hb = new HBox();
//        hb.getChildren().addAll(label1, textField);
//        hb.setSpacing(10);

        GridPane.setConstraints(curentFileLabel, 0, 3);
        GridPane.setColumnSpan(curentFileLabel, 2);
        grid.getChildren().add(curentFileLabel);

        curentFileLabel.setText("ssssss");

        primaryStage.setScene(new Scene(root, 600, 475));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
