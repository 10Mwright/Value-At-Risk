package test.net.mdwright.var;

import static junit.framework.TestCase.fail;

import java.io.IOException;
import net.mdwright.var.DataManager;
import net.mdwright.var.MonteCarloVar;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
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
      

    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

}
