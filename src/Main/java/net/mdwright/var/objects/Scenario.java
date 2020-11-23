package net.mdwright.var.objects;

import java.math.BigDecimal;
import java.util.Calendar;

public class Scenario {

  private Calendar dateOne, dateTwo;
  private BigDecimal valueUnderScenario;

  public Scenario(Calendar dateOne, Calendar dateTwo, BigDecimal valueUnderScenario) {
    this.dateOne = dateOne;
    this.dateTwo = dateTwo;
    this.valueUnderScenario = valueUnderScenario;
  }

  public BigDecimal getValueUnderScenario() {
    return valueUnderScenario;
  }

  public Calendar getDateOne() {
    return dateOne;
  }

  public Calendar getDateTwo() {
    return dateTwo;
  }
}
