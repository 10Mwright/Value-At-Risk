package net.mdwright.var;

import java.io.IOException;
import net.mdwright.var.application.EntranceGUI;
import net.mdwright.var.application.Main;

/**
 * Controller class for changing screens at main menu.
 *
 * @author Matthew Wright
 */
public class ViewController {

  /**
   * Constructor method which adds observers to the menu screen.
   * @param view EntraceGUI object
   */
  public ViewController(EntranceGUI view) {
    view.addModelBuildingObserver(this::changeToModelBuilding);
    view.addHistoricalSimObserver(this::changeToHistoricalSim);
    view.addHelpObserver(this::showHelpScreen);
    view.addVersionObserver(this::showVersionScreen);
  }

  /**
   * Method called when the Model-Building button is pressed, changes to model-building screen.
   */
  public void changeToModelBuilding() {
    try {
      Main.changeScene("/fxml/ModelBuildingGUI.fxml",
          "Value at Risk (Model-Building Methodology)");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Method called when the Historical Simulation button is pressed, changes to historical
   *     sim screen.
   */
  public void changeToHistoricalSim() {
    try {
      Main.changeScene("/fxml/HistoricalSimGUI.fxml",
          "Value at Risk (Historical Simulation Methodology)");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Method called to show the help screen as a secondary screen.
   */
  public void showHelpScreen() {
    try {
      Main.addScene("/fxml/HelpScreen.fxml", "Help Screen");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Method called to show the version screen as a secondary screen.
   */
  public void showVersionScreen() {
    try {
      Main.addScene("/fxml/VersionScreen.fxml", "Version Screen");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
