package test.net.mdwright.var;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import net.mdwright.var.DataManager;
import net.mdwright.var.Volatility;
import net.mdwright.var.objects.Position;
import org.junit.Before;
import org.junit.Test;

public class testVolatility {

  Volatility volCalculator;

  @Before
  public void setup() {
    volCalculator = new Volatility();
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

}
