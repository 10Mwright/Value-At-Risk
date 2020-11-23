package net.mdwright.var.objects;

import java.math.BigDecimal;
import java.util.Calendar;

public class Scenario {

  private Calendar date;
  private BigDecimal valueUnderScenario;

  public Scenario(Calendar date, BigDecimal valueUnderScenario) {
    this.date = date;
    this.valueUnderScenario = valueUnderScenario;
  }

}
