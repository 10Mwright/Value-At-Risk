package net.mdwright.var.application;

import java.math.BigDecimal;
import java.util.Observer;
import java.util.function.Consumer;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Position;

/**
 * Interface class for GUI Views. Influence taken from 2nd Year Software Engineering Calculator
 * Coursework.
 *
 * @author Matthew Wright
 */
public interface ViewInterface {

  Position[] getPortfolio();

  void setResult(BigDecimal varValue);

  void addCalcObserver(Observer obs);

  void addModelObserver(Consumer<Model> model);

}
