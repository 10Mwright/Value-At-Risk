package test.net.mdwright.var;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import net.mdwright.var.DataManager;
import net.mdwright.var.VarMath;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import org.junit.Test;

public class testVarMath {

  @Test
  public void testMean() {
    Position testPosition = new Position("TSLA", 1000);

    try {
      DataManager.getHistoricalPrices(testPosition, 252);

      BigDecimal mean = VarMath.calculateMean(testPosition);

      assertNotEquals(0, mean);
      assertNotNull(testPosition.getMeanPrice());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testCoefficient() {
    Position testPosition = new Position("GOOGL", 10);
    Position testPositionTwo = new Position("TSLA", 100);

    try {
      DataManager.getHistoricalPrices(testPosition, 252);
      DataManager.getHistoricalPrices(testPositionTwo, 252);

      double coefficientCorr = VarMath.calculateCoefficient(testPosition, testPositionTwo);

      assertNotEquals(0, coefficientCorr);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testPercentageChanges() {
    Position testPosition = new Position("GOOGL", 10);
    Portfolio portfolio = new Portfolio(testPosition);

    try {
      DataManager.getHistoricalPrices(testPosition, 252);

      double[] percentageChanges = VarMath.getPercentageChanges(portfolio, 0);

      for (int i = 0; i < percentageChanges.length; i++) {
        System.out.println(percentageChanges[i]);
        assertNotNull(percentageChanges[i]);
      }
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

}
