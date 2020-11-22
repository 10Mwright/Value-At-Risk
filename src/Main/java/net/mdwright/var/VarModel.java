package net.mdwright.var;

import java.math.BigDecimal;
import net.mdwright.var.objects.Position;

public class VarModel {

  private VarCalculator modelBuilding = new ModelBuildingVar();

  public BigDecimal calculateVar(Position[] portfolio, int timeHorizon, double probability) {
    return modelBuilding.calculateVar(portfolio, timeHorizon, probability);
  }

  public BigDecimal calculateVar(Position[] portfolio, int timeHorizon, double probability, int historicalDataLength) {
    // Historical sim method.
    return new BigDecimal(0);
  }

}
