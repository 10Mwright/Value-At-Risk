package net.mdwright.var;

import net.mdwright.var.objects.Normals;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import java.io.IOException;
import java.math.BigDecimal;
import net.mdwright.var.objects.VolatilityMethod;

/**
 * Class for performing complex value at risk calculations using the model-building methodology.
 *
 * @author Matthew Wright
 */
public class ModelBuildingVar implements VarCalculator {

  private DataManager data = new DataManager();
  private Portfolio portfolioData;

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability, VolatilityMethod volatilityChoice) {
    BigDecimal varValue = new BigDecimal(0);

    if (portfolio.getSize() == 1) {
      varValue = calculateVar(portfolio.getPosition(0),
          timeHorizon, probability, volatilityChoice);
    } else if (portfolio.getSize() >= 2) {
      varValue = calculateVar(portfolio.getPosition(0), portfolio.getPosition(1), timeHorizon,
          probability, volatilityChoice);
    }

    return varValue;
  }

  /**
   * Method to calculate VaR for 1 stock.
   *
   * @param position Position object containing position information.
   * @param timeHorizon number of days as an integer to act as the time horizon
   * @param probability A double value representing the percentage probability in decimal form
   * @return BigDecimal value representing the VaR of the single stock portfolio
   */
  public BigDecimal calculateVar(Position position, int timeHorizon,
      double probability, VolatilityMethod volatilityChoice) {
    double normSinV = Normals
        .getNormSinV(probability); //Retrieves appropriate NormSinV value for probability
    BigDecimal singleDayVar = new BigDecimal(0.0);
    BigDecimal multiDayVar = new BigDecimal(0.0);

    Boolean multiDay = false; //Default to false for multi day calculation
    if (timeHorizon > 1) {
      multiDay = true;
    }

    try {
      data.getHistoricalPrices(position, 365);

      portfolioData = new Portfolio(position);

      for (int i = 0; i < position.getHistoricalDataSize(); i++) {
        // Format: [<symbol>@<YYYY-MM-dd>: low-high, open-close (adjusted close)]
        System.out.println(i + ", " + position.getHistoricalData().get(i).getAdjClose());
      }

      double dailyVolatility = 0;

      if(volatilityChoice == VolatilityMethod.SIMPLE) {
        dailyVolatility = Varmath.calculateVolatility(position);
      } else if(volatilityChoice == VolatilityMethod.EWMA) {
        dailyVolatility = Varmath.calculateVolatility(portfolioData, 0, 0.94);
      } else if(volatilityChoice == VolatilityMethod.GARCH) {
        //TODO: IMPLEMENT GARCH METHOD
      }

      System.out.println("Daily Volatility: " + dailyVolatility);

      double dailyStandardDeviation = data.getCurrentValue(position).doubleValue() * (dailyVolatility);

      System.out.println("Daily Standard Deviation: " + dailyStandardDeviation);

      singleDayVar = BigDecimal.valueOf(normSinV)
          .multiply(BigDecimal.valueOf(dailyStandardDeviation));

      System.out.println("Single Day " + (probability * 100) + "% VaR is: " + singleDayVar);

      if (multiDay) {
        multiDayVar = singleDayVar.multiply(new BigDecimal(Math.sqrt(timeHorizon)));
        System.out.println(timeHorizon + " Day " + (probability * 100) + "VaR is: " + multiDayVar);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    if (multiDay) {
      portfolioData.setValueAtRisk(multiDayVar);
      return multiDayVar;
    } else {
      portfolioData.setValueAtRisk(singleDayVar);
      return singleDayVar;
    }
  }

  /**
   * Method to calculate VaR for 2 stocks.
   *
   * @param positionOne Position object containing data on first position
   * @param positionTwo Position object containing data on second position
   * @param timeHorizon number of days as an integer to act as the time horizon
   * @param probability A double value representing the percentage probability in decimal form
   * @return BigDecimal value representing the VaR of the two stock portfolio
   */
  public BigDecimal calculateVar(Position positionOne, Position positionTwo, int timeHorizon,
      double probability, VolatilityMethod volatilityChoice) {
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
      data.getHistoricalPrices(positionOne, 365);
      data.getHistoricalPrices(positionTwo, 365);

      portfolioData = new Portfolio(new Position[] {positionOne, positionTwo});

      double positionOneVolatility = 0;
      double positionTwoVolatility = 0;

      if(volatilityChoice == VolatilityMethod.SIMPLE) {
        positionOneVolatility = Varmath.calculateVolatility(positionOne);
        positionTwoVolatility = Varmath.calculateVolatility(positionTwo);
      } else if(volatilityChoice == VolatilityMethod.EWMA) {
        positionOneVolatility = Varmath.calculateVolatility(portfolioData, 0, 0.94);
        positionTwoVolatility = Varmath.calculateVolatility(portfolioData, 1,0.94);
      } else if(volatilityChoice == VolatilityMethod.GARCH) {
        //TODO: IMPLEMENT GARCH METHOD
      }

      // Calculate the coefficient of correlation between each position
      double coefficientOfCorrelation = calculateCoefficient(positionOne, positionTwo);

      // Calculate each standard deviation
      double positionOneSDeviation = data.getCurrentValue(positionOne).doubleValue() * positionOneVolatility;
      double positionTwoSDeviation = data.getCurrentValue(positionTwo).doubleValue() * positionTwoVolatility;

      double standardDeviation = Math.sqrt(
          Math.pow(positionOneSDeviation, 2) + Math.pow(positionTwoSDeviation, 2) + (2
              * coefficientOfCorrelation * positionOneSDeviation * positionTwoSDeviation));

      //Finally calculate 1 day VaR
      singleDayVar = BigDecimal.valueOf(normSinV).multiply(BigDecimal.valueOf(standardDeviation));

      System.out.println("Single Day " + (probability * 100) + "% VaR is: " + singleDayVar);

      if (multiDay) {
        multiDayVar = singleDayVar.multiply(new BigDecimal(Math.sqrt(timeHorizon)));
        System.out.println(timeHorizon + " Day " + (probability * 100) + "VaR is: " + multiDayVar);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (multiDay) {
      portfolioData.setValueAtRisk(multiDayVar);
      return multiDayVar;
    } else {
      portfolioData.setValueAtRisk(singleDayVar);
      return singleDayVar;
    }
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

    BigDecimal positionOneMean = Varmath.calculateMean(positionOne);
    BigDecimal positionTwoMean = Varmath.calculateMean(positionTwo);

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

  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability,
      int historicalDataLength) {
    throw new UnsupportedOperationException(
        "Invalid operation for model-building VaR (No such thing as Historical Data)");
  }

  @Override
  public Portfolio getData() {
    data.getCurrentPortfolioValue(portfolioData);

    return portfolioData;
  }
}
