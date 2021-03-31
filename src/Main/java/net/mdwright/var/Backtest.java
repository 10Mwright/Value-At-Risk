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

/**
 * Class for performing backtests on VaR models.
 * @author Matthew Wright
 */
public class Backtest {

  private static final int numberOfTests = 5000;
  private static final int daysPerTest = 512;

  private static final double confidence = 0.99;
  private static final int timeHorizon = 1;

  private static ModelBuildingVar modelBuilding = new ModelBuildingVar();
  private static HistoricalSimVar historicalSim = new HistoricalSimVar();

  /**
   * Entry method for performing tests on Model-Building & Historical Simulation.
   */
  public static void doTests() {
    //Backtest Single Asset portfolio
    Position testedPosition = new Position("GOOGL", 105);
    Portfolio testedPortfolio = new Portfolio(testedPosition);

    //Two asset portfolio
    Position testedPositionOne = new Position("GOOGL", 105);
    Position testedPositionTwo = new Position("TSLA", 100);
    Portfolio testedPortfolioTwo = new Portfolio(
        new Position[] {testedPositionOne, testedPositionTwo});

    //Triple asset portfolio
    Position testedPositionThree = new Position("GOOGL", 105);
    Position testedPositionFour = new Position("TSLA", 100);
    Position testedPositionFive = new Position("GME", 165);
    Portfolio testedPortfolioThree = new Portfolio(
        new Position[] {testedPositionThree, testedPositionFour, testedPositionFive});

    try {
      DataManager.getHistoricalPrices(testedPortfolio, numberOfTests);
      DataManager.getHistoricalPrices(testedPortfolioTwo, numberOfTests);
      DataManager.getHistoricalPrices(testedPortfolioThree, numberOfTests);


      //Single asset EQ Weighted
      double[] violationsSingleAsset = testModelBuilding(testedPortfolio, VolatilityMethod.SIMPLE);

      //Double asset EQ Weighted
      double[] violationsDoubleAsset = testModelBuilding(testedPortfolioTwo,
          VolatilityMethod.SIMPLE);

      //Triple asset EQ Weighted
      double[] violationsTripleAsset = testModelBuilding(testedPortfolioThree,
          VolatilityMethod.SIMPLE);

      //Single asset EWMA
      double[] violationsSingleAssetEw = testModelBuilding(testedPortfolio,
          VolatilityMethod.EWMA);

      //Double asset EWMA
      double[] violationsDoubleAssetEw = testModelBuilding(testedPortfolioTwo,
          VolatilityMethod.EWMA);

      //Triple asset EWMA
      double[] violationsTripleAssetEw = testModelBuilding(testedPortfolioThree,
          VolatilityMethod.EWMA);

      //Historical sim using 252 days per run
      double[] violationsHist = testHistoricalSimulation(testedPortfolioThree, 252);

      //Historical sim using 764 days per run
      double[] violationsHistLong = testHistoricalSimulation(testedPortfolioThree, 764);

      System.out.println("Single Asset Testing (SIMPLE): " + violationsSingleAsset[0]
          + " Violations out of " + violationsSingleAsset[1] + " tests!");

      System.out.println("Double Asset Testing (SIMPLE): " + violationsDoubleAsset[0]
          + " Violations out of " + violationsDoubleAsset[1] + " tests!");

      System.out.println("Triple Asset Testing (SIMPLE): " + violationsTripleAsset[0]
          + " Violations out of " + violationsTripleAsset[1] + " tests!");

      System.out.println("Single Asset Testing (EWMA): " + violationsSingleAssetEw[0]
          + " Violations out of " + violationsSingleAssetEw[1] + " tests!");

      System.out.println("Double Asset Testing (EWMA): " + violationsDoubleAssetEw[0]
          + " Violations out of " + violationsDoubleAssetEw[1] + " tests!");

      System.out.println("Triple Asset Testing (EWMA): " + violationsTripleAssetEw[0]
          + " Violations out of " + violationsTripleAssetEw[1] + " tests!");

      System.out.println("Triple Asset Historical Sim (252 days): " + violationsHist[0]
          + " Violations out of " + violationsHist[1] + " tests!");

      System.out.println("Triple Asset Historical Sim (512 days): " + violationsHistLong[0]
          + " Violations out of " + violationsHistLong[1] + " tests!");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * Backtesting method for the Model-Building single case.
   * @param testedPortfolio Portfolio to test upon
   * @param daysPerTest Days to use per run of the algorithm
   */
  public static double[] testHistoricalSimulation(Portfolio testedPortfolio, int daysPerTest) {
    List<HistoricalQuote>[] dataToUse = new ArrayList[testedPortfolio.getSize()];

    for (int i = 0; i < testedPortfolio.getSize(); i++) { //for each position in the portfolio
      dataToUse[i] = testedPortfolio.getPosition(i).getHistoricalData();
    }

    int actualSize = VarMath.getSmallestDatasetSize(testedPortfolio);
    System.out.println("Actual Size: " + actualSize);

    int currentStartingBoundary = 1; //First day is used only to calculate changes
    int currentEndingBoundary = currentStartingBoundary + daysPerTest;
    int workableTests = actualSize - (daysPerTest + 1); //Number of valid test ranges in data

    double numberOfViolations = 0;
    double numberOfTestsCompleted = 0;

    BigDecimal[] changesInValue = getChangesInValue(testedPortfolio);

    // https://stackoverflow.com/questions/8363493/hiding-system-out-print-calls-of-a-class
    // Idea taken from Adrian.ng (https://github.com/Adrian-Ng/VaR/)
    PrintStream originalStream = System.out;

    PrintStream dummyStream = new PrintStream(new OutputStream() {
      public void write(int b) {
        // NO-OP
      }
    });

    System.setOut(dummyStream);


    for (int i = 1; i < workableTests; i++) { //For each workable test
      if ((currentStartingBoundary + daysPerTest) > actualSize) {
        break; //Stop loop when there is no longer enough days to build with
      }

      Position[] clonedPositions = new Position[testedPortfolio.getSize()];
      for (int j = 0; j < testedPortfolio.getSize(); j++) { //For each position
        List<HistoricalQuote> currentDataSet = new ArrayList<HistoricalQuote>();

        clonedPositions[j] = new Position(testedPortfolio.getPosition(j).getTickerSymbol(),
            testedPortfolio.getPosition(j).getHoldings());

        for (int k = currentStartingBoundary; k < currentEndingBoundary;
            k++) { //Build new dataset
          currentDataSet.add(dataToUse[j].get(k));
        }

        clonedPositions[j].setHistoricalData(currentDataSet);
      }

      Portfolio testPortfolioClone = new Portfolio(clonedPositions);

      BigDecimal singleDayVar = historicalSim.calculateVar(testPortfolioClone,
          timeHorizon, confidence);

      if (singleDayVar.compareTo(changesInValue[currentEndingBoundary + 1]) < 0) {
        numberOfViolations++; //Increment violations
      }

      numberOfTestsCompleted++;
      currentStartingBoundary++;
      currentEndingBoundary++;
      System.out.println("Test " + i + " is complete!");
    }

    System.setOut(originalStream);

    System.out.println("Number of violations: " + numberOfViolations + " out of "
        + numberOfTestsCompleted + " tests which were completed!");

    double percentage = (numberOfViolations / numberOfTestsCompleted) * 100;
    System.out.println("Percentage: " + percentage + "%");

    if (percentage > ((1 - confidence) * 100)) {
      System.out.println("This method exceeds allowed violations!");
    }

    double[] violations = new double[] {numberOfViolations, numberOfTestsCompleted};

    return violations;
  }


  /**
   * Backtesting method for the Model-Building single case.
   * @param testedPortfolio Portfolio to test upon
   * @param volatilityMethod VolatilityMethod enum for variance model to use
   */
  public static double[] testModelBuilding(Portfolio testedPortfolio,
      VolatilityMethod volatilityMethod) {
    List<HistoricalQuote>[] dataToUse = new ArrayList[testedPortfolio.getSize()];

    for (int i = 0; i < testedPortfolio.getSize(); i++) { //for each position in the portfolio
      dataToUse[i] = testedPortfolio.getPosition(i).getHistoricalData();
    }

    int actualSize = VarMath.getSmallestDatasetSize(testedPortfolio);
    System.out.println("Actual Size: " + actualSize);

    int currentStartingBoundary = 1; //First day is used only to calculate changes
    int currentEndingBoundary = currentStartingBoundary + daysPerTest;
    int workableTests = actualSize - (daysPerTest + 1); //Number of valid test ranges in data

    double numberOfViolations = 0;
    double numberOfTestsCompleted = 0;

    BigDecimal[] changesInValue = getChangesInValue(testedPortfolio);

    // https://stackoverflow.com/questions/8363493/hiding-system-out-print-calls-of-a-class
    // Idea taken from Adrian.ng (https://github.com/Adrian-Ng/VaR/)
    PrintStream originalStream = System.out;

    PrintStream dummyStream = new PrintStream(new OutputStream() {
      public void write(int b) {
        // NO-OP
      }
    });

    System.setOut(dummyStream);


    for (int i = 1; i < workableTests; i++) { //For each workable test
      if ((currentStartingBoundary + daysPerTest) > actualSize) {
        break; //Stop loop when there is no longer enough days to build with
      }

      Position[] clonedPositions = new Position[testedPortfolio.getSize()];
      for (int j = 0; j < testedPortfolio.getSize(); j++) { //For each position
        List<HistoricalQuote> currentDataSet = new ArrayList<HistoricalQuote>();

        clonedPositions[j] = new Position(testedPortfolio.getPosition(j).getTickerSymbol(),
            testedPortfolio.getPosition(j).getHoldings());

        for (int k = currentStartingBoundary; k < currentEndingBoundary;
            k++) { //Build new dataset
          currentDataSet.add(dataToUse[j].get(k));
        }

        clonedPositions[j].setHistoricalData(currentDataSet);
      }

      Portfolio testPortfolioClone = new Portfolio(clonedPositions);

      BigDecimal singleDayVar = modelBuilding.calculateVar(testPortfolioClone,
          timeHorizon, confidence, volatilityMethod);

      if (singleDayVar.compareTo(changesInValue[currentEndingBoundary + 1]) < 0) {
        numberOfViolations++; //Increment violations
      }

      numberOfTestsCompleted++;
      currentStartingBoundary++;
      currentEndingBoundary++;
      System.out.println("Test " + i + " is complete!");
    }

    System.setOut(originalStream);

    System.out.println("Number of violations: " + numberOfViolations + " out of "
        + numberOfTestsCompleted + " tests which were completed!");

    double percentage = (numberOfViolations / numberOfTestsCompleted) * 100;
    System.out.println("Percentage: " + percentage + "%");

    if (percentage > ((1 - confidence) * 100)) {
      System.out.println("This method exceeds allowed violations!");
    }

    double[] violations = new double[] {numberOfViolations, numberOfTestsCompleted};

    return violations;
  }

  /**
   * Method for retrieving actual changes in the value of the portfolio per day.
   * @param portfolio Portfolio object containing position objects
   * @return BigDecimal array of changes in the cumulative portfolio value per day
   */
  public static BigDecimal[] getChangesInValue(Portfolio portfolio) {
    int smallestSize = VarMath.getSmallestDatasetSize(portfolio);
    BigDecimal[] dailyValues = new BigDecimal[smallestSize];
    BigDecimal[] changes = new BigDecimal[smallestSize];

    for (int i = 0; i < smallestSize; i++) { //Every testable value
      BigDecimal totalValueOnDay = new BigDecimal(0);

      for (int j = 0; j < portfolio.getSize(); j++) { //For each position
        BigDecimal positionValue = new BigDecimal(portfolio.getPosition(j).getHoldings());
        positionValue = positionValue.multiply(portfolio.getPosition(j)
            .getHistoricalData().get(i).getAdjClose());

        totalValueOnDay = totalValueOnDay.add(positionValue);
        dailyValues[i] = totalValueOnDay;
      }

      if (i > 0) {
        //Subtract current value by previous day to get change
        changes[i] = totalValueOnDay.subtract(dailyValues[i - 1]);
        changes[i] = changes[i].multiply(new BigDecimal(- 1));
      } else {
        changes[i] = totalValueOnDay;
      }

      System.out.println("CHANGE ON DAY " + i + " IS " + changes[i]);
    }

    return changes;
  }

}
