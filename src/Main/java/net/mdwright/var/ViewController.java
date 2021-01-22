package net.mdwright.var;

import java.io.IOException;
import net.mdwright.var.application.EntranceGUI;
import net.mdwright.var.application.Main;

public class ViewController {

  private EntranceGUI view;

  public ViewController(EntranceGUI view) {
    this.view = view;

    view.addModelBuildingObserver(this::changeToModelBuilding);
    view.addHistoricalSimObserver(this::changeToHistoricalSim);
  }

  public void changeToModelBuilding() {
    try {
      Main.changeScene("/fxml/ModelBuildingGUI.fxml");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void changeToHistoricalSim() {
    try {
      Main.changeScene("/fxml/HistoricalSimGUI.fxml");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
