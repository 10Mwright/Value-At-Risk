package net.mdwright.var;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.IO;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.fx.FxQuote;

public class DataManager {

  /**
   * Method to retrieve historical stock data from Yahoo Finance via API.
   *
   * @param tickerSymbol the requested stocks ticker symbol
   * @return ArrayList of type HistoricalQuote
   * @throws IOException When a connection error occurs
   */
  public static List<HistoricalQuote> getHistoricalPrices(String tickerSymbol, int dataLength) throws IOException {
    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();
    startDate.add(Calendar.DAY_OF_YEAR, -dataLength); //We'll initially only consider the previous year of data

    Stock target = YahooFinance.get(tickerSymbol, startDate, endDate, Interval.DAILY);

    String currencyFrom = target.getCurrency();
    String currencyTo = "GBP";

    BigDecimal exchangeRate = getFXQuote(currencyFrom, currencyTo);
    System.out.println("EXCHANGE RATE: " + exchangeRate);

    List<HistoricalQuote> historical = target.getHistory();

    for(int i = 0; i < historical.size(); i++) {
      BigDecimal temp = historical.get(i).getAdjClose().multiply(exchangeRate);
      historical.get(i).setAdjClose(temp);
    }

    System.out.println(historical);
    return historical;
  }

  public static BigDecimal getFXQuote(String fromCurrency, String toCurrency) throws IOException {
    FxQuote exchange = YahooFinance.getFx(fromCurrency + toCurrency + "=X");

    return exchange.getPrice();
  }

}
