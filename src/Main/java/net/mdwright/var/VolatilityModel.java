package net.mdwright.var;

import net.mdwright.var.objects.Portfolio;

/**
 * Interface for volatility models.
 * @author Matthew Wright
 */
public interface VolatilityModel {

  /**
   * Method to calculate volatility using one of several methods.
   * @param portfolio Portfolio object containing historical data for each Position
   * @return double value representing the volatility in percentage decimal format
   */
  double calculateVolatility(Portfolio portfolio, int positionIndex);

}
