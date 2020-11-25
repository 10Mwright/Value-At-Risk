package net.mdwright.var;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import net.mdwright.var.objects.Scenario;
import yahoofinance.histquotes.HistoricalQuote;

public class HistoricalSimVar implements VarCalculator {

  @Override
  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability,
      int historicalDataLength) {

    int portfolioSize = portfolio.getSize();

    DataManager data = new DataManager();

    try {

      //Gather data for each position in the portfolio
      for (int i = 0; i < portfolioSize; i++) {
        String targetTickerSymbol = portfolio.getPosition(i).getTickerSymbol();
        portfolio.getPosition(i)
            .setHistoricalData(data.getHistoricalPrices(targetTickerSymbol, historicalDataLength));
      }

      BigDecimal[] varValues = new BigDecimal[portfolioSize];
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");

      for (int n = 0; n < portfolioSize; n++) {
        System.out.println("Position Scenarios: " + n);

        int dataSize = portfolio.getPosition(n).getHistoricalDataSize();

        Scenario[] scenarios = new Scenario[dataSize];

        //For each pair of dates
        for (int j = 0; j < dataSize; j++) {
          if ((j + 1) != dataSize) {
            System.out.println("COMPARING: " + sdf
                .format(portfolio.getPosition(n).getHistoricalData().get(j).getDate().getTime()));
            System.out.println("WITH: " + sdf.format(
                portfolio.getPosition(n).getHistoricalData().get(j + 1).getDate().getTime()));

            HistoricalQuote dayOne = portfolio.getPosition(n).getHistoricalData().get(j);
            HistoricalQuote dayTwo = portfolio.getPosition(n).getHistoricalData().get(j + 1);

            BigDecimal currentDayValue = portfolio.getPosition(n).getHistoricalData().get(dataSize - 1).getAdjClose();

            BigDecimal tempScenario = dayTwo.getAdjClose().divide(dayOne.getAdjClose(), 10, BigDecimal.ROUND_UP);

            tempScenario = tempScenario.multiply(currentDayValue);

            System.out.println("Scenario value (INITIAL):" + tempScenario);

            BigDecimal tempCurrentScenario = tempScenario.divide(currentDayValue, 10, BigDecimal.ROUND_UP);

            System.out.println("TEMP CURRENT SCENARIO: " + tempCurrentScenario);

            System.out.println("CURRENT DAY VALUE: " + currentDayValue);

            BigDecimal portfolioValue = new BigDecimal(portfolio.getPosition(n).getPositionValue());

            System.out.println("PORTFOLIO VALUE: " + portfolioValue);

            BigDecimal scenarioValue = portfolioValue.multiply(tempCurrentScenario);

            System.out.println("SCENARIOVALUE: " + scenarioValue);

            scenarioValue = scenarioValue.subtract(portfolioValue); //Get loss or gain compared to current value

            //Swapping the sign, gains are recorded as negative losses
            scenarioValue = scenarioValue.subtract(scenarioValue.multiply(new BigDecimal(2)));

            scenarios[j] = new Scenario(dayOne.getDate(), dayTwo.getDate(),
                scenarioValue);

            System.out.println("Scenario " + j);
            System.out.println("value under scenario: " + scenarios[j].getValueUnderScenario());
          }
        }

        //Transfer this array to each position object
        portfolio.getPosition(n).setScenarios(scenarios);

        //TODO: Review method of calculation from textbook
        Scenario[] scenariosSorted = portfolio.getPosition(n).sortScenarios();

        for (int o = 0; o < scenariosSorted.length; o++) {
          if (scenariosSorted[o] != null) {
            System.out.println("SCENARIO 0 SORTED: " + scenariosSorted[o].getValueUnderScenario());
            System.out.println(
                "DATES: " + sdf.format(scenariosSorted[o].getDateOne().getTime()) + " : " + sdf
                    .format(scenariosSorted[o].getDateTwo().getTime()));
          }
        }

        varValues[n] = scenariosSorted[getPercentileIndex(scenariosSorted, probability)]
            .getValueUnderScenario(); //

      }

      System.out.println("VAR: " + varValues[0]);

    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Method for finding the index of the desired percentile value.
   *
   * @param scenarios Array of type Scenario containing all scenarios for the position
   * @param desiredPercentile Double value representing the percentile in decimal format
   * @return int value representing the index of this percentile influence taken from
   * https://stackoverflow.com/questions/41413544/calculate-percentile-from-a-long-array
   */
  public int getPercentileIndex(Scenario[] scenarios, double desiredPercentile) {
    int indexOfPercentile = (int) Math.ceil(desiredPercentile / 100.0 * scenarios.length);

    return indexOfPercentile;
  }

  @Override
  public BigDecimal calculateVar(Position[] portfolio, int timeHorizon, double probability) {
    throw new UnsupportedOperationException(
        "Invalid operation for historical Simulation VaR (Requires historical data length)");
  }
}
