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
 *
 * API Used: https://financequotes-api.com/
 *
 * @author Matthew Wright
 */
public class DataManager {

  /**
   * Method to retrieve historical stock data from Yahoo Finance via API.
   *
   * @param position the requested stocks position object
   * @param dataLength An int value representing the number of days to go back for historical data
   * @return ArrayList of type HistoricalQuote
   * @throws IOException When a connection error occurs or the response is invalid
   */
  public static List<HistoricalQuote> getHistoricalPrices(Position position, int dataLength)
      throws IOException {
    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();
    startDate.add(Calendar.DAY_OF_YEAR, -dataLength);

    Stock target = YahooFinance.get(position.getTickerSymbol(), startDate, endDate, Interval.DAILY);

    String currencyFrom = target.getCurrency(); //Find the currency of the historical data
    String currencyTo = "GBP";

    BigDecimal exchangeRate = getFXQuote(currencyFrom,
        currencyTo); //Retrieve the exchange rate for above currencies
    System.out.println("EXCHANGE RATE: 1:" + exchangeRate);

    List<HistoricalQuote> historical = target.getHistory();

    for (int i = 0; i < historical.size(); i++) {
      BigDecimal temp = historical.get(i).getAdjClose()
          .multiply(exchangeRate); //Run through all adjusted close data to convert to GBP
      historical.get(i).setAdjClose(temp);
    }

    System.out.println(historical);
    position.setHistoricalData(historical);
    return historical;
  }

  /**
   * Method for retrieving foreign exchange quotes to perform currency exchange.
   *
   * @param fromCurrency String value representing the abbreviated currency to convert from (e.g.
   * USD)
   * @param toCurrency String value representing the abbreviated currency to convert to (e.g. GBP)
   * @return A BigDecimal value representing the conversion rate (e.g. 1 USD to 0.75 GBP would
   * return 0.75)
   * @throws IOException When a connection error occurs or the response is invalid
   */
  public static BigDecimal getFXQuote(String fromCurrency, String toCurrency) throws IOException {
    FxQuote exchange = YahooFinance.getFx(fromCurrency + toCurrency + "=X");

    return exchange.getPrice();
  }

  /**
   * Method for retrieving the current price of a position in GBP.
   * @param position The target position object
   * @return BigDecimal value representing the current GBP price
   */
  public static BigDecimal getCurrentQuote(Position position) {
    try {
      Stock stock = YahooFinance.get(position.getTickerSymbol());

      BigDecimal currentPrice = stock.getQuote().getPrice();

      currentPrice = currentPrice.multiply(getFXQuote(stock.getCurrency(), "GBP"));

      return currentPrice;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new BigDecimal(0);
  }

  /**
   * Method for retrieving the total price of the position.
   * @param position The target position object
   * @return BigDecimal value representing the current GBP price of the holdings
   */
  public static BigDecimal getCurrentValue(Position position) {
    BigDecimal currentPrice = getCurrentQuote(position); //Retrieves converted current price

    System.out.println("Value of " + position.getHoldings() + " quantity of position " + position.getTickerSymbol() + " is: " + currentPrice.multiply(new BigDecimal(position.getHoldings())));

    position.setPerUnitPrice(currentPrice);

    currentPrice = currentPrice.multiply(new BigDecimal(position.getHoldings()));

    position.setPositionValue(currentPrice);
    return currentPrice;
  }

  /**
   * Method for retrieving the current total value of the portfolio using previously retrieved current values.
   * @param portfolio The target portfolio object
   * @return BigDecimal value representing the current GBP price of all holdings in the portfolio
   */
  public static BigDecimal getCurrentPortfolioValue(Portfolio portfolio) {
    BigDecimal currentValue = new BigDecimal(0);

    for (int i = 0; i < portfolio.getSize(); i++) {
      currentValue = currentValue.add(portfolio.getPosition(i).getPositionValue());
    }

    portfolio.setCurrentValue(currentValue);
    return currentValue;
  }

}
