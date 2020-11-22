package net.mdwright.var;

import java.math.BigDecimal;
import net.mdwright.var.objects.Position;

public interface VarCalculator {

  /**
   * Method to calculate VaR for a portfolio using the model-building model.
   *
   * @param portfolio An array of Positions containing ticker symbols and values for each position
   * in the portfolio
   * @param timeHorizon number of days as an integer to act as the time horizon
   * @param probability A double value representing the percentage probability in decimal form
   * @return BigDecimal value representing the VaR of the single stock portfolio
   */
  BigDecimal calculateVar(Position[] portfolio, int timeHorizon, double probability);

  /**
   * Method to calculate VaR for a portfolio using the historical sim model.
   *
   * @param portfolio An array of Positions containing ticker symbols and values for each position
   * in the portfolio
   */
  BigDecimal calculateVar(Position[] portfolio, int timeHorizon, double probability,
      int historicalDataLength);


}
