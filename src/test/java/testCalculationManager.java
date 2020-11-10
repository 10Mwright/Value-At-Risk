import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import Objects.Position;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class testCalculationManager {

  @Test
  public void testSingleAsset() {
    CalculationManager calculation = new CalculationManager();
    assertNotEquals(new BigDecimal(0.0), calculation.calculateVar("GOOG", 1000000, 10, 0.99));

    System.out.println("----------------");
    assertNotEquals(new BigDecimal(0.0), calculation.calculateVar("TSLA", 10000000, 10, 0.95));
  }

  @Test
  public void testTwoAssets() {
    CalculationManager calculation = new CalculationManager();

    //Create each position object
    Position google = new Position("GOOG", 1000000);
    Position microsoft = new Position("MST", 10000000);

    assertNotEquals(new BigDecimal(0), calculation.calculateVar(google, microsoft, 10, 0.99));
  }
}
