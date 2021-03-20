This project demonstrates the implementation of several estimation methods for Value At Risk and
various mathematically algorithms associated with VaR estimation, all written in Java.

VaR estimation methods include:
 * The Model-Building Approach
     -> Single Asset Case
     -> Two Asset Case (Specific case of the Linear Model)
     -> The Linear Model
 * The Historical Simulation Approach

While variance/volatility implementations include:
 * Equally-Weighted Model (Otherwise known as Simple Variance)
 * Exponentially-Weighted Moving Average Model (EWMA)

Other mathematical functions include:
 * Covariance matrices
 * Coefficients of Correlation
 * Percentage Changes
 * Foreign-Exchange Conversions
 * Portfolio Valuation
 * Position Valuation

All estimations and calculations are performed on real historical data pulled directly from Yahoo
Finance using the unofficial Yahoo Finance API (https://github.com/sstrickx/yahoofinance-api/).

This project also includes a fully-usable user interface built with JavaFx, including data
validation and the charting of historical portfolio prices.