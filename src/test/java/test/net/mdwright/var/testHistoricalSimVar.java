package test.net.mdwright.var;

import static org.junit.Assert.assertNotEquals;

import net.mdwright.var.HistoricalSimVar;
import net.mdwright.var.VarModel;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import org.junit.Before;
import org.junit.Test;

public class testHistoricalSimVar {

  HistoricalSimVar historicalSim;

  @Before
  public void setup() {
    historicalSim = new HistoricalSimVar();
  }

  @Test
  public void testSingleAsset() {
    Portfolio portfolio = new Portfolio(new Position[]{new Position("GOOGL", 100000)});

    assertNotEquals(0, historicalSim.calculateVar(portfolio, 10, 0.99, 252));
  }


}
