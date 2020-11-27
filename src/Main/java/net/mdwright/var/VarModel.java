package net.mdwright.var;

import java.math.BigDecimal;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;

public class VarModel {

  private VarCalculator modelBuilding = new ModelBuildingVar();
  private VarCalculator historicalSim = new HistoricalSimVar();

  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability) {
    return modelBuilding.calculateVar(portfolio, timeHorizon, probability);
  }

  public BigDecimal calculateVar(Portfolio portfolio, int timeHorizon, double probability, int historicalDataLength) {
    return historicalSim.calculateVar(portfolio, timeHorizon, probability, historicalDataLength);
  }

}
