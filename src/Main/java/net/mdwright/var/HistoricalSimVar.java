package net.mdwright.var;

import java.io.IOException;
import java.math.BigDecimal;
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

      Scenario[][] scenarios = new Scenario[portfolioSize][historicalDataLength];

      for (int n = 0; n < portfolioSize; n++) {
        System.out.println("Position Scenarios: " + n);

        int dataSize = portfolio.getPosition(n).getHistoricalDataLength();
        BigDecimal currentDayValue = portfolio.getPosition(n).getHistoricalData().get(dataSize - 1)
            .getAdjClose(); //Get last day's value to use as current value

        //For each pair of dates
        for (int j = 0; j < (dataSize); j++) {
          if ((j + 1) != dataSize) {
            HistoricalQuote dayOne = portfolio.getPosition(n).getHistoricalData().get(j);
            HistoricalQuote dayTwo = portfolio.getPosition(n).getHistoricalData().get(j + 1);

            BigDecimal temp = dayTwo.getAdjClose()
                .divide(dayOne.getAdjClose(), 2, BigDecimal.ROUND_UP);

            scenarios[n][j] = new Scenario(dayOne.getDate(), dayTwo.getDate(),
                currentDayValue.multiply(temp));

            System.out.println("Scenario " + j);
            System.out.println("value under scenario: " + scenarios[n][j].getValueUnderScenario());
          }
        }

        //TODO: implement sorting and finally select appropriate percentile.

      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public BigDecimal calculateVar(Position[] portfolio, int timeHorizon, double probability) {
    throw new UnsupportedOperationException(
        "Invalid operation for historical Simulation VaR (Requires historical data length)");
  }
}
