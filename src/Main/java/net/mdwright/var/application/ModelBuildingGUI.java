package net.mdwright.var.application;

import java.math.BigDecimal;
import java.util.function.Consumer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Position;

/**
 * Class for managing GUI for user interactions for historical simulations.
 *
 * @author Matthew Wright
 */
public class ModelBuildingGUI implements ViewInterface {

  //TODO: swap use of Position array to Portfolio instead
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
    return new Position(tickerSymbolField.getText(), Double.parseDouble(assetHoldingsField.getText()));
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
    return 0; //Returns 0 always to trigger usage of Model-Building.
  }

  @Override
  public void setResult(BigDecimal varValue) {
    resultField.setText(String.valueOf(varValue));
  }

  @Override
  public void setChart(LineChart chart) {
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
    this.portfolioValue.setText(portfolioValue.toString());
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

}
