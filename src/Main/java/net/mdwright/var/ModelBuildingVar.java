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
    BigDecimal varValue;

    if (portfolio.getSize() == 1) {
      varValue = calculateVarSingle(portfolio,
          timeHorizon, probability, volatilityChoice);
    } else if (portfolio.getSize() == 2) {
      varValue = calculateVarDouble(portfolio, timeHorizon,
          probability, volatilityChoice);
    } else {
      varValue = calculateVarLinear(portfolio, timeHorizon, probability, volatilityChoice);
    }

    return varValue;
  }

  /**
   * Method to calculate VaR for 1 stock.
   *
   * @param portfolio Portfolio object containing a singular position
   * @param timeHorizon number of days as an integer to act as the time horizon
   * @param probability A double value representing the percentage probability in decimal form
   * @return BigDecimal value representing the VaR of the single stock portfolio
   */
  public BigDecimal calculateVarSingle(Portfolio portfolio, int timeHorizon,
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
      Position position = portfolio.getPosition(0); //Get single position from portfolio

      data.getHistoricalPrices(position, 365);

      for (int i = 0; i < position.getHistoricalDataSize(); i++) {
        // Format: [<symbol>@<YYYY-MM-dd>: low-high, open-close (adjusted close)]
        System.out.println(i + ", " + position.getHistoricalData().get(i).getAdjClose());
      }

      double dailyVolatility = 0;

      dailyVolatility = VolatilityModelFactory.getModel(volatilityChoice).calculateVolatility(portfolio, 0);

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

    portfolioData = portfolio;

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
   * @param portfolio Portfolio object containing two positions
   * @param timeHorizon number of days as an integer to act as the time horizon
   * @param probability A double value representing the percentage probability in decimal form
   * @return BigDecimal value representing the VaR of the two stock portfolio
   */
  public BigDecimal calculateVarDouble(Portfolio portfolio, int timeHorizon,
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
      Position positionOne = portfolio.getPosition(0); //Get first position in portfolio
      Position positionTwo = portfolio.getPosition(1); //Get 2nd position in portfolio

      data.getHistoricalPrices(positionOne, 365);
      data.getHistoricalPrices(positionTwo, 365);

      double positionOneVolatility = 0;
      double positionTwoVolatility = 0;

      positionOneVolatility = VolatilityModelFactory.getModel(volatilityChoice).calculateVolatility(portfolio, 0);
      positionTwoVolatility = VolatilityModelFactory.getModel(volatilityChoice).calculateVolatility(portfolio, 1);

      // Calculate the coefficient of correlation between each position
      double coefficientOfCorrelation = VarMath.calculateCoefficient(positionOne, positionTwo);

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

    portfolioData = portfolio;

    if (multiDay) {
      portfolioData.setValueAtRisk(multiDayVar);
      return multiDayVar;
    } else {
      portfolioData.setValueAtRisk(singleDayVar);
      return singleDayVar;
    }
  }

  /**
   * Method to calculate value at risk using the linear covariance method.
   * @param portfolio Portfolio object containing associated positions
   * @param timeHorizon number of days as an integer to act as the time horizon
   * @param probability A double value representing the percentage probability in decimal form
   * @param volatilityChoice Enum value representing the volatility calculation to be used
   * @return A BigDecimal value representing the value at risk across the time horizon and at a certain probability
   */
  public BigDecimal calculateVarLinear(Portfolio portfolio, int timeHorizon, double probability, VolatilityMethod volatilityChoice) {
    double normSinV = Normals
        .getNormSinV(probability); //Retrieves appropriate NormSinV value for probability
    BigDecimal var = new BigDecimal(0);

    try {
      BigDecimal[] currentValues = new BigDecimal[portfolio.getSize()];

      for (int i = 0; i < portfolio.getSize(); i++) {
        data.getHistoricalPrices(portfolio.getPosition(i), 252);
        currentValues[i] = data.getCurrentValue(portfolio.getPosition(i));
      }

      double[][] covariances = VolatilityModelFactory.getModel(volatilityChoice).calculateCovarianceMatrix(portfolio);
      BigDecimal portfolioVariance = new BigDecimal(0);

      for (int i = 0; i < portfolio.getSize(); i++) { //Run through rows
        for (int j = 0; j < portfolio.getSize(); j++) { //Run through columns
          BigDecimal variance = new BigDecimal(covariances[i][j]); //Takes covariance between asset i and j
          variance = variance.multiply(currentValues[i]); //Multiplied by current value of asset i
          variance = variance.multiply(currentValues[j]); //Multiplied by current value of asset j

          portfolioVariance = portfolioVariance.add(variance); //Sum up all variances
        }
      }

      double portfolioVolatility = Math.sqrt(portfolioVariance.doubleValue());

      System.out.println("Volatiliy Portfolio: " + portfolioVolatility);

      var = new BigDecimal(Math.abs(normSinV)); //Using absolute value to remove negative sign if necessary
      var = var.multiply(new BigDecimal(portfolioVolatility));
      var = var.multiply(new BigDecimal(Math.sqrt(timeHorizon)));

      portfolioData = portfolio;

      System.out.println("Var: " + var);
    } catch (IOException e) {
      e.printStackTrace();
    }

    portfolioData.setValueAtRisk(var);
    return var;
  }

  /**
   * {@inheritDoc}
   */
  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability,
      int historicalDataLength) {
    throw new UnsupportedOperationException(
        "Invalid operation for model-building VaR (No such thing as Historical Data)");
  }

  /**
   * Pass through method to retrieve portfolio values after var calculation.
   * @return Portfolio object containing all up to date values calculated during modelling
   */
  @Override
  public Portfolio getData() {
    data.getCurrentPortfolioValue(portfolioData);

    return portfolioData;
  }
}
