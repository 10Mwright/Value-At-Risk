import Objects.Normals;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import yahoofinance.histquotes.HistoricalQuote;

public class CalculationManager {

  /**
   * Method to calculate single day, 99% value at risk.
   * @param tickerSymbol String representation of the stock's ticker symbol
   * @param assetValue Double value representing total value in tickerSymbol
   * @return a double value representing the 99% single day VaR
   */
  public static BigDecimal calculateVar(String tickerSymbol, double assetValue, int timeHorizon, double probability){
    double normSinV = Normals.getNormSinV(probability); //Retrieves appropriate NormSinV value for probability
    BigDecimal singleDayVar = new BigDecimal(0.0);
    BigDecimal multiDayVar = new BigDecimal(0.0);

    DataManager data = new DataManager();
    try {
      List<HistoricalQuote> historicalData = data.getHistoricalPrices(tickerSymbol);

      for (int i = 0; i < historicalData.size(); i++ ) {
        // Format: [<symbol>@<YYYY-MM-dd>: low-high, open-close (adjusted close)]
        System.out.println(i + ", " + historicalData.get(i).getAdjClose());
      }

      double dailyVolatility = calculateVolatility(historicalData);

      // Must convert this volatility to a percentage
      // Note: any better method to calculate as a percentage other than avg?
      dailyVolatility = dailyVolatility;

      System.out.println("Daily Volatility: " + dailyVolatility);

      double dailyStandardDeviation = assetValue * (dailyVolatility);

      System.out.println("Daily Standard Deviation: " + dailyStandardDeviation);

      singleDayVar = BigDecimal.valueOf(normSinV).multiply(BigDecimal.valueOf(dailyStandardDeviation));

      multiDayVar = singleDayVar.multiply(new BigDecimal(Math.sqrt(timeHorizon)));

    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Single Day " + (probability*100) + "% VaR is: " + singleDayVar);
    System.out.println(timeHorizon + " Day " + (probability*100) + "VaR is: " + multiDayVar);
    return multiDayVar;
  }

  /**
   * Method for calculating the daily volatility of a stock based on it's historical data.
   * @param historicalData List of type HistoricalQuote
   * @return A double value representing the daily volatility of the stock
   */
  public static double calculateVolatility(List<HistoricalQuote> historicalData) {

    BigDecimal meanStockPrice = new BigDecimal("0.0");

    // Increment through the data and sum them all up
    for(int i = 0; i < historicalData.size(); i++) {
      meanStockPrice = meanStockPrice.add(historicalData.get(i).getAdjClose());
    }

    meanStockPrice = meanStockPrice.divide(new BigDecimal(historicalData.size()), 2, RoundingMode.UP);

    BigDecimal[][] deviations = new BigDecimal[2][historicalData.size()];
    BigDecimal sumOfSquaredDeviations = new BigDecimal(0.0);

    for(int j = 0; j < historicalData.size(); j++) {
      deviations[0][j] = meanStockPrice.subtract(historicalData.get(j).getAdjClose());
      deviations[1][j] = deviations[0][j].multiply(deviations[0][j]);

      sumOfSquaredDeviations = sumOfSquaredDeviations.add(deviations[1][j]);
    }

    BigDecimal stockPriceVariance = sumOfSquaredDeviations.divide(new BigDecimal(historicalData.size()), 2, RoundingMode.UP);

    double dailyVolatility = Math.sqrt(stockPriceVariance.doubleValue());

    System.out.println("Mean: " + meanStockPrice);
    System.out.println("Sum of Squared Deviations: " + sumOfSquaredDeviations);
    System.out.println("Stock price Variance: " + stockPriceVariance);
    System.out.println("Daily Volatility (RAW VALUE): " + dailyVolatility);

    dailyVolatility = dailyVolatility / (meanStockPrice.doubleValue());

    return dailyVolatility;
  }

}
