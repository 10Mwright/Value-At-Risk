package test.net.mdwright.var;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.math.BigDecimal;
import net.mdwright.var.DataManager;
import net.mdwright.var.MonteCarloVar;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import net.mdwright.var.objects.VolatilityMethod;
import org.junit.Before;
import org.junit.Test;

public class testMonteCarloVar {

  MonteCarloVar monteCarlo;

  @Before
  public void setup() {
    monteCarlo = new MonteCarloVar();
  }

  @Test
  public void testMonteCarloSimple() {
    Position testPositionOne = new Position("GOOGL", 1000);
    Position testPositionTwo = new Position("TSLA", 100);
    Portfolio portfolio = new Portfolio(new Position[] {testPositionOne, testPositionTwo});

    try {
      DataManager.getHistoricalPrices(testPositionOne, 252);
      DataManager.getHistoricalPrices(testPositionTwo, 252);

      BigDecimal valueAtRisk = monteCarlo.calculateVar(portfolio, 10, 0.99, VolatilityMethod.SIMPLE);

      assertNotEquals(0, valueAtRisk);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

}
