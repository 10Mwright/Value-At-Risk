package test.net.mdwright.var;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import net.mdwright.var.DataManager;
import net.mdwright.var.EWMAVolatility;
import net.mdwright.var.SimpleVolatility;
import net.mdwright.var.VolatilityModel;
import net.mdwright.var.VolatilityModelFactory;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import net.mdwright.var.objects.VolatilityMethod;
import org.junit.Test;

/*
 Test class for VolatilityModelFactory
 */
public class testVolatilityModelFactory {

  @Test
  public void testSimpleVolatility() { //Testing to see if simple volatility works via factory
    Position testPosition = new Position("GOOGL", 1000);
    Portfolio testPortfolio = new Portfolio(new Position[] {testPosition});

    try {
      DataManager.getHistoricalPrices(testPortfolio, 252);

      VolatilityModel volCalculator = VolatilityModelFactory.getModel(VolatilityMethod.SIMPLE);

      double volatility = volCalculator.calculateVolatility(testPortfolio, 0);

      assertNotEquals(0, volatility);
      assertNotNull(testPosition.getVolatility());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  /*
  Code taken from //https://stackoverflow.com/questions/12404650/assert-an-object-is-a-specific-type
   */
  @Test
  public void testSimpleInstance() {
    VolatilityModel volCalculator = VolatilityModelFactory.getModel(VolatilityMethod.SIMPLE);

    assertThat(volCalculator, instanceOf(SimpleVolatility.class));
  }

  /*
  Code taken from //https://stackoverflow.com/questions/12404650/assert-an-object-is-a-specific-type
   */
  @Test
  public void testEwmaInstance() {
    VolatilityModel volCalculator = VolatilityModelFactory.getModel(VolatilityMethod.EWMA);

    assertThat(volCalculator, instanceOf(EWMAVolatility.class));
  }

  @Test
  public void testEWMAVolatility() { //Testing to see if EWMA volatility works via factory
    Position testPosition = new Position("GME", 10000);
    Portfolio testPortfolio = new Portfolio(new Position[] {testPosition});

    try {
      DataManager.getHistoricalPrices(testPortfolio, 252);

      VolatilityModel volCalculator = VolatilityModelFactory.getModel(VolatilityMethod.EWMA);

      double volatility = volCalculator.calculateVolatility(testPortfolio, 0);

      assertNotEquals(0, volatility);
      assertNotNull(testPosition.getVolatility());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testCalculateCovariancesSimple() { //Testing to see if simple covariances work
    Position testPositionOne = new Position("GOOGL", 100);
    Position testPositionTwo = new Position("GME", 100);
    Portfolio portfolio = new Portfolio(new Position[] {testPositionOne, testPositionTwo});

    try {
      DataManager.getHistoricalPrices(portfolio, 252);

      VolatilityModel volCalculator = VolatilityModelFactory.getModel(VolatilityMethod.SIMPLE);

      double[][] covariances = volCalculator.calculateCovarianceMatrix(portfolio);

      assertNotNull(covariances);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testCalculateCovariancesEWMA() { //Testing to see if EWMA covariances work
    Position testPositionOne = new Position("GOOGL", 100);
    Position testPositionTwo = new Position("GME", 100);
    Position testPositionThree = new Position("TSLA", 100);
    Portfolio portfolio = new Portfolio(new Position[] {testPositionOne, testPositionTwo, testPositionThree});

    try {
      DataManager.getHistoricalPrices(portfolio, 252);

      VolatilityModel volCalculator = VolatilityModelFactory.getModel(VolatilityMethod.EWMA);

      double[][] covariances = volCalculator.calculateCovarianceMatrix(portfolio);

      assertNotNull(covariances);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }
}
