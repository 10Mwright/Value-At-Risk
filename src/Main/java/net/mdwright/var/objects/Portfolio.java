package net.mdwright.var.objects;

import java.math.BigDecimal;

/**
 * Object for storing a portfolio made up of Position objects.
 *
 * @author Matthew Wright
 */
public class Portfolio {

  private Position[] positions;
  private Scenario[] scenarios;
  private BigDecimal valueAtRisk;

  public Portfolio() {

  }

  public Portfolio(Position[] positions) {
    this.positions = positions;
  }

  public Position[] getPositions() {
    return positions;
  }

  public Position getPosition(int positionIndex) {
    return positions[positionIndex];
  }

  public int getSize() {
    return positions.length;
  }

  public Scenario[] getScenarios() {
    return scenarios;
  }

  public void setScenarios(Scenario[] scenarios) {
    this.scenarios = scenarios;
  }

  /**
   * Method for returning a sorted array of scenarios.
   *
   * @return Array of type Scenario containing a sorted array by value Code adapted from
   * https://stackoverflow.com/questions/33462923/sort-elements-of-an-array-in-ascending-order
   */
  public Scenario[] sortScenarios() {
    Scenario temp;

    for (int i = 0; i <= this.scenarios.length; i++) {
      for (int j = i + 1; j < this.scenarios.length; j++) {
        if (this.scenarios[i] != null && this.scenarios[j] != null) {
          if (this.scenarios[i].getValueUnderScenario()
              .compareTo(this.scenarios[j].getValueUnderScenario()) < 0) {
            temp = this.scenarios[i];
            this.scenarios[i] = this.scenarios[j];
            this.scenarios[j] = temp;
          }
        }
      }
    }
    return this.scenarios;
  }

  public BigDecimal getValueAtRisk() {
    return valueAtRisk;
  }

  public void setValueAtRisk(BigDecimal valueAtRisk) {
    this.valueAtRisk = valueAtRisk;
  }
}
