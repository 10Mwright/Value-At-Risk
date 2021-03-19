package test.net.mdwright.var;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import net.mdwright.var.DataManager;
import net.mdwright.var.EWMAVolatility;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import org.junit.Test;

/*
 Testing class for EWMAVolatility object
 */
public class testEWMAVolatility {

  @Test
  public void testEWMAVolatility() { //testing to ensure getVolatility returns a valid value
    Position testPosition = new Position("GME", 10);
    Portfolio portfolio = new Portfolio(new Position[] {testPosition});

    try {
      DataManager.getHistoricalPrices(testPosition, 252);

      EWMAVolatility volCalculator = new EWMAVolatility();

      double volatility = volCalculator.calculateVolatility(portfolio, 0);

      assertNotEquals(0, volatility);
      assertNotNull(testPosition.getVolatility());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }
}
