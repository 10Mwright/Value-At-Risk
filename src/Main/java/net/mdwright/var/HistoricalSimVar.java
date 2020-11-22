package net.mdwright.var;

import java.math.BigDecimal;
import net.mdwright.var.objects.Position;

public class HistoricalSimVar implements VarCalculator {

  @Override
  public BigDecimal calculateVar(Position[] portfolio, int timeHorizon, double probability,
      int historicalDataLength) {
    return null;
  }

  @Override
  public BigDecimal calculateVar(Position[] portfolio, int timeHorizon, double probability) {
    throw new UnsupportedOperationException(
        "Invalid operation for model-building VaR (No such thing as Historical Data)");
  }
}
