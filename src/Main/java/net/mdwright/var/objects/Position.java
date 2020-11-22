package net.mdwright.var.objects;

public class Position {

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

  public String toString() {
    return this.tickerSymbol + ", " + this.positionValue;
  }
}
