package test.net.mdwright.var;

import static org.junit.Assert.fail;

import java.io.IOException;
import net.mdwright.var.Backtest;
import net.mdwright.var.DataManager;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import org.junit.Test;

/*
 Test class to initiate backtesting
 */
public class testBacktest {

  @Test
  public void testGetChanges() { //Testing the get changes method for a single asset portfolio
    Position position = new Position("GOOGL", 100);
    Portfolio portfolio = new Portfolio(position);

    try {
      DataManager.getHistoricalPrices(portfolio, 1000);

      Backtest.getChangesInValue(portfolio);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testDoTests() { //Entry into performing backtesting
    Backtest.doTests();
  }

}
