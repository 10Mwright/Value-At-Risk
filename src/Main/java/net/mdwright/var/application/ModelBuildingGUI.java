package net.mdwright.var.application;

import java.math.BigDecimal;
import java.util.Observer;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Position;

public class ModelBuildingGUI implements ViewInterface {

  @Override
  public Position[] getPortfolio() {
    Position pos = new Position(tickerField.getText(), Double.parseDouble(assetValueField.getText()));

    Position[] positions = {pos};

    return positions;
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
  private TextField tickerField;

  @FXML
  //fx:id="assetValueField"
  private TextField assetValueField;

  @FXML
  // fx:id="resultField"
  private TextField resultField;
}
