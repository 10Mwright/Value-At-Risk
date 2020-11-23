package net.mdwright.var;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    view.addPortfolioObserver(this::addAsset); //Set observer for add button to addAsset method
  }

  public void calculateVar() {
    Position[] portfolio = view.getPortfolio();
    int timeHorizon = view.getTimeHorizon();
    double probability = view.getProbability();

    BigDecimal valueAtRisk = model.calculateVar(portfolio, timeHorizon, probability);

    //Rounding result to 2 decimal places
    valueAtRisk = valueAtRisk.setScale(2, RoundingMode.UP);

    view.setResult(valueAtRisk);
  }

  public void addAsset() {
    Position newPos = view.getNewPosition();

    //Set in view
    view.addNewPosition(newPos);
  }

  public void modelToUse(Model modelToUse) {
    if (modelToUse == Model.ModelBuilding) {
      isModelBuilding = true;
    } else {
      isModelBuilding = false;
    }
  }

}
