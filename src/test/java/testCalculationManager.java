import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class testCalculationManager {

  @Test
  public void testSingleAsset() {
    CalculationManager calculation = new CalculationManager();
    assertEquals(2326.0, calculation.calculateVar(1.0, 100000), 0);
  }
}
