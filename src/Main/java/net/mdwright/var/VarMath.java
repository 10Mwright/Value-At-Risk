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
}
