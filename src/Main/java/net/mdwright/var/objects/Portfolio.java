package net.mdwright.var.objects;

/**
 * Object for storing a portfolio made up of Position objects.
 *
 * @author Matthew Wright
 */
public class Portfolio {

  private Position[] positions;

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
}
