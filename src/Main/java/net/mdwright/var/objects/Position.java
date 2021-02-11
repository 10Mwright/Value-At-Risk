package net.mdwright.var.objects;

import java.util.List;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Object for storing information about a position within a portfolio.
 *
 * @author Matthew Wright
 */
public class Position {

  private String tickerSymbol;
  private double positionValue;
  private double holdings;
  private List<HistoricalQuote> historicalData;

  /**
   * Constructor method for creating a new Position object.
   *
   * @param tickerSymbol String value representing the abbreviated stock symbol (e.g. GOOGL)
   * @param holdings Int value representing the total number of holdings (e.g. number of stocks held)
   */
  public Position(String tickerSymbol, double holdings) {
    this.tickerSymbol = tickerSymbol;
    this.holdings = holdings;
  }

  /**
   * Method for retrieving the ticker symbol (e.g. GOOGL) for this position.
   *
   * @return String value representing the abbreviated stock symbol
   */
  public String getTickerSymbol() {
    return tickerSymbol;
  }

  /**
   * Method for retrieving the total value of the position in £
   *
   * @return Double value representing the total value of the position
   */
  public double getPositionValue() {
    return positionValue;
  }

  /**
   * Method for converting a position into string format for easy GUI display.
   *
   * @return String value representing the position's fields.
   */
  @Override
  public String toString() {
    return "Ticker Symbol = " + this.tickerSymbol + ", Position Value = £" + this.positionValue;
  }

  /**
   * Method for retrieving a position's stored historical data.
   *
   * @return List of type HistoricalQuote containing all historical data
   */
  public List<HistoricalQuote> getHistoricalData() {
    return historicalData;
  }

  /**
   * Method for setting a position's historical data on retrieval.
   *
   * @param historicalData List of type HistoricalQuote containing all historical data
   */
  public void setHistoricalData(
      List<HistoricalQuote> historicalData) {
    this.historicalData = historicalData;
  }

  /**
   * Method for retrieving the size of the position's historical data list.
   *
   * @return An int value representing the size of the list
   */
  public int getHistoricalDataSize() {
    return historicalData.size();
  }
}
