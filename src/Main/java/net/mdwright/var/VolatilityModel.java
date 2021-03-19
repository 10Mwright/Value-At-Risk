package net.mdwright.var;

import net.mdwright.var.objects.Portfolio;

/**
 * Abstract class for volatility models.
 *
 * @author Matthew Wright
 *     Note influence for this design was taken from Adrian Ng, see link below
 *         https://github.com/Adrian-Ng/ValueAtRisk/blob/master/src/main/java/com/adrian
 *             /ng/VolatilityAbstract.java
 */
public abstract class VolatilityModel {

  /**
   * Method to calculate variance using the EWMA or EW approaches.
   * @param portfolio Portfolio object containing historical data for each Position
   * @param positionIndex int value representing the index pointer for the relevant position
   * @return double value representing the variance
   */
  abstract double calculateVariance(Portfolio portfolio, int positionIndex);

  /**
   * Method to calculate volatility using the relevant variance method.
   * @param portfolio Portfolio object containing historical data for each Position
   * @param positionIndex int value representing the index pointer for the relevant position
   * @return double value representing the volatility in percentage decimal format
   */
  public double calculateVolatility(Portfolio portfolio, int positionIndex) {
    double variance = calculateVariance(portfolio, positionIndex);

    double volatility = Math.sqrt(variance);

    portfolio.getPosition(positionIndex).setVolatility(volatility);
    return volatility;
  }

  /**
   * Method to create the covariance matrix for a portfolio.
   * @param portfolio Portfolio object containing position objects
   * @return A matrix of doubles NxN where N is the number of positions in the portfolio
   */
  public double[][] calculateCovarianceMatrix(Portfolio portfolio) {
    double[][] covariance = new double[portfolio.getSize()][portfolio.getSize()];

    for (int i = 0; i < portfolio.getSize(); i++) {
      for (int j = 0; j < portfolio.getSize(); j++) { //Run through the matrix
        double volatilityI = calculateVolatility(portfolio, i);
        double volatilityJ = calculateVolatility(portfolio, j);
        double correlation = VarMath.calculateCoefficient(portfolio.getPosition(i),
            portfolio.getPosition(j));

        covariance[i][j] = volatilityI * volatilityJ * correlation; //Covariance between i & j
      }
    }

    return covariance;
  }

}
