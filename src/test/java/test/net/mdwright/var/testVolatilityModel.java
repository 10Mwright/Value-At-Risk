package test.net.mdwright.var;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
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
 Test class for VolatilityModel object
 */
public class testVolatilityModel {

  @Test
  public void testSimpleVolatility() { //Testing to ensure simple volatility returns a value
    Position testPosition = new Position("GOOGL", 1000);
    Portfolio testPortfolio = new Portfolio(new Position[] {testPosition});

    try {
      DataManager.getHistoricalPrices(testPortfolio, 252);

      VolatilityModel volCalculator = new SimpleVolatility();

      double volatility = volCalculator.calculateVolatility(testPortfolio, 0);

      assertNotEquals(0, volatility);
      assertNotNull(testPosition.getVolatility());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testEWMAVolatility() { //Testing to see if ewma volatility returns a value
    Position testPosition = new Position("TSLA", 100);
    Portfolio testPortfolio = new Portfolio(new Position[] {testPosition});

    try {
      DataManager.getHistoricalPrices(testPortfolio, 252);

      VolatilityModel volCalculator = new EWMAVolatility();

      double volatility = volCalculator.calculateVolatility(testPortfolio, 0);

      assertNotEquals(0, volatility);
      assertNotNull(testPosition.getVolatility());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testCalculateCovariancesSimple() { //Testing to see if covariances work (simple)
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
  public void testCalculateCovariancesEWMA() { //Testing to see if covariances work (EWMA)
    Position testPositionOne = new Position("GOOGL", 100);
    Position testPositionTwo = new Position("GME", 100);
    Portfolio portfolio = new Portfolio(new Position[] {testPositionOne, testPositionTwo});

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
