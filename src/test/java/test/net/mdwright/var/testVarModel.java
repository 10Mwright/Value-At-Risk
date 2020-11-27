package test.net.mdwright.var;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;
import net.mdwright.var.VarModel;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class testVarModel {

  VarModel var = new VarModel();

  @Before
  public void setup() {
    var = new VarModel();
  }

  @Test
  public void testSingleModelBuilding() {
    Portfolio portfolio = new Portfolio(new Position[]{new Position("GOOG", 1000000)});

    assertNotEquals(new BigDecimal(0.0), var.calculateVar(portfolio, 10, 0.99));
  }

  @Test
  public void testTwoModelBuilding() {
    Portfolio portfolio = new Portfolio(new Position[]{new Position("GOOG", 100000), new Position("TSLA", 1000000)});

    assertNotEquals(new BigDecimal(0), var.calculateVar(portfolio, 10, 0.99));
  }

}
