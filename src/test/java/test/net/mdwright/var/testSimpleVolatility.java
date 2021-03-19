package test.net.mdwright.var;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import net.mdwright.var.DataManager;
import net.mdwright.var.SimpleVolatility;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import org.junit.Test;

/*
 Test class for SimpleVolatility object
 */
public class testSimpleVolatility {

  @Test
  public void testSimpleVolatility() { //Testing to ensure getVolatility returns a valid value
    Position testPosition = new Position("GOOGL", 1000);
    Portfolio testPortfolio = new Portfolio(new Position[] {testPosition});

    try {
      DataManager.getHistoricalPrices(testPosition, 252);

      SimpleVolatility volCalculator = new SimpleVolatility();

      double volatility = volCalculator.calculateVolatility(testPortfolio, 0);

      assertNotEquals(0, volatility);
      assertNotNull(testPosition.getVolatility());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }
}
