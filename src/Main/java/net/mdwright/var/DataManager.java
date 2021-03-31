package net.mdwright.var;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.fx.FxQuote;

/**
 * Class for retrieving historical data using the Yahoo Finance API
 * API Used: https://financequotes-api.com/
 *
 * @author Matthew Wright
 */
public class DataManager {

  private static final String localCurrency = "GBP"; //Currency converted to

  /**
   * Method to retrieve historical stock data from Yahoo Finance via API.
   *
   * @param portfolio The portfolio object to gather data for.
   * @param startDate Calendar object of the starting date to gather data from
   * @param endDate Calendar object of the ending date to gather data to
   * @throws IOException When a connection error occurs or the response is invalid
   */
  public static void getHistoricalPrices(Portfolio portfolio, Calendar startDate,
      Calendar endDate) throws IOException {

    for (int i = 0; i < portfolio.getSize(); i++) {
      Stock target = YahooFinance
          .get(portfolio.getPosition(i).getTickerSymbol(), startDate, endDate, Interval.DAILY);

      String currencyFrom = target.getCurrency(); //Find the currency of the historical data
      String currencyTo = localCurrency;

      BigDecimal exchangeRate = getFxQuote(currencyFrom,
          currencyTo); //Retrieve the exchange rate for above currencies
      System.out.println("EXCHANGE RATE: 1:" + exchangeRate);

      List<HistoricalQuote> historical = target.getHistory();

      for (int j = 0; j < historical.size(); j++) {
        BigDecimal temp = historical.get(j).getAdjClose()
            .multiply(exchangeRate); //Run through all adjusted close data to convert to GBP
        historical.get(i).setAdjClose(temp);
      }

      System.out.println(historical); //Print out historical data
      portfolio.getPosition(i).setHistoricalData(historical); //Transfer to position object
    }
  }

  /**
   * Method to retrieve historical stock data from Yahoo Finance via API using datalength.
   * @param portfolio The portfolio boject to gather data for.
   * @param dataLength Number of days to go back in time for data
   */
  public static void getHistoricalPrices(Portfolio portfolio, int dataLength) throws IOException {
    Calendar startDate = Calendar.getInstance();
    startDate.add(Calendar.DAY_OF_YEAR, -dataLength);
    Calendar endDate = Calendar.getInstance();

    getHistoricalPrices(portfolio, startDate, endDate);
  }

  /**
   * Method for retrieving foreign exchange quotes to perform currency exchange.
   *
   * @param fromCurrency String value representing the abbreviated currency to convert from (e.g.
   *     USD)
   * @param toCurrency String value representing the abbreviated currency to convert to (e.g. GBP)
   * @return A BigDecimal value representing the conversion rate (e.g. 1 USD to 0.75 GBP would
   *     return 0.75)
   * @throws IOException When a connection error occurs or the response is invalid
   */
  public static BigDecimal getFxQuote(String fromCurrency, String toCurrency) throws IOException {
    FxQuote exchange = YahooFinance.getFx(fromCurrency + toCurrency + "=X");

    return exchange.getPrice();
  }

  /**
   * Method for retrieving the current price per unit of an asset in GBP.
   * @param position The target position object
   * @return BigDecimal value representing the current GBP price
   */
  public static BigDecimal getCurrentQuote(Position position) {
    try {
      Stock stock = YahooFinance.get(position.getTickerSymbol());

      BigDecimal currentPrice = stock.getQuote().getPrice(); //Gets current price per unit

      // Exchange this price into GBP
      currentPrice = currentPrice.multiply(getFxQuote(stock.getCurrency(), localCurrency));

      return currentPrice;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new BigDecimal(0);
  }

  /**
   * Method for retrieving the cumulative price of a position.
   * @param position The target position object
   * @return BigDecimal value representing the current GBP price of the holdings
   */
  public static BigDecimal getCurrentValue(Position position) {
    BigDecimal currentPrice = getCurrentQuote(position); //Retrieves converted current price

    System.out.println("Value of " + position.getHoldings() + " quantity of position "
        + position.getTickerSymbol() + " is: " + currentPrice.multiply(new
        BigDecimal(position.getHoldings())));

    currentPrice = currentPrice.multiply(new BigDecimal(position.getHoldings()));

    position.setPositionValue(currentPrice);
    return currentPrice;
  }

  /**
   * Method for retrieving the current total value of the portfolio using previously retrieved
   *     current values.
   * @param portfolio The target portfolio object
   * @return BigDecimal value representing the current GBP price of all holdings in the portfolio
   */
  public static BigDecimal getCurrentPortfolioValue(Portfolio portfolio) {
    BigDecimal currentValue = new BigDecimal(0);

    for (int i = 0; i < portfolio.getSize(); i++) { //Run through entire portfolio
      currentValue = currentValue.add(portfolio.getPosition(i).getPositionValue());
    }

    portfolio.setCurrentValue(currentValue);
    return currentValue;
  }

  /**
   * Method for checking if a provided tickerSymbol is a valid symbol.
   * @param tickerSymbol String representation of the stock's ticker symbol
   * @return Boolean value, true if the stock exists and false if it doesn't
   * @throws IOException When there is a connection error
   */
  public static boolean testStockIsValid(String tickerSymbol) throws IOException {
    Stock targetStock = YahooFinance.get(tickerSymbol);

    if (targetStock == null) { //Stock object came back with null
      return false;
    } else { //Stock object came back with data
      return true;
    }
  }

}
