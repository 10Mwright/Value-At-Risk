package net.mdwright.var.application;

import java.math.BigDecimal;
import java.util.function.Consumer;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Position;

/**
 * Interface class for GUI Views. Influence taken from 2nd Year Software Engineering Calculator
 * Coursework.
 *
 * @author Matthew Wright
 */
public interface ViewInterface {

  /**
   * Method to retrieve the portfolio as an array of Position objects.
   *
   * @return An array of type Position
   */
  Position[] getPortfolio();

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
  double getProbability();

  /**
   * Method to set the result in GUI.
   *
   * @param varValue A BigDecimal object representing the value at risk given the user's criteria
   */
  void setResult(BigDecimal varValue);

  /**
   * Adds an observer to monitor user commands to calculate Var.
   *
   * @param obs Object of type Observer
   */
  void addCalcObserver(Observer obs);

  /**
   * Adds an observer to monitor additions to the portfolio
   *
   * @param obs Object of type Observer
   */
  void addPortfolioObserver(Observer obs);

  /**
   * Adds an observer to monitor user's change in model selection.
   *
   * @param model Object of type Observer
   */
  void addModelObserver(Consumer<Model> model);

}
