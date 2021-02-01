package net.mdwright.var;

import java.math.BigDecimal;
import java.util.List;
import net.mdwright.var.objects.Portfolio;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Interface for different models of VaR calculations.
 *
 * @author Matthew Wright
 */
public interface VarCalculator {

  /**
   * Method to calculate VaR for a portfolio using the model-building model.
   *
   * @param portfolio An array of Positions containing ticker symbols and values for each position
   * in the portfolio
   * @param timeHorizon An int value representing the of days to calculate VaR over
   * @param probability A double value representing the percentage probability in decimal form
   * @return BigDecimal value representing the VaR of the portfolio
   */
  BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability);

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
  BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability,
      int historicalDataLength);


  List<HistoricalQuote>[] getData();
}
