package net.mdwright.var.objects;

public enum VolatilityMethod {
  SIMPLE("Simple Volatility"),
  EWMA("EWMA"),
  GARCH("GARCH(1,1)");

  private String stringValue;

  VolatilityMethod(String value) {
    stringValue = value;
  }

  @Override
  public String toString() {
    return stringValue;
  }

}
