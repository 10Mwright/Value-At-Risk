package net.mdwright.var.application;

import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Position;
import net.mdwright.var.objects.VolatilityMethod;

/**
 * Interface class for GUI Views.
 *
 * @author Matthew Wright
 */
public interface ViewInterface {

  /**
   * Method to retrieve the portfolio as an ObservableList of type Position.
   *
   * @return An ObservableList of type Position
   */
  ObservableList getPortfolio();

  /**
   * Method to retrieve the new position's ticker symbol (index 0) and holdings (index 1).
   *
   * @return A String array containing the ticker symbol and holdings value
   */
  String[] getNewPosition();

  /**
   * Method to retrieve the user's preferred time horizon for calculation.
   *
   * @return A String value representing the number of days to calculate Var for
   */
  String getTimeHorizon();

  /**
   * Method to retrieve the user's preffered confidence level/probability for calculation.
   *
   * @return A String value representing the probability in decimal format (e.g. 0.99)
   */
  String getProbability();

  /**
   * Method to retrieve the user's preferred data length for historical sim.
   *
   * @return A String value representing the number of days to gather data for
   */
  String getDataLength();

  /**
   * Method to retrieve the user's preferred lambda value for the EWMA model.
   * @return A String value representing the lambda to be used
   */
  String getLambda();

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

  /**
   * Method to set the result in GUI.
   *
   * @param varValue A String object representing the value at risk given the user's criteria
   */
  void setResult(String varValue);

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
   * Method to clear the ticker symbol and holdings fields.
   */
  void emptyPositionFields();

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
   * @param portfolioValue String value representing the total value of the user's portfolio
   */
  void setPortfolioValue(String portfolioValue);

  /**
   * Method to set the value after var field in GUI.
   * @param valueAfterVar String value representing the value after var is taken
   */
  void setValueAfterVar(String valueAfterVar);

  /**
   * Method to set the var as a percentage field in GUI.
   * @param percentage String value representing the percentage out of 100
   */
  void setVarPercentage(String percentage);

  /**
   * Method to setup the volatility choice on the model-building GUI.
   */
  void setupVolatilityChoice();

}
