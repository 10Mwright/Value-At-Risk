package net.mdwright.var.objects;

import java.util.ArrayList;
import yahoofinance.histquotes.HistoricalQuote;

public class Position {

  /*
  TODO: Implement currency conversion
   */

  private String tickerSymbol = "";
  private double positionValue = 0.0;
  private ArrayList<HistoricalQuote> historicalData;

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

  public ArrayList<HistoricalQuote> getHistoricalData() {
    return historicalData;
  }

  public void setHistoricalData(
      ArrayList<HistoricalQuote> historicalData) {
    this.historicalData = historicalData;
  }
}
