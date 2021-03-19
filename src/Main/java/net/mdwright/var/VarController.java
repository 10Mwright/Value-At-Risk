package net.mdwright.var;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import net.mdwright.var.application.ViewInterface;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;

/**
 * Controller class for Var Calculations.*
 * Note: influence from Calculator code 2nd Year software Engineering Coursework
 *
 * @author Matthew Wright
 */
public class VarController {

  private boolean isFailure = false;
  private VarModel model = new VarModel();
  private ViewInterface view;

  /**
   * Constructor method to assign a view to this controller and setup observing methods.
   *
   * @param view ViewInterface object representing the current user interface to be controlled
   */
  public VarController(ViewInterface view) {
    this.view = view;
    view.setupVolatilityChoice();
    view.addCalcObserver(this::calculateVar); //Set observer to calculateVar method
    view.addPortfolioObserver(this::addAsset); //Set observer for add button to addAsset method
  }

  /**
   * Observing method to call appropriate calculation method depending on user selected model.
   */
  public void calculateVar() {
    Portfolio portfolio = null;
    int timeHorizon = 0;
    double probability = 0;

    if (view.getPortfolio().getSize() != 0) { //Incoming portfolio isn't empty
      portfolio = view.getPortfolio();
    } else {
      isFailure = true;
      sendAlert("Invalid Portfolio",
          "Please enter some valid positions in the portfolio", AlertType.ERROR);
    }

    if (view.getTimeHorizon() != 0) { //Time horizon is set, not 0
      timeHorizon = view.getTimeHorizon();
    } else {
      isFailure = true;
      sendAlert("Invalid Time Horizon",
          "Please enter a valid Integer in the time horizon field!", AlertType.ERROR);
    }

    if (view.getProbability() != 0) { //Probability is set, not 0
      probability = view.getProbability(); //Convert to double percentage
    } else {
      isFailure = true;
      sendAlert("Invalid Probability",
          "Please enter a valid Integer value for the probability!", AlertType.ERROR);
    }

    BigDecimal valueAtRisk = new BigDecimal(0); //Defaults to a value of 0

    if (!isFailure) { //Nothing above has failed
      if (view.getModelToUse()
          == Model.HISTORICAL_SIMULATION) { //If the request originates from the historical sim GUI
        if (view.getDataLength() == 0) {
          sendAlert("Invalid Integer in Data Length Field",
              "Please enter a valid Integer in the data length field!", AlertType.ERROR);
        } else {
          valueAtRisk = model
              .calculateVar(portfolio, timeHorizon, probability, view.getDataLength());
        }
      } else {
        valueAtRisk = model
            .calculateVar(portfolio, timeHorizon, probability, view.getVolatilityChoice());
      }

      if (!isFailure) { //Prevent extra code running if the calculation wasn't successful
        //Rounding result to 2 decimal places
        valueAtRisk = valueAtRisk.setScale(2, RoundingMode.UP);

        view.setResult(valueAtRisk); //Set result in GUI
        drawChart(); //Calls code to create a price chart
      }
    }

    isFailure = false; //Reset failure boolean for next run
  }

  /**
   * Method for drawing a linechart using historical data from the VarModel
   */
  public void drawChart() {
    Portfolio portfolioData = model.getPortfolioData();

    System.out.println(portfolioData);

    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Date");
    yAxis.setLabel("Portfolio Value (£)");

    final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

    XYChart.Series series = new Series();
    series.setName("Portfolio");

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");

    /*
      the first position is used as a benchmark, the number of data points on the graph
      is decided by the historical data gathered for the first position
     */

    int smallestSetSize = VarMath.getSmallestDatasetSize(portfolioData);

    /*
    for (int i = 0; i < smallestSetSize; i++) {
      BigDecimal totalForDay = new BigDecimal(0);

      Calendar currentDate = portfolioData.getPosition(0).getHistoricalData().get(i).getDate();
      String currentDateString = sdf.format(currentDate.getTime());

      for (int j = 0; j < portfolioData.getSize(); j++) {
        Position position = portfolioData.getPosition(j); //Current position object

        for (int k = 0; k < position.getHistoricalDataSize(); k++) { //Run through every day in data
          if(currentDate.equals(position.getHistoricalData().get(k).getDate())) {
            totalForDay = totalForDay.add(position.getHistoricalData().get(i).getAdjClose());
            totalForDay = totalForDay.multiply(new BigDecimal(position.getHoldings()));
            break;
          }
          }
        }

      series.getData().add(new XYChart.Data(currentDateString, totalForDay));
    }
    */

    for (int j = 0; j < portfolioData.getPosition(0)
        .getHistoricalDataSize(); j = j + 5) { //Plots every 5th data point on the graph
      BigDecimal totalForDay = new BigDecimal(0);
      String date = sdf.format(portfolioData.getPosition(0).getHistoricalData()
          .get(j).getDate().getTime()); //Finds date to ensure totals are done using the same dates

      for (int i = 0; i < portfolioData.getSize(); i++) { //Runs through each position
        Position position = portfolioData.getPosition(i);

        //Ensure it's only totalled when the data is from the same date
        if (date.equals(sdf.format(position.getHistoricalData().get(j).getDate().getTime()))) {
          totalForDay = totalForDay.add(position.getHistoricalData().get(j).getAdjClose());
          totalForDay = totalForDay.multiply(new BigDecimal(position.getHoldings())); //Total up
        }
      }

      series.getData().add(new XYChart.Data(date, totalForDay)); //Add data point to data series
    }


    lineChart.getData().add(series); //Construct line chart ready to be passed to the GUI class

    view.setChart(lineChart);

    //Fill in values below graph pane
    view.setPortfolioValue(portfolioData.getCurrentValue());

    //current value of portfolio - value at risk
    view.setValueAfterVar(portfolioData.getCurrentValue().subtract(portfolioData.getValueAtRisk()));

    //VaR as a percentage of portfolio value
    BigDecimal percentage = portfolioData.getValueAtRisk().divide(portfolioData.getCurrentValue(),
        RoundingMode.UP);
    percentage = percentage.multiply(new BigDecimal(100));

    view.setVarPercentage(percentage.doubleValue());
  }

  /**
   * Observing method to add a new position to the list view on the GUI.
   */
  public void addAsset() {
    Position newPos = view.getNewPosition();
    if (newPos == null) {
      sendAlert("Invalid Position Fields",
          "Please enter a valid ticker symbol & holdings amount for a new position!",
          AlertType.ERROR);
      isFailure = true;
    }

    if (!isFailure) {
      //Set in view
      view.addNewPosition(newPos);
    }

    isFailure = false; //Reset failure boolean
  }

  /**
   * Method for sending the user an alert when something goes wrong.
   * @param alertTitle String representing the desired alert title
   * @param alertContent String representing the desired alert content text
   * @param alertType alertType enum representing the desired alert type
   *     Code taken from https://code.makery.ch/blog/javafx-dialogs-official/
   */
  public void sendAlert(String alertTitle, String alertContent, AlertType alertType) {
    Alert alert = new Alert(alertType);
    alert.setTitle(alertTitle);
    alert.setHeaderText(null);
    alert.setContentText(alertContent);

    isFailure = true;

    alert.showAndWait();
  }

}
