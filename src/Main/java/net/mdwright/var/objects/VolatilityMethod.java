package net.mdwright.var.objects;

/**
 * Enum object for methods for calculating variance and volatility.
 *
 * @author Matthew Wright
 */
public enum VolatilityMethod {
  SIMPLE("Simple Volatility"),
  EWMA("EWMA");

  private String stringValue; //String representation for printing out on the GUI
  public static String[] stringValues = {"Simple Volatility", "EWMA"};

  /**
   * Constructor method to set the string value of a new instance of this enum.
   * @param value String representation of the variance/volatility method
   */
  VolatilityMethod(String value) {
    stringValue = value;
  }

  @Override
  public String toString() {
    return stringValue;
  }

  /**
   * Method to convert a string value to an enum object of that value.
   * @param value A string representation fo the variance/volatility method desired
   * @return An instance of this VolatilityMethod object
   */
  //Taken from https://stackoverflow.com/questions/604424/how-to-get-an-enum-value-from-a-string-value-in-java
  public static VolatilityMethod fromString(String value) {
    for (VolatilityMethod method : VolatilityMethod.values()) {
      if (method.stringValue.equalsIgnoreCase(value)) {
        return method;
      }
    }

    return null;
  }

}
