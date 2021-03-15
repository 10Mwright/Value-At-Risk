package test.net.mdwright.var;

import static org.junit.Assert.assertNotEquals;

import java.math.BigDecimal;
import net.mdwright.var.DataManager;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import org.junit.Before;
import org.junit.Test;

public class testDataManager {

  DataManager data = new DataManager();

  @Before
  public void setup() {
    data = new DataManager();
  }

  @Test
  public void testCurrentQuote() {
    Position position = new Position("GOOGL", 10);

    assertNotEquals(new BigDecimal(0), data.getCurrentQuote(position));
  }

  @Test
  public void testCurrentValue() {
    Position position = new Position("GOOGL", 10);

    assertNotEquals(new BigDecimal(0), data.getCurrentValue(position));
  }

  @Test
  public void testCurrentPortfolioValue() {
    Position positionOne = new Position("GOOGL", 100);
    Position positionTwo = new Position("TLSA", 1000);

    //Must call current value method to fill in values
    data.getCurrentValue(positionOne);
    data.getCurrentValue(positionTwo);

    Portfolio portfolio = new Portfolio(new Position[] {positionOne, positionTwo});

    assertNotEquals(new BigDecimal(0), data.getCurrentPortfolioValue(portfolio));
  }

}
