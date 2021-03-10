package test.net.mdwright.var;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import net.mdwright.var.DataManager;
import net.mdwright.var.Varmath;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import org.junit.Before;
import org.junit.Test;

public class testVarmath {

  Varmath volCalculator;

  @Before
  public void setup() {
    volCalculator = new Varmath();
  }

  @Test
  public void testSimpleVolatility() {
    Position testPosition = new Position("GOOGL", 1000);

    try {
      DataManager.getHistoricalPrices(testPosition, 252);

      double volatility = volCalculator.calculateVolatility(testPosition);

      assertNotEquals(0, volatility);
      assertNotNull(testPosition.getVolatility());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testEWMAVolatility() {
    Position testPosition = new Position("GME", 10);
    Portfolio portfolio = new Portfolio(new Position[] {testPosition});

    try {
      DataManager.getHistoricalPrices(testPosition, 252);

      double volatility = volCalculator.calculateVolatility(portfolio, 0, 0.94);

      assertNotEquals(0, volatility);
      assertNotNull(testPosition.getVolatility());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testMean() {
    Position testPosition = new Position("TSLA", 1000);

    try {
      DataManager.getHistoricalPrices(testPosition, 252);

      BigDecimal mean = volCalculator.calculateMean(testPosition);

      assertNotEquals(0, mean);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

}
