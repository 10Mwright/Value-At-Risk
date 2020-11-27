package net.mdwright.var;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
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
   * @param tickerSymbol the requested stocks ticker symbol
   * @return ArrayList of type HistoricalQuote
   * @throws IOException When a connection error occurs or the response is invalid
   */
  public static List<HistoricalQuote> getHistoricalPrices(String tickerSymbol, int dataLength)
      throws IOException {
    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();
    startDate.add(Calendar.DAY_OF_YEAR,
        -dataLength); //We'll initially only consider the previous year of data

    Stock target = YahooFinance.get(tickerSymbol, startDate, endDate, Interval.DAILY);

    String currencyFrom = target.getCurrency(); //Find the currency of the historical data
    String currencyTo = "GBP";

    BigDecimal exchangeRate = getFXQuote(currencyFrom,
        currencyTo); //Retrieve the exchange rate for above currencies
    System.out.println("EXCHANGE RATE: " + exchangeRate);

    List<HistoricalQuote> historical = target.getHistory();

    for (int i = 0; i < historical.size(); i++) {
      BigDecimal temp = historical.get(i).getAdjClose()
          .multiply(exchangeRate); //Run through all adjusted close data to convert to GBP
      historical.get(i).setAdjClose(temp);
    }

    System.out.println(historical);
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

}
