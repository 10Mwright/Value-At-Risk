import Objects.Normals;
import Objects.Position;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Class for performing complex value at risk calculations.
 *
 * @author Matthew Wright
 */
public class CalculationManager {

  /**
   * Method to calculate VaR for 1 stock.
   *
   * @param tickerSymbol String representation of the stock's ticker symbol
   * @param assetValue Double value representing total value in tickerSymbol
   * @param timeHorizon number of days as an integer to act as the time horizon
   * @param probability A double value representing the percentage probability in decimal form
   * @return BigDecimal value representing the VaR of the single stock portfolio
   */
  public static BigDecimal calculateVar(String tickerSymbol, double assetValue, int timeHorizon,
      double probability) {
    double normSinV = Normals
        .getNormSinV(probability); //Retrieves appropriate NormSinV value for probability
    BigDecimal singleDayVar = new BigDecimal(0.0);
    BigDecimal multiDayVar = new BigDecimal(0.0);

    Boolean multiDay = false; //Default to false for multi day calculation
    if (timeHorizon > 1) {
      multiDay = true;
    }

    DataManager data = new DataManager();
    try {
      List<HistoricalQuote> historicalData = data.getHistoricalPrices(tickerSymbol);

      for (int i = 0; i < historicalData.size(); i++) {
        // Format: [<symbol>@<YYYY-MM-dd>: low-high, open-close (adjusted close)]
        System.out.println(i + ", " + historicalData.get(i).getAdjClose());
      }

      double dailyVolatility = calculateVolatility(historicalData);

      // Must convert this volatility to a percentage
      // Note: any better method to calculate as a percentage other than avg?
      // e.g. TESLA has a massive daily volatility over a 1 year period (60.4%)
      dailyVolatility = dailyVolatility;

      System.out.println("Daily Volatility: " + dailyVolatility);

      double dailyStandardDeviation = assetValue * (dailyVolatility);

      System.out.println("Daily Standard Deviation: " + dailyStandardDeviation);

      singleDayVar = BigDecimal.valueOf(normSinV)
          .multiply(BigDecimal.valueOf(dailyStandardDeviation));

      if (multiDay) {
        multiDayVar = singleDayVar.multiply(new BigDecimal(Math.sqrt(timeHorizon)));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Single Day " + (probability * 100) + "% VaR is: " + singleDayVar);
    System.out.println(timeHorizon + " Day " + (probability * 100) + "VaR is: " + multiDayVar);

    if (multiDay) {
      return multiDayVar;
    } else {
      return singleDayVar;
    }
  }

  /**
   * Method to calculate VaR for 2 stocks.
   *
   * @param positionOne Position object for the first stock position
   * @param positionTwo Position object for the second stock position
   * @param timeHorizon number of days as an integer to act as the time horizon
   * @param probability A double value representing the percentage probability in decimal form
   * @return BigDecimal value representing the VaR of the two stock portfolio
   */
  public static BigDecimal calculateVar(Position positionOne, Position positionTwo, int timeHorizon,
      double probability) {
    double normSinV = Normals
        .getNormSinV(probability); //Retrieves appropriate NormSinV value for probability

    BigDecimal singleDayVar = new BigDecimal(0.0);
    BigDecimal multiDayVar = new BigDecimal(0.0);

    Boolean multiDay = false; //Default to false for multi day calculation
    if (timeHorizon > 1) {
      multiDay = true;
    }

    DataManager data = new DataManager();
    try {
      List<HistoricalQuote> positionOneData = data
          .getHistoricalPrices(positionOne.getTickerSymbol());
      List<HistoricalQuote> positionTwoData = data
          .getHistoricalPrices(positionTwo.getTickerSymbol());

      double positionOneVolatility = calculateVolatility(positionOneData);
      double positionTwoVolatility = calculateVolatility(positionTwoData);

      // Calculate the coefficient of correlation between each position
      double coefficientOfCorrelation = calculateCoefficient(positionOneData, positionTwoData);

      // Calculate each standard deviation
      double positionOneSDeviation = positionOne.getPositionValue() * positionOneVolatility;
      double positionTwoSDeviation = positionTwo.getPositionValue() * positionTwoVolatility;

      double standardDeviation = Math.sqrt(
          Math.pow(positionOneSDeviation, 2) + Math.pow(positionTwoSDeviation, 2) + (2
              * coefficientOfCorrelation * positionOneSDeviation * positionTwoSDeviation));

      //Finally calculate 1 day VaR
      singleDayVar = BigDecimal.valueOf(normSinV).multiply(BigDecimal.valueOf(standardDeviation));

      System.out.println("SINGLE DAY (MULTI): " + singleDayVar);

      if (multiDay) {
        multiDayVar = singleDayVar.multiply(new BigDecimal(Math.sqrt(timeHorizon)));
        System.out.println("MULTI DAY (MULTI): " + multiDayVar);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (multiDay) {
      return multiDayVar;
    } else {
      return singleDayVar;
    }
  }

  /**
   * Method for calculating the daily volatility of a stock based on it's historical data.
   *
   * @param historicalData List of type HistoricalQuote
   * @return A double value representing the daily volatility of the stock influence from website:
   * https://www.wallstreetmojo.com/volatility-formula/
   */
  public static double calculateVolatility(List<HistoricalQuote> historicalData) {

    BigDecimal meanStockPrice = calculateMean(historicalData);

    BigDecimal[][] deviations = new BigDecimal[2][historicalData.size()];
    BigDecimal sumOfSquaredDeviations = new BigDecimal(0.0);

    for (int j = 0; j < historicalData.size(); j++) {
      deviations[0][j] = meanStockPrice.subtract(historicalData.get(j).getAdjClose());
      deviations[1][j] = deviations[0][j].multiply(deviations[0][j]);

      sumOfSquaredDeviations = sumOfSquaredDeviations.add(deviations[1][j]);
    }

    BigDecimal stockPriceVariance = sumOfSquaredDeviations
        .divide(new BigDecimal(historicalData.size()), 2, RoundingMode.UP);

    double dailyVolatility = Math.sqrt(stockPriceVariance.doubleValue());

    System.out.println("Mean: " + meanStockPrice);
    System.out.println("Sum of Squared Deviations: " + sumOfSquaredDeviations);
    System.out.println("Stock price Variance: " + stockPriceVariance);
    System.out.println("Daily Volatility (RAW VALUE): " + dailyVolatility);

    dailyVolatility = dailyVolatility / (meanStockPrice.doubleValue());

    return dailyVolatility;
  }

  /**
   * Method for calculating the coefficient of correlation between two positions.
   *
   * @param positionOneData List of type HistoricalQuote for position one
   * @param positionTwoData List of type HistoricalQuote for position two
   * @return A double value representing the coefficient in range -1 to 1. Influence for calculation
   * method taken from https://budgeting.thenest.com/correlation-two-stocks-32359.html
   */
  public static double calculateCoefficient(List<HistoricalQuote> positionOneData,
      List<HistoricalQuote> positionTwoData) {

    int dataSize = 0;

    // Match length of each dataset if required
    if (positionOneData.size() != positionTwoData.size()) {
      if (positionOneData.size() < positionTwoData.size()) {
        dataSize = positionOneData.size();
      } else {
        dataSize = positionTwoData.size();
      }
    } else {
      dataSize = positionOneData.size();
    }

    BigDecimal positionOneMean = calculateMean(positionOneData);
    BigDecimal positionTwoMean = calculateMean(positionTwoData);

    // Column 1: positionOneMean - positionOnePrice
    // Column 2: positionTwoMean - positionTwoPrice
    // Column 3: square(Column 1)
    // Column 4: square(Column 2)
    // Column 5: product (multiplied) column 1 and 2
    BigDecimal[][] deviations = new BigDecimal[dataSize][5];

    BigDecimal sumSquaredOne = new BigDecimal(0.0);
    BigDecimal sumSquaredTwo = new BigDecimal(0.0);
    BigDecimal sumProduct = new BigDecimal(0.0);

    for (int i = 0; i < dataSize; i++) {
      deviations[i][0] = positionOneMean.subtract(positionOneData.get(i).getAdjClose());
      deviations[i][1] = positionTwoMean.subtract(positionTwoData.get(i).getAdjClose());
      deviations[i][2] = deviations[i][0].multiply(deviations[i][0]);
      deviations[i][3] = deviations[i][1].multiply(deviations[i][1]);
      deviations[i][4] = deviations[i][0].multiply(deviations[i][1]);

      sumSquaredOne = sumSquaredOne.add(deviations[i][2]);
      sumSquaredTwo = sumSquaredTwo.add(deviations[i][3]);
      sumProduct = sumProduct.add(deviations[i][4]);
    }

    double coefficient = sumProduct.doubleValue() / (Math
        .sqrt(sumSquaredOne.doubleValue() * sumSquaredTwo.doubleValue()));

    System.out.println("Coefficient of Correlation: " + coefficient);

    return coefficient;
  }

  /**
   * Method for calculating the mean close price across a historical data set.
   *
   * @param historicalData List of type HistoricalQuote holding previous stock data
   * @return BigDecimal value representing the mean across the entire data set
   */
  public static BigDecimal calculateMean(List<HistoricalQuote> historicalData) {
    BigDecimal meanStockPrice = new BigDecimal("0.0");

    // Increment through the data and sum them all up
    for (int i = 0; i < historicalData.size(); i++) {
      meanStockPrice = meanStockPrice.add(historicalData.get(i).getAdjClose());
    }

    meanStockPrice = meanStockPrice
        .divide(new BigDecimal(historicalData.size()), 2, RoundingMode.UP);

    return meanStockPrice;
  }

}
