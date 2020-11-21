package net.mdwright.var.application;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.mdwright.var.VarController;

/**
 * Main entry class, sets up views. Code originally provided by Dave Cohen in Software Engineering
 * course (Calculator Corusework) Repurposed for this project
 */
public class Main extends Application {

  static FXMLLoader loader;
  static Parent root;

  static URL loadURL = Main.class.getResource("/java/resources/ModelBuildingGUI.fxml");

  public static void main(String[] args) throws IOException {
    System.out.println(Main.class.getClassLoader().getResource("/fxml/ModelBuildingGUI.fxml"));
    loader = new FXMLLoader(loadURL);
    root = (Parent) loader.load();
    ViewInterface view = loader.getController();

    new VarController(view);

    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    Scene scene = new Scene(root, 600, 572);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
