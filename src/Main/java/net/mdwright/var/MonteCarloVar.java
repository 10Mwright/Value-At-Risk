package net.mdwright.var;

import java.math.BigDecimal;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.VolatilityMethod;

public class MonteCarloVar implements VarCalculator {

  @Override
  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability,
      VolatilityMethod volatilityChoice) {
    return null;
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
