package net.mdwright.var;

import net.mdwright.var.objects.Model;

public class VarController {

  private boolean isModelBuilding;
  private VarModel model = new VarModel();

  public void calculateVar() {
    //Get data from view

    //Pass to VarModel's method
  }

  public void modelToUse(Model modelToUse) {
    if(modelToUse == Model.ModelBuilding) {
      isModelBuilding = true;
    } else {
      isModelBuilding = false;
    }
  }

}
