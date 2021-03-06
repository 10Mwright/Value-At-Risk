package net.mdwright.var.application;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.mdwright.var.VarController;
import net.mdwright.var.ViewController;

/**
 * Main entry class, sets up views.
 * Some code originally provided by Dave Cohen in Software Engineering
 *     course (Calculator Coursework) Reused for this project with minor tweaks.
 * @author Matthew Wright, Dave Cohen
 */
public class Main extends Application {

  static FXMLLoader loader;
  static Parent root;

  private static Stage primaryStage;

  /**
   * Main (Driver) class for setting up and running application components.
   * @param args Command line arguments
   * @throws IOException In the event of an Application error
   */
  public static void main(String[] args) throws IOException {
    Application.launch(args);
  }

  /**
   * Method for setting up visual application components.
   * @param primaryStage Stage object for the primary stage to display on
   */
  @Override
  public void start(Stage primaryStage) throws IOException {
    System.out.println(Main.class.getResource("/fxml/EntranceGUI.fxml"));

    loader = new FXMLLoader(Main.class.getResource("/fxml/EntranceGUI.fxml"));
    root = (Parent) loader.load();
    EntranceGUI view = loader.getController();

    new ViewController(view);

    this.primaryStage = primaryStage;

    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Value at Risk (Main Menu)");
    primaryStage.centerOnScreen();
    primaryStage.show();
  }

  /**
   * Method to change the current scene to a new scene using a file path.
   * @param scenePath File path to fxml file
   * @param sceneName Name of scene to be set as the title
   * @throws IOException When the requested file path isn't valid or the fxml file isn't valid
   */
  public static void changeScene(String scenePath, String sceneName) throws IOException {
    ViewInterface view;

    System.out.println(Main.class.getResource(scenePath));
    loader = new FXMLLoader(Main.class.getResource(scenePath));
    root = (Parent) loader.load();
    view = loader.getController();

    new VarController(view);

    Scene newScene = new Scene(root);
    primaryStage.setScene(newScene);
    primaryStage.setWidth(root.getScene().getWidth());
    primaryStage.setHeight(root.getScene().getHeight());
    primaryStage.centerOnScreen(); //Centers the window on the users screen
    primaryStage.setTitle(sceneName);
  }

  /**
   * Method to add a second scene and show it in a second stage (no controller).
   * @param scenePath File path to fxml file
   * @param sceneName Name of scene to be set as the title
   * @throws IOException When the requested file path isn't valid or the fxml file isn't valid
   */
  public static void addScene(String scenePath, String sceneName) throws IOException {
    Stage secondaryStage = new Stage();

    loader = new FXMLLoader(Main.class.getResource(scenePath));
    Parent secondaryRoot = loader.load();

    Scene secondaryScene = new Scene(secondaryRoot);
    secondaryStage.setScene(secondaryScene);
    secondaryStage.setTitle(sceneName);
    secondaryStage.show();
  }
}
