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
 * Main entry class, sets up views.
 * Code originally provided by Dave Cohen in Software Engineering
 * course (Calculator Coursework) Reused for this project with minor tweaks.
 * @author Matthew Wright, Dave Cohen
 */
public class Main extends Application {

  static FXMLLoader loader;
  static Parent root;

  /**
   * Main (Driver) class for setting up and running application components.
   * @param args Command line arguments
   * @throws IOException In the event of an Application error
   */
  public static void main(String[] args) throws IOException {
    ViewInterface view;

    System.out.println(Main.class.getResource("/fxml/ModelBuildingGUI.fxml"));
    loader = new FXMLLoader(Main.class.getResource("/fxml/ModelBuildingGUI.fxml"));
    root = (Parent) loader.load();
    view = loader.getController();

    new VarController(view);

    Application.launch(args);
  }

  /**
   * Method for setting up visual application components.
   * @param primaryStage Stage object for the primary stage to display on
   */
  @Override
  public void start(Stage primaryStage) {
    Scene scene = new Scene(root, 600, 572);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
