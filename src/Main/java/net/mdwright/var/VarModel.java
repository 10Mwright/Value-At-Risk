package net.mdwright.var;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.VolatilityMethod;

/**
 * Class to send calculation requests to appropriate calculating classes.
 *
 * @author Matthew Wright
 */
public class VarModel {

  private final int defaultDataLength = 252;

  private VarCalculator modelBuilding = new ModelBuildingVar();
  private VarCalculator historicalSim = new HistoricalSimVar();

  private Portfolio portfolioData;

  /**
   * Method to calculate VaR for a portfolio using the model-building model.
   *
   * @param portfolio An array of Positions containing ticker symbols and values for each position
   *     in the portfolio
   * @param timeHorizon An int value representing the of days to calculate VaR over
   * @param probability A double value representing the percentage probability in decimal form
   * @param volatilityChoice Enum representing the variance method to be used
   * @return BigDecimal value representing the VaR of the portfolio
   */
  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability,
      VolatilityMethod volatilityChoice) {

    Calendar startDate = Calendar.getInstance();
    startDate.add(Calendar.DAY_OF_YEAR, -defaultDataLength);

    Calendar endDate = Calendar.getInstance();

    try {
      DataManager.getHistoricalPrices(portfolio, startDate, endDate);


      BigDecimal var = modelBuilding.calculateVar(portfolio, timeHorizon, probability,
          volatilityChoice); //Call underlying method in ModelBuildingVar

      portfolioData = modelBuilding.getData();

      return var;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new BigDecimal(0); //Default value of 0
  }

  /**
   * Method to calculate VaR for a portfolio using the historical sim model.
   *
   * @param portfolio An array of Positions containing ticker symbols and values for each position
   *     in the portfolio
   * @param timeHorizon An int value representing the number of days to calculate VaR over
   * @param probability A double value representing the percentage probability in decimal form
   * @param historicalDataLength An int value representing the number of days to fetch historical
   *     data for
   * @return BigDecimal value representing the VaR of the portfolio
   */
  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability,
      int historicalDataLength) {

    Calendar startDate = Calendar.getInstance();
    startDate.add(Calendar.DAY_OF_YEAR, -historicalDataLength);

    Calendar endDate = Calendar.getInstance();

    try {
      DataManager.getHistoricalPrices(portfolio, startDate, endDate);

      //Call underlying method in HistoricalSimVar
      BigDecimal var =  historicalSim.calculateVar(portfolio, timeHorizon, probability);

      portfolioData = historicalSim.getData();

      return var;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new BigDecimal(0); //Default value of 0
  }

  public Portfolio getPortfolioData() {
    return portfolioData;
  }

}
