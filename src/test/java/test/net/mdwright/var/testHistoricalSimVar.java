package test.net.mdwright.var;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import net.mdwright.var.DataManager;
import net.mdwright.var.HistoricalSimVar;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import org.junit.Before;
import org.junit.Test;

/*
 Test class for HistoricalSimVar object
 */
public class testHistoricalSimVar {

  HistoricalSimVar historicalSim;

  @Before
  public void setup() {
    historicalSim = new HistoricalSimVar();
  }

  @Test
  public void testSingleAsset() { //Testing single asset calculations
    Portfolio portfolio = new Portfolio(new Position[]{new Position("GOOGL", 100000)});

    try {
      DataManager.getHistoricalPrices(portfolio, 252);
      assertNotEquals(0, historicalSim.calculateVar(portfolio,
          10, 0.99, 252));
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testMultiAsset() { //Testing multi asset calculations
    Portfolio portfolio = new Portfolio(new Position[]{new Position("GOOGL", 100000), new Position("TSLA", 10000)});

    assertNotEquals(0, historicalSim.calculateVar(portfolio, 10, 0.99, 252));
  }


}
