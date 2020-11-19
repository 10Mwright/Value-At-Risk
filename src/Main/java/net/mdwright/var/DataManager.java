package net.mdwright.var;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class DataManager {

  /**
   * Method to retrieve historical stock data from Yahoo Finance via API.
   * @param tickerSymbol the requested stocks ticker symbol
   * @return ArrayList of type HistoricalQuote
   * @throws IOException When a connection error occurs
   */
  public static List<HistoricalQuote> getHistoricalPrices(String tickerSymbol) throws IOException {
    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();
    startDate.add(Calendar.YEAR, -1); //We'll initially only consider the previous year of data

    Stock target = YahooFinance.get(tickerSymbol, startDate, endDate, Interval.DAILY);

    List<HistoricalQuote> historical = new ArrayList<HistoricalQuote>();

    historical = target.getHistory();

    System.out.println(historical);
    return historical;
  }

}
