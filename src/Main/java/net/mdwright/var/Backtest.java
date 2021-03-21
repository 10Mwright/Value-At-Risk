package net.mdwright.var;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import net.mdwright.var.objects.VolatilityMethod;
import yahoofinance.histquotes.HistoricalQuote;

public class Backtest {

  private static final int numberOfTests = 1000;
  private static final int daysPerTest = 252;

  private static final double confidence = 0.99;
  private static final int timeHorizon = 1;

  private static ModelBuildingVar modelBuilding = new ModelBuildingVar();
  private static HistoricalSimVar historicalSim = new HistoricalSimVar();

  /**
   * Backtesting method for the Model-Building single case
   */
  public static void testModelSingle() {
    Position testedPosition = new Position("GOOGL", 105);
    Portfolio testedPortfolio = new Portfolio(testedPosition);

    try {
      DataManager.getHistoricalPrices(testedPortfolio, numberOfTests);

      List<HistoricalQuote> dataToUse = testedPortfolio.getPosition(0).getHistoricalData();

      int actualSize = VarMath.getSmallestDatasetSize(testedPortfolio);

      int currentStartingBoundary = 0;
      int currentEndingBoundary = daysPerTest;

      double numberOfViolations = 0;
      double numberOfTestsCompleted = 0;

      int[][] modelBuildingResults = new int[3][actualSize];
      BigDecimal[] changesInValue = getChangesInValue(testedPortfolio);

      for (int i = 0; i < actualSize-1; i++) { //For each test
        Portfolio testPortfolio = new Portfolio(testedPosition);
        List<HistoricalQuote> currentDataSet = new ArrayList<HistoricalQuote>();

        if((currentStartingBoundary + 252) > actualSize) {
          break; //Stop loop when there is no longer 252 more tests to be had
        }

        for (int k = currentStartingBoundary; k < currentEndingBoundary; k++) { //Get data from previous retrieved data
          currentDataSet.add(dataToUse.get(k));
        }

        testedPortfolio.getPosition(0).setHistoricalData(currentDataSet);

        BigDecimal singleVar = modelBuilding.calculateVar(testPortfolio, timeHorizon, confidence, VolatilityMethod.EWMA);

        if(singleVar.compareTo(changesInValue[i]) < 0) {
          numberOfViolations++; //Increment violations
        }

        numberOfTestsCompleted++;
        currentStartingBoundary++;
        currentEndingBoundary++;
        System.out.println("Test " + i + " is complete!");
      }

      System.out.println("Number of violations: " + numberOfViolations + " out of " + numberOfTestsCompleted + " tests which were completed!");

      double percentage = (numberOfViolations / numberOfTestsCompleted) * 100;
      System.out.println("Percentage: " + percentage + "%");

      if(percentage > ((1-confidence) * 100)) {
        System.out.println("This method exceeds allowed violations!");
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static BigDecimal[] getChangesInValue(Portfolio portfolio) {
    int smallestSize = VarMath.getSmallestDatasetSize(portfolio);
    BigDecimal[] dailyValues = new BigDecimal[smallestSize];
    BigDecimal[] changes = new BigDecimal[smallestSize];

    for (int i = 0; i < smallestSize; i++) { //Every testable value
      BigDecimal totalValueOnDay = new BigDecimal(0);

      for (int j = 0; j < portfolio.getSize(); j++) { //For each position
        BigDecimal positionValue = new BigDecimal(portfolio.getPosition(j).getHoldings());
        positionValue = positionValue.multiply(portfolio.getPosition(j).getHistoricalData().get(i).getAdjClose());

        totalValueOnDay = totalValueOnDay.add(positionValue);
        dailyValues[i] = totalValueOnDay;
      }

      if(i > 0) {
        changes[i] = totalValueOnDay.subtract(dailyValues[i-1]); //Subtract current value by previous day to get change
      } else {
        changes[i] = totalValueOnDay;
      }

      System.out.println("CHANGE ON DAY " + i + " IS " + changes[i]);
    }

    return changes;
  }

}
