package net.mdwright.var.application;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import net.mdwright.var.DataManager;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import net.mdwright.var.objects.VolatilityMethod;

/**
 * Class for managing GUI for user interactions for model-building approach.
 *
 * @author Matthew Wright
 */
public class ModelBuildingGUI implements ViewInterface {

  private DecimalFormat numberFormat = new DecimalFormat("#,###.00"); //Format for numbers

  /**
   * {@inheritDoc}
   */
  @Override
  public void setupVolatilityChoice() {
    volatilityMethod.getItems().removeAll();
    //Fill in choices from available volatility method enums
    volatilityMethod.getItems().addAll(VolatilityMethod.stringValues);
    volatilityMethod.getSelectionModel().select(0);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VolatilityMethod getVolatilityChoice() {
    return VolatilityMethod.fromString(volatilityMethod.getSelectionModel().getSelectedItem());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Portfolio getPortfolio() {
    int portfolioSize = portfolioList.getItems().size(); //Size of position's list on GUI
    Position[] positions = new Position[portfolioSize];

    for (int i = 0; i < portfolioSize; i++) {
      positions[i] = portfolioList.getItems().get(i);
    }

    Portfolio portfolio = new Portfolio(positions);

    if (!lambdaField.getText().equals("")) { //Retrieves lambda value
      System.out.println(Double.parseDouble(lambdaField.getText()));
      portfolio.setVolatilityLambda(Double.parseDouble(lambdaField.getText()));
    }

    return portfolio;
  }

  @Override
  public Position getNewPosition() {
    if (tickerSymbolField.getText().equals("") || assetHoldingsField.getText().equals("")) {
      return null;
    } else {
      Position newPositon = new Position(tickerSymbolField.getText(),
          Double.parseDouble(assetHoldingsField.getText()));

      //Clear fields
      tickerSymbolField.setText("");
      assetHoldingsField.setText("");

      return newPositon;
    }
  }

  @Override
  public int getTimeHorizon() {
    if (!timeHorizonField.getText().equals("")) {
      return Integer.parseInt(timeHorizonField.getText());
    } else {
      return 0;
    }
  }

  @Override
  public int getProbability() {
    if (!probabilityField.getText().equals("")) {
      return (Integer.parseInt(probabilityField.getText()));
    } else {
      return 0;
    }
  }

  @Override
  public int getDataLength() {
    return 0; //Returns 0 always to trigger usage of Model-Building.
  }

  @Override
  public void setResult(BigDecimal varValue) {
    resultField.setText(numberFormat.format(varValue));
  }

  @Override
  public void setChart(LineChart chart) {
    graphPane.getChildren().clear();

    chart.setLegendVisible(false);
    chart.setPrefSize(670, 530);

    graphPane.getChildren().add(chart);
  }

  @Override
  public void addNewPosition(Position newPos) {
    portfolioList.getItems().add(newPos);
  }

  @Override
  public void setPortfolioValue(BigDecimal portfolioValue) {
    this.portfolioValue.setText(numberFormat.format(portfolioValue));
  }

  @Override
  public void setValueAfterVar(BigDecimal valueAfterVar) {
    this.valueAfterVar.setText("£" + numberFormat.format(valueAfterVar));
  }

  @Override
  public void setVarPercentage(double percentage) {
    this.varPercentage.setText(numberFormat.format(percentage) + "%");
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
  public Model getModelToUse() {
    return Model.MODEL_BUILDING;
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
  // fx:id="assetHoldingsField"
  private TextField assetHoldingsField;

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
  // fx:id="portfolioValue"
  private Label portfolioValue;

  @FXML
  // fx:id="valueAfterVar
  private Label valueAfterVar;

  @FXML
  // fx:id="varPercentage"
  private Label varPercentage;

  @FXML
  // fx:id="volatilityMethod"
  private ChoiceBox<String> volatilityMethod;

  @FXML
  // fx:id="lambdaField"
  private TextField lambdaField;

}
