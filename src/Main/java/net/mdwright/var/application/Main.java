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

  public static void main(String[] args) throws IOException {
    ViewInterface view;

    System.out.println(Main.class.getResource("/fxml/ModelBuildingGUI.fxml"));
    loader = new FXMLLoader(Main.class.getResource("/fxml/ModelBuildingGUI.fxml"));
    root = (Parent) loader.load();
    view = loader.getController();

    new VarController(view);

    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    Scene scene = new Scene(root, 600, 572);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public URL getLoadURL() {
    URL loadURL = getClass().getResource("/fxml/ModelBuildingGUI.fxml");

    return loadURL;
  }
}
