package net.mdwright.var;

import java.math.BigDecimal;
import net.mdwright.var.application.ViewInterface;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Position;

/**
 * Controller class for Var Calculations. Note: influence from Calculator code 2nd Year software
 * Engineering Coursework
 *
 * @author Matthew Wright
 */
public class VarController {

  private boolean isModelBuilding;
  private VarModel model = new VarModel();
  private ViewInterface view = null;

  public VarController(ViewInterface view) {
    this.view = view;
    view.addCalcObserver(this::calculateVar); //Set observer to calculateVar method
  }

  public void calculateVar() {
    //Get data from view
    Position[] portfolio = view.getPortfolio();
    int timeHorizon = view.getTimeHorizon();
    double probability = view.getProbability();

    BigDecimal valueAtRisk = model.calculateVar(portfolio, timeHorizon, probability);

    view.setResult(valueAtRisk);
    //Pass to VarModel's method
  }

  public void modelToUse(Model modelToUse) {
    if (modelToUse == Model.ModelBuilding) {
      isModelBuilding = true;
    } else {
      isModelBuilding = false;
    }
  }

}
