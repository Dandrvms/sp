package com.ceos.sparkline;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        SparkLine line = new SparkLine();
        Scene scene = new Scene(line, 400, 400);
        stage.setTitle("Widget Test");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
