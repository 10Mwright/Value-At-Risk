package net.mdwright.var;

import java.io.IOException;
import java.math.BigDecimal;
import net.mdwright.var.objects.Normals;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import net.mdwright.var.objects.VolatilityMethod;

/**
 * Class for performing complex value at risk calculations using the model-building methodology.
 *
 * @author Matthew Wright
 */
public class ModelBuildingVar implements VarCalculator {

  private final int probabilityScale = 100; //Used to scale probabilities up/down

  private DataManager data = new DataManager();
  private Portfolio portfolioData;

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability) {
    throw new UnsupportedOperationException(
        "Invalid operation for model-building VaR (No such thing as Historical Data Length)");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability,
      VolatilityMethod volatilityChoice) { //Entry method, chooses approach based on portfolio size
    BigDecimal valueAtRisk;

    System.out.println("TEST");

    if (portfolio.getSize() == 1) { //Single asset case
      valueAtRisk = calculateVarSingle(portfolio,
          timeHorizon, probability, volatilityChoice);
    } else if (portfolio.getSize() == 2) { //Two asset case
      valueAtRisk = calculateVarDouble(portfolio, timeHorizon,
          probability, volatilityChoice);
    } else { //Linear case
      valueAtRisk = calculateVarLinear(portfolio, timeHorizon, probability, volatilityChoice);
    }

    return valueAtRisk;
  }

  /**
   * Method to calculate VaR for 1 stock.
   *
   * @param portfolio Portfolio object containing a singular position
   * @param timeHorizon number of days as an integer to act as the time horizon
   * @param probability A double value representing the percentage probability in decimal form
   * @param volatilityChoice Enum representing the variance method to be used
   * @return BigDecimal value representing the VaR of the single stock portfolio
   */
  public BigDecimal calculateVarSingle(Portfolio portfolio, int timeHorizon,
      double probability, VolatilityMethod volatilityChoice) {
    double normSinV = Normals
        .getNormSinV(probability); //Retrieves appropriate NormSinV value for probability
    BigDecimal valueAtRisk = new BigDecimal(0);

      Position position = portfolio.getPosition(0); //Get the position from portfolio

      for (int i = 0; i < position.getHistoricalDataSize(); i++) {
        // Format: [<symbol>@<YYYY-MM-dd>: low-high, open-close (adjusted close)]
        System.out.println(i + ", " + position.getHistoricalData().get(i).getAdjClose());
      }

      double dailyVolatility = VolatilityModelFactory.getModel(volatilityChoice)
          .calculateVolatility(portfolio, 0);

      System.out.println("Daily Volatility: " + dailyVolatility);

      double dailyStandardDeviation = data.getCurrentValue(position).doubleValue()
          * (dailyVolatility);

      System.out.println("Daily Standard Deviation: " + dailyStandardDeviation);

      valueAtRisk = BigDecimal.valueOf(Math.abs(normSinV))
          .multiply(BigDecimal.valueOf(dailyStandardDeviation));

      System.out.println("Single Day " + (probability * probabilityScale)
          + "VaR is: " + valueAtRisk);

      //Calculate VaR over time horizon
      valueAtRisk = valueAtRisk.multiply(new BigDecimal(Math.sqrt(timeHorizon)));

      System.out.println(timeHorizon + " Day " + (probability * probabilityScale) + "VaR is: "
          + valueAtRisk);

    portfolio.setValueAtRisk(valueAtRisk); //Pass VaR to portfolio object

    portfolioData = portfolio; //Store portfolio object for data gathering by GUI
    return valueAtRisk;
  }

  /**
   * Method to calculate VaR for 2 stocks.
   *
   * @param portfolio Portfolio object containing two positions
   * @param timeHorizon number of days as an integer to act as the time horizon
   * @param probability A double value representing the percentage probability in decimal form
   * @param volatilityChoice Enum object representing the variance method to be used
   * @return BigDecimal value representing the VaR of the two stock portfolio
   */
  public BigDecimal calculateVarDouble(Portfolio portfolio, int timeHorizon,
      double probability, VolatilityMethod volatilityChoice) {
    double normSinV = Normals
        .getNormSinV(probability); //Retrieves appropriate NormSinV value for probability

    BigDecimal valueAtRisk = new BigDecimal(0);

      Position positionOne = portfolio.getPosition(0); //Get first position in portfolio
      Position positionTwo = portfolio.getPosition(1); //Get 2nd position in portfolio

      double positionOneVolatility = VolatilityModelFactory.getModel(volatilityChoice)
          .calculateVolatility(portfolio, 0);
      double positionTwoVolatility = VolatilityModelFactory.getModel(volatilityChoice)
          .calculateVolatility(portfolio, 1);

      // Calculate the coefficient of correlation between each position
      double coefficientOfCorrelation = VarMath.calculateCoefficient(positionOne, positionTwo);

      // Calculate each standard deviation
      double positionOneSDeviation = data.getCurrentValue(positionOne).doubleValue()
          * positionOneVolatility;
      double positionTwoSDeviation = data.getCurrentValue(positionTwo).doubleValue()
          * positionTwoVolatility;

      // Calculate cumulative standard deviation
      double standardDeviation = Math.sqrt(
          Math.pow(positionOneSDeviation, 2) + Math.pow(positionTwoSDeviation, 2) + (2
              * coefficientOfCorrelation * positionOneSDeviation * positionTwoSDeviation));

      //Finally calculate 1 day VaR
      valueAtRisk = BigDecimal.valueOf(Math.abs(normSinV)).multiply(BigDecimal.valueOf(standardDeviation));

      System.out.println("Single Day " + (probability * probabilityScale)
          + "VaR is: " + valueAtRisk);

      //Calculate VaR over time horizon
      valueAtRisk = valueAtRisk.multiply(new BigDecimal(Math.sqrt(timeHorizon)));

      System.out.println(timeHorizon + " Day " + (probability * probabilityScale)
          + "VaR is: " + valueAtRisk);

    portfolio.setValueAtRisk(valueAtRisk); //Pass VaR to portfolio object

    portfolioData = portfolio; //Store portfolio object for data gathering by GUI
    return valueAtRisk;
  }

  /**
   * Method to calculate value at risk using the linear covariance method.
   * @param portfolio Portfolio object containing associated positions
   * @param timeHorizon number of days as an integer to act as the time horizon
   * @param probability A double value representing the percentage probability in decimal form
   * @param volatilityChoice Enum value representing the volatility calculation to be used
   * @return A BigDecimal value representing the value at risk across the time horizon and at a
   *     certain probability
   */
  public BigDecimal calculateVarLinear(Portfolio portfolio, int timeHorizon, double probability,
      VolatilityMethod volatilityChoice) {
    double normSinV = Normals
        .getNormSinV(probability); //Retrieves appropriate NormSinV value for probability
    BigDecimal valueAtRisk = new BigDecimal(0);

      BigDecimal[] currentValues = new BigDecimal[portfolio.getSize()];

      for (int i = 0; i < portfolio.getSize(); i++) {
        currentValues[i] = data.getCurrentValue(portfolio.getPosition(i));
      }

      double[][] covariances = VolatilityModelFactory.getModel(volatilityChoice)
          .calculateCovarianceMatrix(portfolio);
      BigDecimal portfolioVariance = new BigDecimal(0);

      for (int i = 0; i < portfolio.getSize(); i++) { //Run through rows
        for (int j = 0; j < portfolio.getSize(); j++) { //Run through columns
          BigDecimal variance = new BigDecimal(covariances[i][j]); //Covariance between asset i & j
          variance = variance.multiply(currentValues[i]); //Multiplied by current value of asset i
          variance = variance.multiply(currentValues[j]); //Multiplied by current value of asset j

          portfolioVariance = portfolioVariance.add(variance); //Sum up all variances
        }
      }

      double portfolioVolatility = Math.sqrt(portfolioVariance.doubleValue());

      System.out.println("Portfolio Volatility: " + portfolioVolatility);

      valueAtRisk = new BigDecimal(Math.abs(normSinV)); //Using absolute value to remove -
      valueAtRisk = valueAtRisk.multiply(new BigDecimal(portfolioVolatility));

      System.out.println("Single Day " + (probability * probabilityScale)
          + "VaR is: " + valueAtRisk);

      //Calculate VaR over time horizon
      valueAtRisk = valueAtRisk.multiply(new BigDecimal(Math.sqrt(timeHorizon)));

      System.out.println(timeHorizon + " Day " + (probability * probabilityScale)
          + "VaR is: " + valueAtRisk);

    portfolio.setValueAtRisk(valueAtRisk); //Pass VaR to portfolio object

    portfolioData = portfolio; //Store portfolio object for data gathering by GUI
    return valueAtRisk;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Portfolio getData() {
    data.getCurrentPortfolioValue(portfolioData);

    return portfolioData;
  }
}
