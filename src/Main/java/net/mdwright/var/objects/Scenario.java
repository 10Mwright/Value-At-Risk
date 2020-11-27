package net.mdwright.var.objects;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Object for storing information for each scenario between two dates.
 *
 * @author Matthew Wright
 */
public class Scenario {

  private Calendar dateOne, dateTwo;
  private BigDecimal valueUnderScenario;

  /**
   * Constructor method for constructing a Scenario with to and from dates and it's respective
   * gain/loss.
   *
   * @param dateOne A Calendar object containing the from date
   * @param dateTwo A Calendar object containing the to date
   * @param valueUnderScenario A BigDecimal value representing the gain or loss during this
   * scenarios period
   */
  public Scenario(Calendar dateOne, Calendar dateTwo, BigDecimal valueUnderScenario) {
    this.dateOne = dateOne;
    this.dateTwo = dateTwo;
    this.valueUnderScenario = valueUnderScenario;
  }

  /**
   * Method for retrieving the gain or loss that occurred in this scenario.
   *
   * @return A BigDecimal representing the gain or loss during this scenarios period
   */
  public BigDecimal getValueUnderScenario() {
    return valueUnderScenario;
  }

  /**
   * Method for retrieving the from date of this scenario.
   *
   * @return A Calendar object representing the from date
   */
  public Calendar getDateOne() {
    return dateOne;
  }

  /**
   * Method for retrieving the to date of this scenario.
   *
   * @return A Calendar object representing the to date
   */
  public Calendar getDateTwo() {
    return dateTwo;
  }
}
