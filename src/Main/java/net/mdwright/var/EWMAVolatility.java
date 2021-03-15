package net.mdwright.var;

import java.util.List;
import net.mdwright.var.objects.Portfolio;
import net.mdwright.var.objects.Position;
import yahoofinance.histquotes.HistoricalQuote;

public class EWMAVolatility implements VolatilityModel {

  private static double lambda = 0.94; //0.94 by default

  /**
   * Exponentially Weighted Moving Average model for calculating volatility.
   *
   * @param portfolio Portfolio object containing the position objects to be calculated using
   * @param positionIndex Int value representing the index of the position within the Portfolios array
   * @return double value representing the volatility of the position over the time period
   */
  public double calculateVolatility(Portfolio portfolio, int positionIndex) {
    this.lambda = portfolio.getVolatilityLambda(); //Retrieve any user inputted lambda, defaults to 0.94 if no input

    List<HistoricalQuote> historicalData = portfolio.getPosition(positionIndex).getHistoricalData();

    double[][] returns = new double[3][historicalData.size()];
    double currentWeight = (1 - lambda); //Initial weight
    System.out.println("INITIAL WEIGHT: " + currentWeight);

    double dailyVolatility = 0;

    for (int i = (historicalData.size() - 2); i >= 0; i--) { //Increment through historical data day by day
      double currentDay = historicalData.get(i).getAdjClose().doubleValue();
      double yesterday = historicalData.get(i+1).getAdjClose().doubleValue();

      double returnCurrentDay = currentDay / yesterday;
      returns[0][i] = Math.log(returnCurrentDay); //The natural log of the return in day i
      returns[1][i] = Math.pow(returns[0][i], 2); //Daily variance (unweighted)

      returns[2][i] = returns[1][i] * currentWeight; //Weighted variance

      dailyVolatility += returns[2][i]; //Sum all weighted values to get a final volatility
      currentWeight = currentWeight * (lambda);
      System.out.println("CURRENT WEIGHT: " + currentWeight);
    }

    dailyVolatility = Math.sqrt(dailyVolatility);

    System.out.println("Daily Volatility (EWMA): " + dailyVolatility);

    portfolio.getPosition(positionIndex).setVolatility(dailyVolatility);
    return dailyVolatility; //Final volatility value by sqrt
  }
}
