package net.mdwright.var;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import net.mdwright.var.objects.Portfolio;
import yahoofinance.histquotes.HistoricalQuote;

public class SimpleVolatility implements VolatilityModel {

  private static final int divisionScale = 2;

  /**
   * Simple Method for calculating volatility over a position's time period.
   *
   * @param portfolio Portfolio object containing all the positions and their historical data
   * @return A double value representing the daily volatility of the stock influence from website:
   * https://www.wallstreetmojo.com/volatility-formula/
   */
  public double calculateVolatility(Portfolio portfolio, int positionIndex) {
    List<HistoricalQuote> historicalData = portfolio.getPosition(positionIndex).getHistoricalData();
    double[] percentageChange = VarMath.getPercentageChanges(portfolio, 0);

    double variance = 0;

    for(int j = 0; j < percentageChange.length; j++) {
      variance += Math.pow(percentageChange[j], 2);
    }

    variance = variance / historicalData.size(); //Take average of % changes

    System.out.println("Variance: " + variance);

    double volatility = Math.sqrt(variance); //Square root to find volatility (daily)

    System.out.println("Volatility: " + volatility);

    return volatility;
  }
}
