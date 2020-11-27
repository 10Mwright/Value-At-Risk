package net.mdwright.var;

import java.math.BigDecimal;
import net.mdwright.var.objects.Portfolio;

/**
 * Class to send calculation requests to appropriate calculating classes.
 *
 * @author Matthew Wright
 */
public class VarModel {

  private VarCalculator modelBuilding = new ModelBuildingVar();
  private VarCalculator historicalSim = new HistoricalSimVar();

  /**
   * Method to calculate VaR for a portfolio using the model-building model.
   *
   * @param portfolio An array of Positions containing ticker symbols and values for each position
   * in the portfolio
   * @param timeHorizon An int value representing the of days to calculate VaR over
   * @param probability A double value representing the percentage probability in decimal form
   * @return BigDecimal value representing the VaR of the portfolio
   */
  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability) {
    return modelBuilding.calculateVar(portfolio, timeHorizon, probability);
  }

  /**
   * Method to calculate VaR for a portfolio using the historical sim model.
   *
   * @param portfolio An array of Positions containing ticker symbols and values for each position
   * in the portfolio
   * @param timeHorizon An int value representing the number of days to calculate VaR over
   * @param probability A double value representing the percentage probability in decimal form
   * @param historicalDataLength An int value representing the number of days to fetch historical
   * data for
   * @return BigDecimal value representing the VaR of the portfolio
   */
  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability,
      int historicalDataLength) {
    return historicalSim.calculateVar(portfolio, timeHorizon, probability, historicalDataLength);
  }

}
