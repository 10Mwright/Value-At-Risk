package net.mdwright.var.application;

import java.math.BigDecimal;
import java.util.function.Consumer;
import javafx.scene.chart.LineChart;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Position;

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
  @Deprecated //Method has been deprecated as the GUIs have now been separated on a per model basis.
  void addModelObserver(Consumer<Model> model);

}
