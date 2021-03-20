package test.net.mdwright.var;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.math.BigDecimal;
import javax.xml.crypto.Data;
import net.mdwright.var.DataManager;
import net.mdwright.var.VarModel;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import net.mdwright.var.objects.VolatilityMethod;
import org.junit.Before;
import org.junit.jupiter.api.Test;

/*
 Test class for VarModel object
 */
public class testVarModel {

  VarModel var = new VarModel();

  @Before
  public void setup() {
    var = new VarModel();
  }

  @Test
  public void testSingleModelBuilding() { //Testing to ensure model-building passthrough works
    Portfolio portfolio = new Portfolio(new Position[]{new Position("GOOG", 100)});

    try {
      DataManager.getHistoricalPrices(portfolio, 252);

      assertNotEquals(new BigDecimal(0.0), var.calculateVar(portfolio, 10, 0.99, VolatilityMethod.EWMA));
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testTwoModelBuilding() { //Testing to ensure dual asset model-building works
    Portfolio portfolio = new Portfolio(new Position[]{new Position("GOOG", 100), new Position("TSLA", 10)});

    try {
      DataManager.getHistoricalPrices(portfolio, 252);

      assertNotEquals(new BigDecimal(0), var.calculateVar(portfolio, 10, 0.99, VolatilityMethod.EWMA));
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testHistorical() { //Testing to ensure historical sim passthrough works
    Position position = new Position("GOOGL", 10);
    Portfolio portfolio = new Portfolio(position);

    try {
      DataManager.getHistoricalPrices(portfolio, 252);

      assertNotEquals(new BigDecimal(0), var.calculateVar(portfolio, 10, 0.99, 252));
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

}
