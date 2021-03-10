package net.mdwright.var;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import yahoofinance.histquotes.HistoricalQuote;

public class Volatility {

  private static final int divisionScale = 2;

  /**
   * Method for calculating the daily volatility of a stock based on it's historical data.
   *
   * @param position Position object of the position to calculate volatility for
   * @return A double value representing the daily volatility of the stock influence from website:
   * https://www.wallstreetmojo.com/volatility-formula/
   */
  public static double calculateVolatility(Position position) {
    List<HistoricalQuote> historicalData = position.getHistoricalData();

    BigDecimal meanStockPrice = calculateMean(position);

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

    position.setVolatility(dailyVolatility);
    return dailyVolatility;
  }

  public static BigDecimal calculateMean(Position position) {
    BigDecimal meanStockPrice = new BigDecimal("0.0");

    // Increment through the data and sum them all up
    for (int i = 0; i < position.getHistoricalDataSize(); i++) {
      meanStockPrice = meanStockPrice.add(position.getHistoricalData().get(i).getAdjClose());
    }

    meanStockPrice = meanStockPrice
        .divide(new BigDecimal(position.getHistoricalDataSize()), 2, RoundingMode.UP);

    return meanStockPrice;
  }

  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability,
      int historicalDataLength) {
    throw new UnsupportedOperationException(
        "Invalid operation for model-building VaR (No such thing as Historical Data)");
  }
}
