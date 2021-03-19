package net.mdwright.var.objects;

import java.math.BigDecimal;

/**
 * Object for storing a portfolio made up of Position objects.
 *
 * @author Matthew Wright
 */
public class Portfolio {

  private Position[] positions;
  private BigDecimal valueAtRisk;
  private BigDecimal currentValue;
  private double volatilityLambda = 0.94;

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
   * @param positionIndex An int value representing the index of the desired position
   *     within the array
   * @return The Position object representing the desired position
   */
  public Position getPosition(int positionIndex) throws NullPointerException {
    if (positions.length != 0) { //Portfolio object isn't empty
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

  public double getVolatilityLambda() {
    return volatilityLambda;
  }

  public void setVolatilityLambda(double volatilityLambda) {
    this.volatilityLambda = volatilityLambda;
  }
}
