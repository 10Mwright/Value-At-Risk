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
  private BigDecimal currentValue;

  /**
   * Constructor method for creating a portfolio with multiple Positions.
   * @param positions Array of type Position containing all positions within the portfolio
   */
  public Portfolio(Position[] positions) {
    this.positions = positions;
  }

  /**
   * Constructor method for creating a portfolio with a single Position.
   * @param position A Position object representing the desired position
   */
  public Portfolio(Position position) {
    this.positions = new Position[]{position};
  }

  /**
   * Method for retrieving a specific Position from a portfolio of multiple positions.
   * @param positionIndex An int value representing the index of the desired position within the array
   * @return The Position object representing the desired position
   */
  public Position getPosition(int positionIndex) throws NullPointerException {
    if(positions.length != 0) { //Portfolio object isn't empty
      return positions[positionIndex];
    } else {
      throw new NullPointerException("Positions Array in Portfolio Object is Empty!");
    }
  }

  /**
   * Method for retrieving the size of the positions array.
   * @return An int value representing the size of the array
   */
  public int getSize() {
    return positions.length;
  }

  /**
   * Method for setting the scenarios array during historical sim calculations.
   * @param scenarios An array of type Scenario containing all scenarios for this portfolio
   */
  public void setScenarios(Scenario[] scenarios) {
    this.scenarios = scenarios;
  }

  /**
   * Method for returning a sorted array of scenarios.
   *
   * @return Array of type Scenario containing a sorted array by value (Descending)
   *
   * Code adapted from:
   * https://stackoverflow.com/questions/33462923/sort-elements-of-an-array-in-ascending-order
   */
  public Scenario[] sortScenarios() {
    Scenario temp;

    //Runs through each scenario and the scenarios proceeding it
    for (int i = 0; i <= this.scenarios.length; i++) {
      for (int j = i + 1; j < this.scenarios.length; j++) {
        if (this.scenarios[i] != null && this.scenarios[j] != null) {
          //Compares the values, if lower then swap around
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

  /**
   * Method for retrieving the VaR value for this portfolio.
   * @return A BigDecimal value representing the VaR value
   */
  public BigDecimal getValueAtRisk() {
    return valueAtRisk;
  }

  /**
   * Method for setting the VaR value for this portfolio.
   * @param valueAtRisk A BigDecimal value representing the VaR value
   */
  public void setValueAtRisk(BigDecimal valueAtRisk) {
    this.valueAtRisk = valueAtRisk;
  }

  public BigDecimal getCurrentValue() {
    return currentValue;
  }

  public void setCurrentValue(BigDecimal currentValue) {
    this.currentValue = currentValue;
  }
}
