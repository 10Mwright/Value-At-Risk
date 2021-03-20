package test.net.mdwright.var;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import net.mdwright.var.DataManager;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import org.junit.Before;
import org.junit.Test;

/*
 Testing class for the DataManager class
 */
public class testDataManager {

  DataManager data = new DataManager();

  @Before
  public void setup() {
    data = new DataManager();
  }

  @Test
  public void testGetData() { //Testing to ensure data is capable of being gathered
    Position position = new Position("GOOGL", 100);
    Portfolio portfolio = new Portfolio(position);

    try {
      Calendar startDate = Calendar.getInstance();
      Calendar endDate = Calendar.getInstance();
      startDate.add(Calendar.DAY_OF_YEAR, -252);

      data.getHistoricalPrices(portfolio, startDate, endDate);

      assertNotNull(position.getHistoricalData()); //Data isn't empty
      assertNotEquals(0, position.getHistoricalDataSize());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testCurrentQuote() { //Testing the current quote method works and returns a value
    Position position = new Position("GOOGL", 10);

    assertNotEquals(new BigDecimal(0), data.getCurrentQuote(position));
  }

  @Test
  public void testCurrentValue() { //Testing current value method works and returns a value
    Position position = new Position("GOOGL", 10);

    assertNotEquals(new BigDecimal(0), data.getCurrentValue(position));
  }

  @Test
  public void testFxQuote() { //Testing to ensure Fx Quote method works and returns a value
    try {
      assertNotEquals(0, data.getFxQuote("USD", "GBP"));
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testCurrentPortfolioValue() { //Testing current portfolio value method works
    Position positionOne = new Position("GOOGL", 100);
    Position positionTwo = new Position("TLSA", 1000);

    //Must call current value method to fill in values
    data.getCurrentValue(positionOne);
    data.getCurrentValue(positionTwo);

    Portfolio portfolio = new Portfolio(new Position[] {positionOne, positionTwo});

    assertNotEquals(new BigDecimal(0), data.getCurrentPortfolioValue(portfolio));
  }

  @Test
  public void testValidStock() {
    try {
      assertTrue(data.testStockIsValid("GOOGL"));
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testInvalidStock() {
    try {
      assertFalse(data.testStockIsValid("INVALID"));
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

}
