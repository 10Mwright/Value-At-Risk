import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class testCalculationManager {

  @Test
  public void testSingleAsset() {
    CalculationManager calculation = new CalculationManager();
    assertNotEquals(new BigDecimal(0.0), calculation.calculateVar("GOOG", 1000000));
  }
}
