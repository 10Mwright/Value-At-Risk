package net.mdwright.var.application;

import java.math.BigDecimal;
import java.util.function.Consumer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Position;

/**
 * Class for managing GUI for user interactions for historical simulations.
 * @author Matthew Wright
 */
public class HistoricalSimGUI implements ViewInterface {

  @Override
  public Position[] getPortfolio() {
    int portfolioSize = portfolioList.getItems().size();
    Position[] positions = new Position[portfolioSize];

    for (int i = 0; i < portfolioSize; i++) {
      positions[i] = portfolioList.getItems().get(i);
    }

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
    return (Double.parseDouble(probabilityField.getText()) / 100);
  }

  @Override
  public int getDataLength() {
    return Integer.parseInt(dataLengthField.getText());
  }

  @Override
  public void setResult(BigDecimal varValue) {
    resultField.setText(String.valueOf(varValue));
  }

  @Override
  public void addNewPosition(Position newPos) {
    portfolioList.getItems().add(newPos);
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
  // fx:id="dataLengthField"
  private TextField dataLengthField;

  @FXML
  // fx:id="resultField"
  private TextField resultField;

  @FXML
  // fx:id="calculateButton"
  private Button calculateButton;

  @FXML
  // fx:id="addAssetButton"
  private Button addAssetButton;

  @FXML
  // fx:id="portfolioList"
  private ListView<Position> portfolioList;
}
