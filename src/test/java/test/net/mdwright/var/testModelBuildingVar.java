package test.net.mdwright.var;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import net.mdwright.var.HistoricalSimVar;
import net.mdwright.var.ModelBuildingVar;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import java.math.BigDecimal;
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
    ModelBuildingVar calculation = new ModelBuildingVar();
    Portfolio portfolio = new Portfolio(new Position("GOOG", 1000000));
    assertNotEquals(new BigDecimal(0.0), calculation.calculateVar(portfolio, 10, 0.99));

    System.out.println("----------------");
    portfolio = new Portfolio(new Position("TSLA", 10000000));
    assertNotEquals(new BigDecimal(0.0), calculation.calculateVar(portfolio, 10, 0.95));
  }

  @Test
  public void testTwoAssets() {
    ModelBuildingVar calculation = new ModelBuildingVar();

    //Create each position object
    Position google = new Position("GOOG", 1000000);
    Position microsoft = new Position("MSFT", 10000000);

    assertNotEquals(new BigDecimal(0), calculation.calculateVar(google, microsoft, 10, 0.99));
  }
}
