package net.mdwright.var;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import net.mdwright.var.objects.VolatilityMethod;
import yahoofinance.histquotes.HistoricalQuote;

public class Backtest {

  private static final int numberOfTests = 2000;
  private static final int daysPerTest = 252;
  private static final int numberOfMethods = 4;

  private static final double confidence = 0.99;
  private static final int timeHorizon = 1;

  private static ModelBuildingVar modelBuilding = new ModelBuildingVar();
  private static HistoricalSimVar historicalSim = new HistoricalSimVar();

  public static void doTests() {
    //Backtest Single Asset Model-Building
    Position testedPosition = new Position("TSLA", 105);
    Portfolio testedPortfolio = new Portfolio(testedPosition);

    Position testedPositionTwo = new Position("GOOGL", 100);
    Portfolio testedPortfolioTwo = new Portfolio(new Position[] {testedPosition, testedPositionTwo});

    Position testedPositionThree = new Position("GME", 165);
    Portfolio testedPortfolioThree = new Portfolio(new Position[] {testedPosition, testedPositionTwo, testedPositionThree});

    try {
      DataManager.getHistoricalPrices(testedPortfolio, numberOfTests);
      DataManager.getHistoricalPrices(testedPortfolioTwo, numberOfTests);
      DataManager.getHistoricalPrices(testedPortfolioThree, numberOfTests);

      double[] violationsSingleAsset = testModelBuilding(testedPortfolio);

      double[] violationsDoubleAsset = testModelBuilding(testedPortfolioTwo);

      double[] violationsTripleAsset = testModelBuilding(testedPortfolioThree);

      System.out.println("Single Asset Testing: " + violationsSingleAsset[0] + " Violations out of " + violationsSingleAsset[1] + " tests!");

      System.out.println("Double Asset Testing: " + violationsDoubleAsset[0] + " Violations out of " + violationsDoubleAsset[1] + " tests!");

      System.out.println("Triple Asset Testing: " + violationsTripleAsset[0] + " Violations out of " + violationsTripleAsset[1] + " tests!");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * Backtesting method for the Model-Building single case
   */
  public static double[] testModelBuilding(Portfolio testedPortfolio) {
    List<HistoricalQuote>[] dataToUse = new ArrayList[testedPortfolio.getSize()];

    for (int i = 0; i < testedPortfolio.getSize(); i++) { //for each position in the portfolio
      dataToUse[i] = testedPortfolio.getPosition(i).getHistoricalData();
    }

    int actualSize = VarMath.getSmallestDatasetSize(testedPortfolio);

    int currentStartingBoundary = 1; //First day is used only to calculate changes
    int currentEndingBoundary = currentStartingBoundary + daysPerTest;
    int workableTests = actualSize - (daysPerTest + 1); //Number of valid test ranges in data

    double numberOfViolations = 0;
    double numberOfTestsCompleted = 0;

    BigDecimal[] changesInValue = getChangesInValue(testedPortfolio);

    PrintStream originalStream = System.out;

    PrintStream dummyStream = new PrintStream(new OutputStream(){
      public void write(int b) {
        // NO-OP
      }
    });

    System.setOut(dummyStream);

    for (int i = 1; i < workableTests; i++) { //For each workable test
      if ((currentStartingBoundary + daysPerTest) > actualSize) {
        break; //Stop loop when there is no longer enough days to build with
      }

      Portfolio testPortfolio = testedPortfolio;

      for (int j = 0; j < testedPortfolio.getSize(); j++) { //For each position
        List<HistoricalQuote> currentDataSet = new ArrayList<HistoricalQuote>();

        for (int k = currentStartingBoundary; k < currentEndingBoundary;
            k++) { //Build new dataset
          currentDataSet.add(dataToUse[j].get(k));
        }

        testedPortfolio.getPosition(j).setHistoricalData(currentDataSet);
      }

      BigDecimal singleDayVar = modelBuilding.calculateVar(testPortfolio, timeHorizon, confidence, VolatilityMethod.EWMA);

      if(singleDayVar.compareTo(changesInValue[currentEndingBoundary + 1]) < 0) {
        numberOfViolations++; //Increment violations
      }

      numberOfTestsCompleted++;
      currentStartingBoundary++;
      currentEndingBoundary++;
      System.out.println("Test " + i + " is complete!");
    }

    System.setOut(originalStream);

    System.out.println("Number of violations: " + numberOfViolations + " out of " + numberOfTestsCompleted + " tests which were completed!");

    double percentage = (numberOfViolations / numberOfTestsCompleted) * 100;
    System.out.println("Percentage: " + percentage + "%");

    if(percentage > ((1-confidence) * 100)) {
      System.out.println("This method exceeds allowed violations!");
    }

    double[] violations = new double[] {numberOfViolations, numberOfTestsCompleted};

    return violations;
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
        changes[i] = changes[i].abs();
      } else {
        changes[i] = totalValueOnDay;
      }

      System.out.println("CHANGE ON DAY " + i + " IS " + changes[i]);
    }

    return changes;
  }

}
