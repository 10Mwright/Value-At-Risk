package net.mdwright.var.objects;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import yahoofinance.histquotes.HistoricalQuote;

public class Position {

  /*
  TODO: Implement currency conversion
   */

  private String tickerSymbol;
  private double positionValue;
  private List<HistoricalQuote> historicalData;
  private Scenario[] scenarios;

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

  public List<HistoricalQuote> getHistoricalData() {
    return historicalData;
  }

  public void setHistoricalData(
      List<HistoricalQuote> historicalData) {
    this.historicalData = historicalData;
  }

  public int getHistoricalDataSize() {
    return historicalData.size();
  }
}
