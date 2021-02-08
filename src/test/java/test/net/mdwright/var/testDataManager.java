package test.net.mdwright.var;

import static org.junit.Assert.assertNotEquals;

import java.math.BigDecimal;
import net.mdwright.var.DataManager;
import net.mdwright.var.HistoricalSimVar;
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

}
