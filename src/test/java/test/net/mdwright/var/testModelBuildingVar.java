package test.net.mdwright.var;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.math.BigDecimal;
import net.mdwright.var.DataManager;
import net.mdwright.var.ModelBuildingVar;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import net.mdwright.var.objects.VolatilityMethod;
import org.junit.Before;
import org.junit.jupiter.api.Test;

public class testModelBuildingVar {

  ModelBuildingVar modelBuilding;

  @Before
  public void setup() {
    modelBuilding = new ModelBuildingVar();
  }

  @Test
  public void testSingleAsset() {
    Portfolio portfolio = new Portfolio(new Position("GOOG", 1000000));

    try {
      DataManager.getHistoricalPrices(portfolio, 252);

      assertNotEquals(new BigDecimal(0.0),
          modelBuilding.calculateVar(portfolio, 10, 0.99, VolatilityMethod.EWMA));

      System.out.println("----------------");
      portfolio = new Portfolio(new Position("TSLA", 10000000));
      assertNotEquals(new BigDecimal(0.0),
          modelBuilding.calculateVar(portfolio, 10, 0.95, VolatilityMethod.EWMA));
    }  catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testTwoAssets() {
    //Create each position object
    Position google = new Position("GOOG", 1000000);
    Position microsoft = new Position("MSFT", 10000000);

    //Create new portfolio object
    Portfolio portfolio = new Portfolio(new Position[] {google, microsoft});

    try {
      DataManager.getHistoricalPrices(portfolio, 252);

      assertNotEquals(new BigDecimal(0),
          modelBuilding.calculateVarDouble(portfolio, 10, 0.99, VolatilityMethod.EWMA));
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testSingleAssetSimple() {
    Portfolio portfolio = new Portfolio(new Position("GOOG", 100));

    try {
      DataManager.getHistoricalPrices(portfolio, 252);
      assertNotEquals(new BigDecimal(0.0), modelBuilding.calculateVar(portfolio,
          10, 0.99, VolatilityMethod.SIMPLE));
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testTwoAssetsSimple() {
    //Create each position object
    Position google = new Position("GOOG", 100);
    Position microsoft = new Position("MSFT", 100);

    //Create new portfolio object
    Portfolio portfolio = new Portfolio(new Position[] {google, microsoft});

    try {
      DataManager.getHistoricalPrices(portfolio, 252);
      assertNotEquals(new BigDecimal(0), modelBuilding.calculateVarDouble(portfolio,
          10, 0.99, VolatilityMethod.SIMPLE));
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testLinearSimple() {
    //Create each position object
    Position google = new Position("GOOG", 100);
    Position microsoft = new Position("MSFT", 1000);

    //Create new portfolio object
    Portfolio portfolio = new Portfolio(new Position[] {google, microsoft});

    try {
      DataManager.getHistoricalPrices(portfolio, 252);
      BigDecimal var = modelBuilding.calculateVarLinear(portfolio,
          10, 0.99, VolatilityMethod.EWMA);

      assertNotEquals(new BigDecimal(0), var);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }
}
