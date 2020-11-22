package net.mdwright.var.objects;

public class Position {

  /*
  TODO: Implement currency conversion
   */

  private String tickerSymbol = "";
  private double positionValue = 0.0;

  public Position(String tickerSymbol, double positionValue) {
    this.tickerSymbol = tickerSymbol;
    this.positionValue = positionValue;
  }


  public String getTickerSymbol() {
    return tickerSymbol;
  }

  public double getPositionValue() {
    return positionValue;
  }

  @Override
  public String toString() {
    return "Ticker Symbol = " + this.tickerSymbol + ", Position Value = £" + this.positionValue;
  }
}
