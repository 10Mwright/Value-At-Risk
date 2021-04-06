package net.mdwright.var;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
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
import net.mdwright.var.objects.VolatilityMethod;

/**
 * Controller class for Var Calculations.*
 * Note: influence from Calculator code 2nd Year software Engineering Coursework
 *
 * @author Matthew Wright
 */
public class VarController {

  private final int plotResolution = 5; //How many days between each chart point

  private final DecimalFormat numberFormat = new DecimalFormat("#,###.00");

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
    int probability = 0;
    int dataLength = 0; //Only applicable for historical sim.
    double lambda = 0; //Only applicable for model-building

    if (getPortfolio().getSize() != 0) { //Incoming portfolio isn't empty
      portfolio = getPortfolio();
    } else {
      isFailure = true;
      sendAlert("Invalid Portfolio",
          "Please enter some valid positions in the portfolio", AlertType.ERROR);
    }

    /*
    Below blocks of code gets each respective element from the GUI
    Each block ensures that the respective field isn't empty and that the values in the
    field are of the correct data type.
     */

    //Get time horizon
    if (view.getTimeHorizon() != null) {
      try {
        timeHorizon = Integer.parseInt(view.getTimeHorizon());
      } catch (NumberFormatException e) {
        isFailure = true;
        sendAlert("Invalid Time Horizon",
            "The value entered in the time horizon field is not a valid integer!",
            AlertType.ERROR);
      }
    } else {
      isFailure = true;
      sendAlert("Empty Time Horizon Field",
          "Please enter a valid Integer in the time horizon field!", AlertType.ERROR);
    }

    //Get probability
    if (view.getProbability() != null) { //Probability is set, not 0
      try {
        probability = Integer.parseInt(view.getProbability());
      } catch (NumberFormatException e) {
        isFailure = true;
        sendAlert("Invalid Probability", "Please enter a valid Integer in the "
            + "probability field! (e.g. 95 or 99, not 0.99)", AlertType.ERROR);
      }
    } else {
      isFailure = true;
      sendAlert("Blank Probability",
          "Please enter a valid Integer value for the probability!", AlertType.ERROR);
    }

    //Get data length (Only ran when the request originates from the historical sim. gui
    if (view.getModelToUse() == Model.HISTORICAL_SIMULATION) {
      if (view.getDataLength() != null) {
        try {
          dataLength = Integer.parseInt(view.getDataLength());
        } catch (NumberFormatException e) {
          isFailure = true;
          sendAlert("Invalid Data Length", "Please enter a valid Integer in "
              + "the data length field! (e.g. 252 or 512 days)", AlertType.ERROR);
        }
      } else {
        isFailure = true;
        sendAlert("Blank Data Length",
            "Please enter a valid number of days in the data length field!",
            AlertType.ERROR);
      }
    } else if (view.getModelToUse() == Model.MODEL_BUILDING
        && view.getVolatilityChoice() == VolatilityMethod.EWMA) {
      if (view.getLambda() != null) { //Get lambda if model-building request
        try {
          lambda = Double.parseDouble(view.getLambda());
          portfolio.setVolatilityLambda(lambda); //Transfer to portfolio object.
        } catch (NumberFormatException e) {
          isFailure = true;
          sendAlert("Invalid Lambda Value",
              "Please enter a valid value for lambda (e.g. 0.94)", AlertType.ERROR);
        }
      } else {
        isFailure = true;
        sendAlert("Blank Lambda Field",
            "Please enter a valid value for lambda (e.g. 0.94)", AlertType.ERROR);
      }
    }

    BigDecimal valueAtRisk;

