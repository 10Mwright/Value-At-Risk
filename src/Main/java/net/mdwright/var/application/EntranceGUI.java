package net.mdwright.var.application;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class EntranceGUI {

  public void addModelBuildingObserver(Observer obs) {
    modelBuildingButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
          obs.tell();
      }
    });
  }

  public void addHistoricalSimObserver(Observer obs) {
    historicalSimButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        obs.tell();
      }
    });
  }

  @FXML
  // fx:id="modelBuildingButton"
  private Button modelBuildingButton;

  @FXML
  // fx:id="historicalSimButton"
  private Button historicalSimButton;
}
