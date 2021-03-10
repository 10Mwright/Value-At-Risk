package net.mdwright.var;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Class for supporting calculations used in value at risk models.
 * @author Matthew Wright
 */
public class Varmath {

  private static final int divisionScale = 2; //Scale to be used when dividing using big decimals

  /**
   * Simple Method for calculating volatility over a position's time period.
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

  /**
   * Exponentially Weighted Moving Average model for calculating volatility.
   *
   * @param portfolio Portfolio object containing the position objects to be calculated using
   * @param index Int value representing the index of the position within the Portfolios array
   * @param lambda Double value representing the lambda constant to be used in calculation
   * @return double value representing the volatility of the position over the time period
   */
  public static double calculateVolatility(Portfolio portfolio, int index, double lambda) {
    List<HistoricalQuote> historicalData = portfolio.getPosition(index).getHistoricalData();

    double[][] returns = new double[3][historicalData.size()];
    double currentWeight = (1 - lambda); //Initial weight
    System.out.println("INITIAL WEIGHT: " + currentWeight);

    double dailyVolatility = 0;

    for (int i = (historicalData.size() - 2); i >= 0; i--) { //Increment through historical data day by day
      double currentDay = historicalData.get(i).getAdjClose().doubleValue();
      double yesterday = historicalData.get(i+1).getAdjClose().doubleValue();

      double returnCurrentDay = currentDay / yesterday;
      returns[0][i] = Math.log(returnCurrentDay); //The natural log of the return in day i
      returns[1][i] = Math.pow(returns[0][i], 2); //Daily variance (unweighted)

      returns[2][i] = returns[1][i] * currentWeight; //Weighted variance

      dailyVolatility += returns[2][i]; //Sum all weighted values to get a final volatility
      currentWeight = currentWeight * (lambda);
      System.out.println("CURRENT WEIGHT: " + currentWeight);
    }

    dailyVolatility = Math.sqrt(dailyVolatility);

    System.out.println("Daily Volatility (EWMA): " + dailyVolatility);

    portfolio.getPosition(index).setVolatility(dailyVolatility);
    return dailyVolatility; //Final volatility value by sqrt
  }

  /**
   * Method for calculating the mean close price across a historical data set.
   *
   * @param position Position object of the position to calculate the mean across
   * @return BigDecimal value representing the mean across the entire data set
   */
  public static BigDecimal calculateMean(Position position) {
    BigDecimal meanStockPrice = new BigDecimal("0.0");

    // Increment through the data and sum them all up
    for (int i = 0; i < position.getHistoricalDataSize(); i++) {
      meanStockPrice = meanStockPrice.add(position.getHistoricalData().get(i).getAdjClose());
    }

    meanStockPrice = meanStockPrice
        .divide(new BigDecimal(position.getHistoricalDataSize()), divisionScale, RoundingMode.UP);

    position.setMeanPrice(meanStockPrice);
    return meanStockPrice;
  }

  public static double calculateCoefficient(Position positionOne, Position positionTwo) {

  }
}
