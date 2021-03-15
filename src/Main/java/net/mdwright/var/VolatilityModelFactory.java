package net.mdwright.var;

import net.mdwright.var.objects.VolatilityMethod;

public class VolatilityModelFactory {

  public static VolatilityModel getModel(VolatilityMethod model) {
    if(model == VolatilityMethod.SIMPLE) {
      return new SimpleVolatility();
    } else if(model == VolatilityMethod.EWMA) {
      return new EWMAVolatility();
    }

    return null;
  }

}
