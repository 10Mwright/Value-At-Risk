package net.mdwright.var;

import java.io.IOException;
import java.math.BigDecimal;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.VolatilityMethod;

public class MonteCarloVar implements VarCalculator {

  DataManager data = new DataManager();

  @Override
  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability,
      VolatilityMethod volatilityChoice) {

    try {
      for (int i = 0; i < portfolio.getSize(); i++) {
        data.getHistoricalPrices(portfolio.getPosition(i), 252); //Get historical data of all positions in portfolio
        data.getCurrentValue(portfolio.getPosition(i)); //Get current value of all positions in portfolio
      }

      double[][] percentageChangesMatrix = VarMath.getPercentageChangeMatrix(portfolio); //Get matrix of percent changes in portfolio


    } catch (IOException e) {
      e.printStackTrace();
    }

    return new BigDecimal(0);
  }

  @Override
  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability,
      int historicalDataLength) {
    throw new UnsupportedOperationException(
        "Invalid operation for model-building VaR (No such thing as Historical Data)");
  }

  @Override
  public Portfolio getData() {
    return null;
  }
}
