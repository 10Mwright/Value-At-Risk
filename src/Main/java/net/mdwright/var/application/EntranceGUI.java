package net.mdwright.var.application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Class monitoring model selection from menu screen.
 * @author Matthew Wright
 */
public class EntranceGUI {

  /**
   * Adds a button observer to the Model-Building button.
   * @param obs Observer object to be told of button presses
   */
  public void addModelBuildingObserver(Observer obs) {
    modelBuildingButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
          obs.tell();
      }
    });
  }

  /**
   * Adds a button observer to the Historical-Simulation button.
   * @param obs Observer object to be told of button presses
   */
  public void addHistoricalSimObserver(Observer obs) {
    historicalSimButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        obs.tell();
      }
    });
  }

  //FXML Elements Below

  @FXML
  // fx:id="modelBuildingButton"
  private Button modelBuildingButton;

  @FXML
  // fx:id="historicalSimButton"
  private Button historicalSimButton;
}
