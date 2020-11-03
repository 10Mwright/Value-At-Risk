import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class testCalculationManager {

  @Test
  public void testSingleAsset() {
    CalculationManager calculation = new CalculationManager();
    assertNotEquals(0, calculation.calculateVar("GOOG", 1000000), 0);
  }
}
