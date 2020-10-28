public class CalculationManager {

  public static double calculateVar(double dailyVolatility, double assetValue){
    double dailyStandardDeviation = assetValue * (dailyVolatility / 100);

    double normSinV = 2.326; //For now we maintain this value i.e. a 99% VaR

    double singleDayVar = normSinV * dailyStandardDeviation;

    return singleDayVar;
  }

}
