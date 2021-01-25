package net.mdwright.var;

import java.math.BigDecimal;
import java.math.RoundingMode;
import net.mdwright.var.application.ViewInterface;
import net.mdwright.var.objects.Model;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;

/**
 * Controller class for Var Calculations.
 *
 * Note: influence from Calculator code 2nd Year software Engineering Coursework
 *
 * @author Matthew Wright
 */
public class VarController {

  private boolean isModelBuilding = true;
  private VarModel model = new VarModel();
  private ViewInterface view;

  /**
   * Constructor method to assign a view to this controller and setup observing methods.
   *
   * @param view ViewInterface object representing the current user interface to be controlled
   */
  public VarController(ViewInterface view) {
    this.view = view;
    view.addCalcObserver(this::calculateVar); //Set observer to calculateVar method
    view.addPortfolioObserver(this::addAsset); //Set observer for add button to addAsset method
    view.addModelObserver(this::modelToUse); //Set observer for model selection to modelToUse method
  }

  /**
   * Observing method to call appropriate calculation method depending on user selected model.
   */
  public void calculateVar() {
    Portfolio portfolio = new Portfolio(view.getPortfolio());
    int timeHorizon = view.getTimeHorizon();
    double probability = view.getProbability();

    BigDecimal valueAtRisk;

    if(view.getDataLength() != 0) { //Datalength has been set
      isModelBuilding = false;
    }

    if (!isModelBuilding) {
      int dataLength = view.getDataLength();

      valueAtRisk = model.calculateVar(portfolio, timeHorizon, probability, dataLength);
    } else {
      valueAtRisk = model.calculateVar(portfolio, timeHorizon, probability);
    }

    //Rounding result to 2 decimal places
    valueAtRisk = valueAtRisk.setScale(2, RoundingMode.UP);

    view.setResult(valueAtRisk); //Set result in GUI
  }

  /**
   * Observing method to add a new position to the list view on the GUI.
   */
  public void addAsset() {
    Position newPos = view.getNewPosition();

    //Set in view
    view.addNewPosition(newPos);
  }

  /**
   * Observing method to switch between calculation models.
   *
   * @param modelToUse Model object containing the enum value to use
   */
  public void modelToUse(Model modelToUse) {
    if (modelToUse == Model.ModelBuilding) {
      isModelBuilding = true;
    } else {
      isModelBuilding = false;
    }
  }

}
