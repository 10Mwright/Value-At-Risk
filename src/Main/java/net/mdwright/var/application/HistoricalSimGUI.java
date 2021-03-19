package net.mdwright.var.application;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import net.mdwright.var.objects.VolatilityMethod;

/**
 * Class for managing GUI for user interactions for historical simulations.
 * @author Matthew Wright
 */
public class HistoricalSimGUI implements ViewInterface {

  private final DecimalFormat numberFormat = new DecimalFormat("#,###.00");
  private final int positionFields = 2; //Number of fields that consitutes a new position

  /**
   * {@inheritDoc}
   */
  @Override
  public void setupVolatilityChoice() { //Dummy method, isn't required on this interface
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VolatilityMethod getVolatilityChoice() { //Dummy method, isn't required on this interface
    return null;
  } //Dummy method, not required

  /**
   * {@inheritDoc}
   */
  @Override
  public ObservableList getPortfolio() {
    return portfolioList.getItems();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String[] getNewPosition() {
    String[] positionValues = new String[positionFields];

    if (tickerSymbolField.getText().equals("") || assetHoldingsField.getText().equals("")) {
      return null;
    } else {
      positionValues[0] = tickerSymbolField.getText();
      positionValues[1] = assetHoldingsField.getText();

      return positionValues;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void emptyPositionFields() {
    tickerSymbolField.clear();
    assetHoldingsField.clear();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTimeHorizon() {
    if (!timeHorizonField.getText().equals("")) {
      return timeHorizonField.getText();
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getProbability() {
    if (!probabilityField.getText().equals("")) {
      return probabilityField.getText();
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDataLength() {
    if (!dataLengthField.getText().equals("")) {
      return dataLengthField.getText();
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setResult(BigDecimal varValue) {
    resultField.setText(numberFormat.format(varValue));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setChart(LineChart chart) {
    graphPane.getChildren().clear();

    graphPane.getChildren().add(chart);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addNewPosition(Position newPos) {
    portfolioList.getItems().add(newPos);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPortfolioValue(BigDecimal portfolioValue) {
    this.portfolioValue.setText("£" + numberFormat.format(portfolioValue));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValueAfterVar(BigDecimal valueAfterVar) {
    this.valueAfterVar.setText("£" + numberFormat.format(valueAfterVar));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVarPercentage(double percentage) {
    this.varPercentage.setText(numberFormat.format(percentage) + "%");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addCalcObserver(Observer obs) {
    calculateButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        obs.tell();
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addPortfolioObserver(Observer obs) {
    addAssetButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        obs.tell();
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Model getModelToUse() {
    return Model.HISTORICAL_SIMULATION;
  }

  //FXML Elements below

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
  // fx:id="assetHoldingsField"
  private TextField assetHoldingsField;

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

  @FXML
  // fx:id="graphPane"
  private Pane graphPane;

  @FXML
  // fx:id="portfolioValue
  private Label portfolioValue;

  @FXML
  // fx:id="valueAfterVar"
  private Label valueAfterVar;

  @FXML
  // fx:id="varPercentage"
  private Label varPercentage;
}
