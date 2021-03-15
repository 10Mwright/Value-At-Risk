package net.mdwright.var;

import net.mdwright.var.objects.Position;

/**
 * Interface for volatility models.
 * @author Matthew Wright
 */
public interface VolatilityModel {

  /**
   * Method to calculate volatility using one of several methods.
   * @param position Position object containing historical data
   * @return double value representing the volatility in percentage decimal format
   */
  double calculateVolatility(Position position);

}
