package test.net.mdwright.var;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import net.mdwright.var.ModelBuildingVar;
import net.mdwright.var.objects.Position;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class testModelBuildingVar {

  @Test
  public void testSingleAsset() {
    ModelBuildingVar calculation = new ModelBuildingVar();
    assertNotEquals(new BigDecimal(0.0), calculation.calculateVar("GOOG", 1000000, 10, 0.99));

    System.out.println("----------------");
    assertNotEquals(new BigDecimal(0.0), calculation.calculateVar("TSLA", 10000000, 10, 0.95));
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
