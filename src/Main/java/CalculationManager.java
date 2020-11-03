import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

public class CalculationManager {

  public static double calculateVar(double dailyVolatility, double assetValue){
    double dailyStandardDeviation = assetValue * (dailyVolatility / 100);

    double normSinV = 2.326; //For now we maintain this value i.e. a 99% VaR

    double singleDayVar = normSinV * dailyStandardDeviation;

    DataManager data = new DataManager();
    try {
      List<HistoricalQuote> historicalData = data.getHistoricalPrices("GOOG");

      for (int i = 0; i < historicalData.size(); i++ ) {
        // Format: [<symbol>@<YYYY-MM-dd>: low-high, open-close (adjusted close)]
        System.out.println(i + ", " + historicalData.get(i).getAdjClose());



      }

      calculateVolatility(historicalData);

    } catch (IOException e) {
      e.printStackTrace();
    }

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

    System.out.println("Mean: " + meanStockPrice);

    return 0.0;
  }

}
