package net.mdwright.var;

import java.util.List;
import net.mdwright.var.objects.Portfolio;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Extension of VolatilityModel for calculating variance with the EWMA approach.
 *
 * @author Matthew Wright
 */
public class EWMAVolatility extends VolatilityModel {

  /**
   * Method for calculating variance using the EWMA model.
   *
   * @param portfolio Portfolio object containing the position objects to be calculated using
   * @param positionIndex Int value representing the index of the position within Portfolios array
   * @return double value representing the daily variance of the asset
   */
  public double calculateVariance(Portfolio portfolio, int positionIndex) {
    double lambda = portfolio.getVolatilityLambda(); //Retrieve any user inputted lambda,
    // defaults to 0.94 if no input

    System.out.println("LAMBDA: " + lambda);

    List<HistoricalQuote> historicalData = portfolio.getPosition(positionIndex).getHistoricalData();

    double[][] returns = new double[3][historicalData.size()];
    double currentWeight = (1 - lambda); //Initial weight
    System.out.println("INITIAL WEIGHT: " + currentWeight);

    double variance = 0;

    for (int i = (historicalData.size() - 2); i >= 0; i--) { //Increment through historical data
      double currentDay = historicalData.get(i).getAdjClose().doubleValue();
      double yesterday = historicalData.get(i + 1).getAdjClose().doubleValue();

      double returnCurrentDay = currentDay / yesterday;
      returns[0][i] = Math.log(returnCurrentDay); //The natural log of the return in day i
      returns[1][i] = Math.pow(returns[0][i], 2); //Daily variance (unweighted)

      returns[2][i] = returns[1][i] * currentWeight; //Weighted variance

      variance += returns[2][i]; //Sum all weighted values to get a final volatility
      currentWeight = currentWeight * (lambda);
      System.out.println("CURRENT WEIGHT: " + currentWeight);
    }

    return variance;
  }
}
