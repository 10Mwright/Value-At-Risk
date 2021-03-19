package net.mdwright.var;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
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

  private final int plotResolution = 5; //How many days between each chart point

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
   * Method for drawing a linechart using historical data from the VarModel.
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
      The number of dates plotted is dependant on the smallest number of prices gathered, for
      example if a position only has 200 prices while all other positions have 500 then the chart
      will only be plotted with 200 prices.
     */

    int smallestSetSize = VarMath.getSmallestDatasetSize(portfolioData);
    List<Map<String, BigDecimal>> portfolioPriceMap = VarMath.getHashMaps(portfolioData);

    int dataLength = portfolioData.getPosition(0).getHistoricalDataSize();
    Calendar currentDate = portfolioData.getPosition(0).getHistoricalData()
        .get(dataLength - 1).getDate(); //Using first position to get a starting date

    //Move the date back by the smallest number of prices a position has in the portfolio
    currentDate.add(Calendar.DAY_OF_YEAR, -smallestSetSize);

    for (int i = 0; i < smallestSetSize; i++) {
      BigDecimal totalPriceForDay = new BigDecimal(0);
      String targetDateString = sdf.format(currentDate.getTime());
      Boolean isPlottable = true; //Used to plot only valid data

      for (int j = 0; j < portfolioData.getSize(); j++) { //For each position
        Map<String, BigDecimal> positionPriceMap = portfolioPriceMap.get(j);

        if (positionPriceMap.get(targetDateString) != null) { //Ensure there is data here
          BigDecimal positionValueOnDay = positionPriceMap.get(targetDateString);
          positionValueOnDay = positionValueOnDay.multiply(
              new BigDecimal(portfolioData.getPosition(j).getHoldings())); //Total value of pos.
          totalPriceForDay = totalPriceForDay.add(positionValueOnDay); //Add to the current day
        } else {
          isPlottable = false; //There is missing data, we don't want to plot this
        }
      }

      if (isPlottable) { //Only plot if all positions had data for this day
        series.getData().add(new XYChart.Data(targetDateString, totalPriceForDay));
      }

      currentDate.add(Calendar.DAY_OF_YEAR, plotResolution); //Increment the date by plotResolution
    }

    //We add the most recent valuation using the today's prices
    Calendar todaysDate = Calendar.getInstance();
    String todaysDateString = sdf.format(todaysDate.getTime());

    series.getData().add(new XYChart.Data(todaysDateString, portfolioData.getCurrentValue()));

    lineChart.getData().add(series); //Construct line chart ready to be passed to the GUI class

    //Pass new chart over to the GUI
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

    boolean isExists = false; //Defaults to not existing in current portfolio

    if (!isFailure) {
      if(view.getPortfolio().getSize() > 0) { //Currently a standing portfolio
        Portfolio portfolio = view.getPortfolio();

        //Check portfolio doesn't contain this position already
        for (int i = 0; i < portfolio.getSize(); i++) {
          if(portfolio.getPosition(i).getTickerSymbol().equals(newPos.getTickerSymbol())) {
            isExists = true; //set boolean to true to alter code below
            break; //Break out of loop, not need to continue
          }
        }
      }

      if(!isExists) { //Doesn't exist, just add it
        //Set new position in view
        view.addNewPosition(newPos);
      } else {
        sendAlert("Position Already Exists!",
            "This position already exists in your portfolio!", AlertType.ERROR);
      }
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
