package net.mdwright.var;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Class for supporting calculations used in value at risk models.
 * @author Matthew Wright
 */
public class VarMath {

  private static final int divisionScale = 2; //Scale to be used when dividing using big decimals

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

  /**
   * Method for calculating the coefficient of correlation between two positions.
   *
   * @param positionOne Position object of the first position to calculate with
   * @param positionTwo Position object of the second position to calculate with
   * @return A double value representing the coefficient in range -1 to 1 Influence for calculation
   * method taken from https://budgeting.thenest.com/correlation-two-stocks-32359.html
   */
  public static double calculateCoefficient(Position positionOne,
      Position positionTwo) {

    int dataSize = 0;

    // Match length of each dataset if required
    if (positionOne.getHistoricalDataSize() != positionTwo.getHistoricalDataSize()) {
      if (positionOne.getHistoricalDataSize() < positionTwo.getHistoricalDataSize()) {
        dataSize = positionOne.getHistoricalDataSize();
      } else {
        dataSize = positionTwo.getHistoricalDataSize();
      }
    } else {
      dataSize = positionOne.getHistoricalDataSize();
    }

    BigDecimal positionOneMean = VarMath.calculateMean(positionOne);
    BigDecimal positionTwoMean = VarMath.calculateMean(positionTwo);

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
      deviations[i][0] = positionOneMean.subtract(positionOne.getHistoricalData().get(i).getAdjClose());
      deviations[i][1] = positionTwoMean.subtract(positionTwo.getHistoricalData().get(i).getAdjClose());
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
   * Method to create an array of percentage changes between each days prices.
   * @param portfolio Portfolio object containing the position object and historical data
   * @param positionIndex int value representing the index pointer for the target asset in the portfolio array
   * @return An array of doubles representing the percentage changes between each day's closing price
   */
  public static double[] getPercentageChanges(Portfolio portfolio, int positionIndex) {
    List<HistoricalQuote> historicalData = portfolio.getPosition(positionIndex).getHistoricalData();
    double[] percentageChanges = new double[historicalData.size()-1];

    for (int i = 1; i < historicalData.size(); i++) {
      BigDecimal currentDay = historicalData.get(i).getAdjClose();
      BigDecimal previousDay = historicalData.get(i-1).getAdjClose();

      BigDecimal tempValue = currentDay.subtract(previousDay);

      tempValue = tempValue.divide(previousDay, divisionScale, BigDecimal.ROUND_UP);

      percentageChanges[i-1] = tempValue.doubleValue();
    }

    return percentageChanges;
  }

  /**
   * Method to retrieve the size of the smallest historical dataset in a portfolio.
   * @param portfolio Portfolio object containing position objects with historical data
   * @return int value representing the size of the smallest dataset in the portfolio
   */
  public static int getSmallestDatasetSize(Portfolio portfolio) {
    int smallestDataset = portfolio.getPosition(0).getHistoricalDataSize();

    for (int i = 0; i < portfolio.getSize(); i++) { //Cycle through portfolio sampling dataset sizes to find smallest
      int currentPositionDataset = portfolio.getPosition(i).getHistoricalDataSize();

      if(currentPositionDataset < smallestDataset) {
        smallestDataset = currentPositionDataset; //Set smallest size to this size if smaller than current smallestSize
      }
    }

    return smallestDataset-1; //To correct size for for loops
  }

  /**
   * Method to retrieve a list of hashmaps relating dates to prices for charting the portfolio
   *     value.
   * @param portfolio Portfolio object containing position objects with historical data
   * @return List of Maps mapping a date in string format to the days adj. closing price
   */
  public static List<Map<String, BigDecimal>> getHashMaps(Portfolio portfolio) {
    //List of maps to store dates along with their adj. closing prices on those dates
    List<Map<String, BigDecimal>> portfolioPriceMap = new ArrayList<Map<String, BigDecimal>>();

    for (int i = 0; i < portfolio.getSize(); i++) {
      Map<String, BigDecimal> positionPriceMap = new HashMap<String, BigDecimal>();
      Position currentPosition = portfolio.getPosition(i);

      for (int j = 0; j < currentPosition.getHistoricalDataSize(); j++) {

      }
    }

    return portfolioPriceMap;
  }
}
