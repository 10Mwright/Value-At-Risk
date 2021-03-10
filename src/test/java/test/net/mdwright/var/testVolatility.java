package test.net.mdwright.var;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

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

    double volatility = volCalculator.calculateVolatility(testPosition);

    assertNotEquals(0, volatility);
    //Will need a new test for checking the Position object receives a volatility value as well
  }

}
