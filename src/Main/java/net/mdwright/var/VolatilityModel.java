package net.mdwright.var;

import net.mdwright.var.objects.Portfolio;

/**
 * Interface for volatility models.
 * @author Matthew Wright
 */
public abstract class VolatilityModel {

  /**
   * Method to calculate volatility using one of several methods.
   * @param portfolio Portfolio object containing historical data for each Position
   * @return double value representing the volatility in percentage decimal format
   */
  abstract double calculateVariance(Portfolio portfolio, int positionIndex);

  public double calculateVolatility(Portfolio portfolio, int positionIndex) {
    double variance = calculateVariance(portfolio, positionIndex);

    double volatility = Math.sqrt(variance);

    portfolio.getPosition(positionIndex).setVolatility(volatility);
    return volatility;
  }

}