    if (!isFailure) { //Nothing above has failed
      if (view.getModelToUse() == Model.HISTORICAL_SIMULATION) {
        //If the request originates from the historical sim GUI

        valueAtRisk = model
            .calculateVar(portfolio, timeHorizon, probability, dataLength);

      } else {

        valueAtRisk = model
            .calculateVar(portfolio, timeHorizon, probability, view.getVolatilityChoice());

      }

      view.setResult(numberFormat.format(valueAtRisk)); //Set result in GUI
      drawChart(); //Calls code to create a price chart
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

    //Set up some options for chart
    lineChart.setLegendVisible(false);
    lineChart.setPrefSize(670, 530);

    //Pass new chart over to the GUI
    view.setChart(lineChart);

    //Fill in values below graph pane
    view.setPortfolioValue(numberFormat.format(portfolioData.getCurrentValue()));

    //current value of portfolio - value at risk
    BigDecimal valueAfterVar = portfolioData.getCurrentValue()
        .subtract(portfolioData.getValueAtRisk());
    view.setValueAfterVar(numberFormat.format(valueAfterVar));

    //VaR as a percentage of portfolio value
    BigDecimal percentage = portfolioData.getValueAtRisk().divide(portfolioData.getCurrentValue(),
        RoundingMode.UP);
    percentage = percentage.multiply(new BigDecimal(100));

    view.setVarPercentage(numberFormat.format(percentage.doubleValue()));
  }

  /**
   * Observing method to add a new position to the list view on the GUI.
   */
  public void addAsset() {
    Position newPos = getNewPosition();

    boolean isExists = false; //Defaults to not existing in current portfolio

    try {
      if (!isFailure) {
        if (DataManager.testStockIsValid(newPos.getTickerSymbol())) { //Check ticker is valid
          if (getPortfolio().getSize() > 0) { //Currently a standing portfolio
            Portfolio portfolio = getPortfolio();

            //Check portfolio doesn't contain this position already
            for (int i = 0; i < portfolio.getSize(); i++) {
              if (portfolio.getPosition(i).getTickerSymbol().equals(newPos.getTickerSymbol())) {
                isExists = true; //set boolean to true to alter code below
                break; //Break out of loop, not need to continue
              }
            }
          }

          if (!isExists) { //Doesn't exist, just add it
            //Set new position in view
            view.addNewPosition(newPos);
          } else {
            sendAlert("Position Already Exists!",
                "This position already exists in your portfolio!", AlertType.ERROR);
          }
        } else { //Ticker symbol isn't valid
          sendAlert("Ticker Symbol Invalid!", "The provided ticker symbol "
              + newPos.getTickerSymbol() + " isn't a valid ticker symbol!", AlertType.ERROR);
        }

        isFailure = false; //Reset failure boolean
      }

      isFailure = false; //Reset failure boolean
    } catch (IOException e) {
      e.printStackTrace();
    }
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

  /**
   * Method to retrieve the portfolio from the view and format it into a portfolio object.
   * @return Portfolio object containing the portfolio object
   */
  public Portfolio getPortfolio() {
    ObservableList<Position> portfolioList = view.getPortfolio();

    int portfolioSize = portfolioList.size(); //Size of position's list on GUI
    Position[] positions = new Position[portfolioSize];

    for (int i = 0; i < portfolioSize; i++) {
      positions[i] = portfolioList.get(i);
    }

    return new Portfolio(positions);
  }

  /**
   * Method to retrieve the new position from the view fields and format it into a position object.
   * @return Position object built using user entered values
   */
  public Position getNewPosition() {
    if (view.getNewPosition() != null) {
      String[] positionValues = view.getNewPosition();

      try {
        int holdingsValue = Integer.parseInt(positionValues[1]);

        Position newPosition = new Position(positionValues[0], holdingsValue);

        view.emptyPositionFields(); //Clear the fields on GUI

        return newPosition;
      } catch (NumberFormatException e) {
        isFailure = true;
        sendAlert("Invalid Holdings Field",
            "Please enter a valid numerical value in the holdings field!",
            AlertType.ERROR);

        return null;
      }
    } else {
      isFailure = true;
      sendAlert("Invalid Positions Field!",
          "Please enter a valid ticker symbol & holdings value in the relevant fields!",
          AlertType.ERROR);
      return null;
    }
  }

}
