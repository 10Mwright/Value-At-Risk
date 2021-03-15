package test.net.mdwright.var;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import net.mdwright.var.DataManager;
import net.mdwright.var.EWMAVolatility;
import net.mdwright.var.SimpleVolatility;
import net.mdwright.var.VolatilityModel;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import org.junit.Test;

public class testVolatilityModel {

  @Test
  public void testSimpleVolatility() {
    Position testPosition = new Position("GOOGL", 1000);
    Portfolio testPortfolio = new Portfolio(new Position[] {testPosition});

    try {
      DataManager.getHistoricalPrices(testPosition, 252);

      VolatilityModel volCalculator = new SimpleVolatility();

      double volatility = volCalculator.calculateVolatility(testPortfolio, 0);

      assertNotEquals(0, volatility);
      assertNotNull(testPosition.getVolatility());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testEWMAVolatility() {
    Position testPosition = new Position("TSLA", 100);
    Portfolio testPortfolio = new Portfolio(new Position[] {testPosition});

    try {
      DataManager.getHistoricalPrices(testPosition, 252);

      VolatilityModel volCalculator = new EWMAVolatility();

      double volatility = volCalculator.calculateVolatility(testPortfolio, 0);

      assertNotEquals(0, volatility);
      assertNotNull(testPosition.getVolatility());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

}