package net.mdwright.var;

import java.util.List;
import net.mdwright.var.objects.Position;
import yahoofinance.histquotes.HistoricalQuote;

public class Volatility {

  public static double calculateVolatility(Position position) {
    List<HistoricalQuote> historicalData = position.getHistoricalData();
  }
}
