package net.mdwright.var.application;

import java.math.BigDecimal;
import javafx.scene.chart.LineChart;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import net.mdwright.var.objects.VolatilityMethod;

/**
 * Interface class for GUI Views.
 *
 * @author Matthew Wright
 */
public interface ViewInterface {

  /**
   * Method to retrieve the portfolio as an array of Position objects.
   *
   * @return An array of type Position
   */
  Portfolio getPortfolio();

  /**
   * Method to retrieve the position to be added to the portfolio.
   *
   * @return An object of type Position containing user defined data
   */
  Position getNewPosition();

  /**
   * Method to retrieve the user's preferred time horizon for calculation.
   *
   * @return An int value representing the number of days to calculate Var for
   */
  int getTimeHorizon();

  /**
   * Method to retrieve the user's preffered confidence level/probability for calculation.
   *
   * @return A double value representing the probability in decimal format (e.g. 0.99)
   */
  int getProbability();

  /**
   * Method to retrieve the user's preferred data length for historical sim.
   *
   * @return An int value representing the number of days to gather data for
   */
  int getDataLength();

  /**
   * Method to set the result in GUI.
   *
   * @param varValue A BigDecimal object representing the value at risk given the user's criteria
   */
  void setResult(BigDecimal varValue);

  /**
   * Method to set draw the price history chart in the GUI.
   * @param chart A LineChart object representing the completed chart
   */
  void setChart(LineChart chart);

  /**
   * Method to add the newly defined position to the portfolio list.
   *
   * @param newPos A Position object with the user defined parameters
   */
  void addNewPosition(Position newPos);

  /**
   * Adds an observer to monitor user commands to calculate Var.
   *
   * @param obs Object of type Observer
   */
  void addCalcObserver(Observer obs);

  /**
   * Adds an observer to monitor additions to the portfolio.
   *
   * @param obs Object of type Observer
   */
  void addPortfolioObserver(Observer obs);

  /**
   * Method to set the portfolio value field in GUI.
   * @param portfolioValue BigDecimal value representing the total value of the user's portfolio
   */
  void setPortfolioValue(BigDecimal portfolioValue);

  /**
   * Method to set the value after var field in GUI.
   * @param valueAfterVar BigDecimal value representing the value after var is taken
   */
  void setValueAfterVar(BigDecimal valueAfterVar);

  /**
   * Method to set the var as a percentage field in GUI.
   * @param percentage double value representing the percentage out of 100
   */
  void setVarPercentage(double percentage);

  /**
   * Method to setup the volatility choice on the model-building GUI.
   */
  void setupVolatilityChoice();

  /**
   * Method to retrieve the volatility choice on the model-building GUI.
   * @return VolatilityMethod Enum object
   */
  VolatilityMethod getVolatilityChoice();

  /**
   * Method to retrieve the current GUI's Model.
   * @return Model Enum object representing the GUI's respective model
   */
  Model getModelToUse();

}
