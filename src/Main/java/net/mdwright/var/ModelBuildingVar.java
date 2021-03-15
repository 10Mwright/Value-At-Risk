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

      dailyVolatility = VolatilityModelFactory.getModel(volatilityChoice).calculateVolatility(portfolioData, 0);

      /*
      if(volatilityChoice == VolatilityMethod.SIMPLE) {
        dailyVolatility = VarMath.calculateVolatility(position);
      } else if(volatilityChoice == VolatilityMethod.EWMA) {
        dailyVolatility = VarMath.calculateVolatility(portfolioData, 0, 0.94);
      } else if(volatilityChoice == VolatilityMethod.GARCH) {
        //TODO: IMPLEMENT GARCH METHOD
      }
      */

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

      positionOneVolatility = VolatilityModelFactory.getModel(volatilityChoice).calculateVolatility(portfolioData, 0);
      positionTwoVolatility = VolatilityModelFactory.getModel(volatilityChoice).calculateVolatility(portfolioData, 1);

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

    if (multiDay) {
      portfolioData.setValueAtRisk(multiDayVar);
      return multiDayVar;
    } else {
      portfolioData.setValueAtRisk(singleDayVar);
      return singleDayVar;
    }
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
