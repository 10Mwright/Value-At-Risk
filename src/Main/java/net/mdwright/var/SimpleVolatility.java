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

    BigDecimal meanStockPrice = VarMath.calculateMean(portfolio.getPosition(positionIndex));

    BigDecimal[][] deviations = new BigDecimal[2][historicalData.size()];
    BigDecimal sumOfSquaredDeviations = new BigDecimal(0.0);

    for (int j = 0; j < historicalData.size(); j++) {
      deviations[0][j] = meanStockPrice.subtract(historicalData.get(j).getAdjClose());
      deviations[1][j] = deviations[0][j].multiply(deviations[0][j]);

      sumOfSquaredDeviations = sumOfSquaredDeviations.add(deviations[1][j]);
    }

    BigDecimal stockPriceVariance = sumOfSquaredDeviations
        .divide(new BigDecimal(historicalData.size()), divisionScale, RoundingMode.UP);

    double dailyVolatility = Math.sqrt(stockPriceVariance.doubleValue());

    dailyVolatility = dailyVolatility / (meanStockPrice.doubleValue());

    System.out.println("Mean: " + meanStockPrice);
    System.out.println("Sum of Squared Deviations: " + sumOfSquaredDeviations);
    System.out.println("Stock price Variance: " + stockPriceVariance);
    System.out.println("Daily Volatility: " + dailyVolatility);

    portfolio.getPosition(positionIndex).setVolatility(dailyVolatility);
    return dailyVolatility;
  }
}
