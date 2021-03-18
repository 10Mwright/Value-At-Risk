package net.mdwright.var.objects;

public enum VolatilityMethod {
  SIMPLE("Simple Volatility"),
  EWMA("EWMA");

  private String stringValue;
  public static String[] stringValues = {"Simple Volatility", "EWMA"};

  VolatilityMethod(String value) {
    stringValue = value;
  }

  @Override
  public String toString() {
    return stringValue;
  }

  //Taken from https://stackoverflow.com/questions/604424/how-to-get-an-enum-value-from-a-string-value-in-java
  public static VolatilityMethod fromString(String value) {
    for(VolatilityMethod method : VolatilityMethod.values()) {
      if(method.stringValue.equalsIgnoreCase(value)) {
        return method;
      }
    }

    return null;
  }

}
