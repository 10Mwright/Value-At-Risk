import java.io.IOException;
import java.math.BigDecimal;
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
        System.out.println(historicalData.get(i));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return singleDayVar;
  }

}
