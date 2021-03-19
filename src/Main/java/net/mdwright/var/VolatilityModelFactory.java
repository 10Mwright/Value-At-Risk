package net.mdwright.var;

import net.mdwright.var.objects.VolatilityMethod;

/**
 * Factory class for creating VolatilityModels.
 *
 * @author Matthew Wright
 */
public class VolatilityModelFactory {

  /**
   * Method to retrieve the relevant class depending on the user's choice of Model.
   * @param model VolatilityMethod enum object to be used
   * @return VolatilityModel object (either EWMAVolatility or SimpleVolatility).
   *
   *     Influence for this method was taken from the following tutorial:
   *     https://www.tutorialspoint.com/design_pattern/factory_pattern.htm
   */
  public static VolatilityModel getModel(VolatilityMethod model) {
    if (model == VolatilityMethod.SIMPLE) {
      return new SimpleVolatility();
    } else if (model == VolatilityMethod.EWMA) {
      return new EWMAVolatility();
    }

    return null;
  }

}
