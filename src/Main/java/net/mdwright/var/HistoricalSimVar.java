package net.mdwright.var;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import net.mdwright.var.objects.Scenario;
import net.mdwright.var.objects.VolatilityMethod;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Class for performing complex value at risk calculations using the historical simulation
 * methodology.
 *
 * @author Matthew Wright
 */
public class HistoricalSimVar implements VarCalculator {

  private DataManager data = new DataManager();
  private Portfolio portfolioData;

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability,
      VolatilityMethod volatilityChoice) {
    throw new UnsupportedOperationException(
        "Invalid operation for Historical Simulation VaR (Requires historical data length)");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability,
      int historicalDataLength) {
    int portfolioSize = portfolio.getSize();

    BigDecimal valueAtRisk = new BigDecimal(0);

    try {
      //Gather data for each position in the portfolio
      for (int i = 0; i < portfolioSize; i++) {
        Position targetPosition = portfolio.getPosition(i);
        data.getHistoricalPrices(targetPosition, historicalDataLength);
        data.getCurrentValue(portfolio.getPosition(i)); //Calculate current position value
      }

      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
      int dataSize = portfolio.getPosition(0).getHistoricalDataSize();
      Scenario[] scenarios = new Scenario[dataSize];

      //For each pair of dates
      for (int j = 0; j < dataSize; j++) {
        if ((j + 1) != dataSize) {
          BigDecimal scenarioValue = new BigDecimal(0);
          BigDecimal scenarioValueTemp;

          //Setup objects to hold each day's data
          HistoricalQuote dayOne = null;
          HistoricalQuote dayTwo = null;

          // For each position in the portfolio
          for (int n = 0; n < portfolioSize; n++) {
            System.out.println("COMPARING: " + sdf
                .format(portfolio.getPosition(n).getHistoricalData().get(j).getDate().getTime()));
            System.out.println("WITH: " + sdf.format(
                portfolio.getPosition(n).getHistoricalData().get(j + 1).getDate().getTime()));

            //Gather the historical quote for these dates
            dayOne = portfolio.getPosition(n).getHistoricalData().get(j);
            dayTwo = portfolio.getPosition(n).getHistoricalData().get(j + 1);

            //Current day value becomes the most recent historical adjusted close price
            BigDecimal currentDayValue = portfolio.getPosition(n).getHistoricalData()
                .get(dataSize - 1).getAdjClose();

            // Dividing our 2nd day's value by our first day's value then multiplying by most recent
            BigDecimal tempScenario = dayTwo.getAdjClose()
                .divide(dayOne.getAdjClose(), 10, BigDecimal.ROUND_UP);

            tempScenario = tempScenario.multiply(currentDayValue);

            System.out.println("Scenario value (INITIAL):" + tempScenario);

            // Dividing our initial scenario value by the current day value
            BigDecimal tempCurrentScenario = tempScenario
                .divide(currentDayValue, 10, BigDecimal.ROUND_UP);

            System.out.println("TEMP CURRENT SCENARIO: " + tempCurrentScenario);

            System.out.println("CURRENT DAY VALUE: " + currentDayValue);

            BigDecimal portfolioValue = portfolio.getPosition(n).getPositionValue();

            System.out.println("PORTFOLIO VALUE: " + portfolioValue);

            // Multiplying our position's value with our initial scenario value
            scenarioValueTemp = portfolioValue.multiply(tempCurrentScenario);

            System.out.println("SCENARIOVALUE: " + scenarioValueTemp);

            // Subtracting our position's value to get gain or loss
            scenarioValueTemp = scenarioValueTemp
                .subtract(portfolioValue); //Get loss or gain compared to current value

            scenarioValueTemp = scenarioValueTemp.subtract(scenarioValueTemp
                .multiply(new BigDecimal(2))); //Swap signs, gains are recorded as -'tive losses

            scenarioValue = scenarioValue.add(
                scenarioValueTemp); //Add all scenario values of each position up for total value

          }

          scenarios[j] = new Scenario(dayOne.getDate(), dayTwo.getDate(),
              scenarioValue);

          System.out.println("Scenario " + j);
          System.out.println("value under scenario: " + scenarios[j].getValueUnderScenario());
          System.out.println("------------------------------------------------------------");
        }
      }

      Scenario[] scenariosSorted = sortScenarios(scenarios); //Sort the scenarios
      portfolio.setScenarios(scenariosSorted); //Set portfolio's scenario array to sorted array

      for (int o = 0; o < scenariosSorted.length; o++) { //Run through sorted array
        if (scenariosSorted[o] != null) {
          System.out.println("SCENARIO 0 SORTED: " + scenariosSorted[o].getValueUnderScenario());
          System.out.println(
              "DATES: " + sdf.format(scenariosSorted[o].getDateOne().getTime()) + " : " + sdf
                  .format(scenariosSorted[o].getDateTwo().getTime()));
        }
      }

      // Retrieving our percentile value in our sorted array
      valueAtRisk = scenariosSorted[getPercentileIndex(scenariosSorted, probability)]
          .getValueUnderScenario();

      System.out.println("Single Day " + (probability * 100) + "VaR is: " + valueAtRisk);

      //Calculate VaR over time horizon
      valueAtRisk = valueAtRisk.multiply(new BigDecimal(Math.sqrt(timeHorizon)));

      System.out.println(timeHorizon + " Day " + (probability * 100) + "VaR is: " + valueAtRisk);
      portfolio.setValueAtRisk(valueAtRisk);

      System.out.println("VAR VALUE: " + portfolio.getValueAtRisk());

    } catch (IOException e) {
      e.printStackTrace();
    }

    portfolio.setValueAtRisk(valueAtRisk); //Pass VaR to portfolio object

    portfolioData = portfolio; //Store portfolio object for data gathering by GUI
    return valueAtRisk;
  }

  /**
   * Method for returning a sorted array of scenarios.
   *
   * @return Array of type Scenario containing a sorted array by value (Descending)
   *
   *     Code adapted from:
   *     https://stackoverflow.com/questions/33462923/sort-elements-of-an-array-in-ascending-order
   */
  public Scenario[] sortScenarios(Scenario[] unsortedScenarios) {
    Scenario temp;

    //Runs through each scenario and the scenarios proceeding it
    for (int i = 0; i <= unsortedScenarios.length; i++) {
      for (int j = i + 1; j < unsortedScenarios.length; j++) {
        if (unsortedScenarios[i] != null && unsortedScenarios[j] != null) {
          //Compares the values, if lower then swap around
          if (unsortedScenarios[i].getValueUnderScenario()
              .compareTo(unsortedScenarios[j].getValueUnderScenario()) < 0) {
            temp = unsortedScenarios[i];
            unsortedScenarios[i] = unsortedScenarios[j];
            unsortedScenarios[j] = temp;
          }
        }
      }
    }

    return unsortedScenarios;
  }

  /**
   * Method for finding the index of the desired percentile value.
   *
   * @param scenarios Array of type Scenario containing all scenarios for the position
   * @param desiredPercentile Double value representing the percentile in decimal format
   * @return int value representing the index of this percentile influence taken from
   *     https://stackoverflow.com/questions/41413544/calculate-percentile-from-a-long-array
   */
  public int getPercentileIndex(Scenario[] scenarios, double desiredPercentile) {
    int indexOfPercentile = (int) Math.ceil(desiredPercentile / 10000 * scenarios.length);

    return indexOfPercentile;
  }

  @Override
  public Portfolio getData() {
    data.getCurrentPortfolioValue(portfolioData);

    return portfolioData;
  }
}
