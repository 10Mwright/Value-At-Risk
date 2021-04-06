package test.net.mdwright.var;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import net.mdwright.var.DataManager;
import net.mdwright.var.VarMath;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import org.junit.Test;

/*
 Test class for VarMath object
 */
public class testVarMath {

  @Test
  public void testMean() { //Testing to ensure calculateMean returns a valid value
    Position testPosition = new Position("TSLA", 1000);
    Portfolio portfolio = new Portfolio(testPosition);

    try {
      DataManager.getHistoricalPrices(portfolio, 252);

      BigDecimal mean = VarMath.calculateMean(testPosition);

      assertNotEquals(0, mean);
      assertNotNull(testPosition.getMeanPrice()); //Check position object has received this value
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testCoefficient() { //Testing to ensure calculateCoefficient returns a valid value
    Position testPosition = new Position("GOOGL", 10);
    Position testPositionTwo = new Position("TSLA", 100);
    Portfolio portfolio = new Portfolio(new Position[] {testPosition, testPositionTwo});

    try {
      DataManager.getHistoricalPrices(portfolio, 252);

      double coefficientCorr = VarMath.calculateCoefficient(testPosition, testPositionTwo);

      assertNotEquals(0, coefficientCorr);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testPercentageChanges() { //Testing to ensure getPercentageChanges returns valid
    Position testPosition = new Position("GOOGL", 10);
    Portfolio portfolio = new Portfolio(testPosition);

    try {
      DataManager.getHistoricalPrices(portfolio, 252);

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

  @Test
  public void testSmallestDataset() { //Testing to ensure the smallest dataset method works
    Position testPositionOne = new Position("GOOGL", 100);
    Position testPositionTwo = new Position("TSLA", 100);
    Portfolio portfolio = new Portfolio(new Position[] {testPositionOne, testPositionTwo});

    try {
      DataManager.getHistoricalPrices(portfolio, 252);

      int expectedSize = portfolio.getPosition(0).getHistoricalDataSize();

      for (int i = 0; i < portfolio.getSize(); i++) {
        if(portfolio.getPosition(i).getHistoricalDataSize() < expectedSize) {
          expectedSize = portfolio.getPosition(i).getHistoricalDataSize();
        }
      }

      int returnedSize = VarMath.getSmallestDatasetSize(portfolio);

      assertEquals(expectedSize - 1, returnedSize);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testGetHashMaps() { //Testing to ensure valid hashMaps are returned from getHashMaps
    Position testPositionOne = new Position("GOOGL", 100);
    Position testPositionTwo = new Position("TSLA", 100);
    Portfolio portfolio = new Portfolio(new Position[] {testPositionOne, testPositionTwo});

    try {
      DataManager.getHistoricalPrices(portfolio, 252);

      List<Map<String, BigDecimal>> portfolioPriceMaps = VarMath.getHashMaps(portfolio);

      assertFalse(portfolioPriceMaps.isEmpty());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

}
