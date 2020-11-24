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

  public Scenario[] getScenarios() {
    return scenarios;
  }

  public void setScenarios(Scenario[] scenarios) {
    this.scenarios = scenarios;
  }

  /**
   * Method for returning a sorted array of scenarios.
   *
   * @return Array of type Scenario containing a sorted array by value Code adapted from
   * https://stackoverflow.com/questions/33462923/sort-elements-of-an-array-in-ascending-order
   */
  public Scenario[] sortScenarios() {
    Scenario temp;

    for (int i = 0; i <= this.scenarios.length; i++) {
      for (int j = i + 1; j < this.scenarios.length; j++) {
        if (this.scenarios[i] != null && this.scenarios[j] != null) {
          if (this.scenarios[i].getValueUnderScenario()
              .compareTo(this.scenarios[j].getValueUnderScenario()) > 0) {
            temp = this.scenarios[i];
            this.scenarios[i] = this.scenarios[j];
            this.scenarios[j] = temp;
          }
        }
      }
    }
    return this.scenarios;
  }
}
