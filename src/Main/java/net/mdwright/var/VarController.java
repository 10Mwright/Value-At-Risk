package net.mdwright.var;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale.Category;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import net.mdwright.var.application.ViewInterface;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Controller class for Var Calculations.
 *
 * Note: influence from Calculator code 2nd Year software Engineering Coursework
 *
 * @author Matthew Wright
 */
public class VarController {

  private boolean isModelBuilding = true;
  private VarModel model = new VarModel();
  private ViewInterface view;

  /**
   * Constructor method to assign a view to this controller and setup observing methods.
   *
   * @param view ViewInterface object representing the current user interface to be controlled
   */
  public VarController(ViewInterface view) {
    this.view = view;
    view.addCalcObserver(this::calculateVar); //Set observer to calculateVar method
    view.addPortfolioObserver(this::addAsset); //Set observer for add button to addAsset method
    view.addModelObserver(this::modelToUse); //Set observer for model selection to modelToUse method
  }

  /**
   * Observing method to call appropriate calculation method depending on user selected model.
   */
  public void calculateVar() {
    Portfolio portfolio = new Portfolio(view.getPortfolio());
    int timeHorizon = view.getTimeHorizon();
    double probability = view.getProbability();

    BigDecimal valueAtRisk;

    if(view.getDataLength() != 0) { //Datalength has been set
      isModelBuilding = false;
    }

    if (!isModelBuilding) {
      int dataLength = view.getDataLength();

      valueAtRisk = model.calculateVar(portfolio, timeHorizon, probability, dataLength);
    } else {
      valueAtRisk = model.calculateVar(portfolio, timeHorizon, probability);
    }

    //Rounding result to 2 decimal places
    valueAtRisk = valueAtRisk.setScale(2, RoundingMode.UP);

    view.setResult(valueAtRisk); //Set result in GUI
    drawChart(); //Calls code to create a price chart
  }

  public void drawChart() {
    Portfolio portfolioData = model.getPortfolioData();
    List<HistoricalQuote>[] portfolioPrice;

    System.out.println(portfolioData);

    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Date");
    yAxis.setLabel("Portfolio Value (£)");

    final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

    XYChart.Series series = new Series();
    series.setName("Portfolio");

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");

    for(int j = 0; j < portfolioData.getPosition(0).getHistoricalDataSize(); j = j + 5) { //Runs through the portfolio's positions ready for plotting
      BigDecimal totalForDay = new BigDecimal(0);

      for (int i = 0; i < portfolioData.getSize(); i++) { //Plots every 5th data point on graph,
        Position position = portfolioData.getPosition(i);

        totalForDay = totalForDay.add(position.getHistoricalData().get(j).getAdjClose());
      }

      Position firstPosition = portfolioData.getPosition(0); //First position in the portfolio, used to gather dates for each day

      series.getData().add(new XYChart.Data(sdf.format(firstPosition.getHistoricalData().get(j).getDate().getTime()), totalForDay));
    }

    lineChart.getData().add(series);

    view.setChart(lineChart);
  }

  /**
   * Observing method to add a new position to the list view on the GUI.
   */
  public void addAsset() {
    Position newPos = view.getNewPosition();

    //Set in view
    view.addNewPosition(newPos);
  }

  /**
   * Observing method to switch between calculation models.
   *
   * @param modelToUse Model object containing the enum value to use
   */
  public void modelToUse(Model modelToUse) {
    if (modelToUse == Model.ModelBuilding) {
      isModelBuilding = true;
    } else {
      isModelBuilding = false;
    }
  }

}
