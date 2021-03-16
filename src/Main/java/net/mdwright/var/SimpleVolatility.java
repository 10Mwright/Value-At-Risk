package net.mdwright.var;

import java.util.List;
import net.mdwright.var.objects.Portfolio;
import yahoofinance.histquotes.HistoricalQuote;

public class SimpleVolatility extends VolatilityModel {

  /**
   * Method for calculating variance using the equally weighted model.
   *
   * @param portfolio Portfolio object containing all the positions and their historical data
   * @return A double value representing the daily volatility of the stock influence from website:
   * https://www.wallstreetmojo.com/volatility-formula/
   */
  public double calculateVariance(Portfolio portfolio, int positionIndex) {
    List<HistoricalQuote> historicalData = portfolio.getPosition(positionIndex).getHistoricalData();
    double[] percentageChange = VarMath.getPercentageChanges(portfolio, 0);

    double variance = 0;

    for(int j = 0; j < percentageChange.length; j++) {
      variance += Math.pow(percentageChange[j], 2);
    }

    variance = variance / historicalData.size(); //Take average of % changes

    System.out.println("Variance: " + variance);

    return variance;
  }
}
