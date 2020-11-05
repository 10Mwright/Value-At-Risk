import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

public class CalculationManager {

  /**
   * Method to calculate single day, 99% value at risk.
   * @param tickerSymbol String representation of the stock's ticker symbol
   * @param assetValue Double value representing total value in tickerSymbol
   * @return a double value representing the 99% single day VaR
   */
  public static BigDecimal calculateVar(String tickerSymbol, double assetValue){
    double normSinV = 2.326; //For now we maintain this value i.e. a 99% VaR
    BigDecimal singleDayVar = new BigDecimal(0.0);

    DataManager data = new DataManager();
    try {
      List<HistoricalQuote> historicalData = data.getHistoricalPrices(tickerSymbol);

      for (int i = 0; i < historicalData.size(); i++ ) {
        // Format: [<symbol>@<YYYY-MM-dd>: low-high, open-close (adjusted close)]
        System.out.println(i + ", " + historicalData.get(i).getAdjClose());
      }

      double dailiyVolatility = calculateVolatility(historicalData);

      double dailyStandardDeviation = assetValue * (dailiyVolatility);

      singleDayVar = BigDecimal.valueOf(normSinV).multiply(BigDecimal.valueOf(dailyStandardDeviation));

    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Single Day 99% VaR is: " + singleDayVar);
    return singleDayVar;
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

    double[][] deviations = new double[2][historicalData.size()];
    double sumOfSquaredDeviations = 0.0;

    for(int j = 0; j < historicalData.size(); j++) {
      deviations[0][j] = meanStockPrice.doubleValue() - historicalData.get(j).getAdjClose().doubleValue();
      deviations[1][j] = deviations[0][j] * deviations[0][j];

      sumOfSquaredDeviations += deviations[1][j];
    }

    double stockPriceVariance = sumOfSquaredDeviations / historicalData.size();

    double dailyVolatility = Math.sqrt(stockPriceVariance);

    System.out.println("Mean: " + meanStockPrice);
    System.out.println("Sum of Squared Deviations: " + sumOfSquaredDeviations);
    System.out.println("Stock price Variance: " + stockPriceVariance);
    System.out.println("Daily Volatility: " + dailyVolatility);

    return dailyVolatility;
  }

}
