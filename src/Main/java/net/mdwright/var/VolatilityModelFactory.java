package net.mdwright.var;

import net.mdwright.var.objects.VolatilityMethod;

public class VolatilityModelFactory {

  public VolatilityModel getModel(VolatilityMethod model) {
    if(model == VolatilityMethod.SIMPLE) {

    } else if(model == VolatilityMethod.EWMA) {
      return new EWMAVolatility();
    }

    return null;
  }

}
