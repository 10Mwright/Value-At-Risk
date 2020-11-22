package net.mdwright.var.application;

import java.math.BigDecimal;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Position;

public class ModelBuildingGUI implements ViewInterface {

  @Override
  public Position[] getPortfolio() {
    Position pos = new Position(tickerSymbolField.getText(), Double.parseDouble(assetValueField.getText()));

    Position[] positions = {pos};

    return positions;
  }

  @Override
  public Position getNewPosition() {
    return new Position(tickerSymbolField.getText(), Double.parseDouble(assetValueField.getText()));
  }

  @Override
  public int getTimeHorizon() {
    return Integer.parseInt(timeHorizonField.getText());
  }

  @Override
  public double getProbability() {
    return Double.parseDouble(probabilityField.getText());
  }

  @Override
  public void setResult(BigDecimal varValue) {
    resultField.setText(String.valueOf(varValue));
  }

  @Override
  public void addCalcObserver(Observer obs) {
    calculateButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        obs.tell();
      }
    });
  }

  @Override
  public void addPortfolioObserver(Observer obs) {
    addAssetButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        obs.tell();
      }
    });
  }

  @Override
  public void addModelObserver(Consumer<Model> model) {

  }

  @FXML
  // fx:id="timeHorizonField"
  private TextField timeHorizonField;

  @FXML
  // fx:id="probabilityField"
  private TextField probabilityField;

  @FXML
  // fx:id="tickerSymbolField"
  private TextField tickerSymbolField;

  @FXML
  // fx:id="assetValueField"
  private TextField assetValueField;

  @FXML
  // fx:id="resultField"
  private TextField resultField;

  @FXML
  // fx:id="calculateButton"
  private Button calculateButton;

  @FXML
  // fx:id="addAssetButton"
  private Button addAssetButton;
}
