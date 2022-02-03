package pl.bzowski.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.analysis.criteria.NumberOfPositionsCriterion;
import org.ta4j.core.analysis.criteria.WinningPositionsRatioCriterion;
import org.ta4j.core.analysis.criteria.pnl.GrossReturnCriterion;

public class StrategyAnalysis {

    private final Logger logger = LoggerFactory.getLogger(StrategyAnalysis.class);

    public void doIt(BarSeries series, TradingRecord tradingRecord, Strategy strategy, String symbol) {
        logger.info("Strategy {} analysis for {}", strategy.getName(), symbol);
        // Total profit
        GrossReturnCriterion totalReturn = new GrossReturnCriterion();
        logger.info("Total return: " + totalReturn.calculate(series, tradingRecord));
        // Returns the total number of bars in all the positions.
        // logger.info("Total number of bars in all the positions: " + new
        // NumberOfBarsCriterion().calculate(series, tradingRecord));
        // Average profit (per bar)
        // logger.info(
        // "Average return (per bar): "
        // + new AverageReturnPerBarCriterion().calculate(series, tradingRecord));
        // Number of positions
        logger.info("Number of positions: "
                + new NumberOfPositionsCriterion().calculate(series, tradingRecord));
        // Profitable position ratio
        logger.info(
                "Winning positions ratio: " + new WinningPositionsRatioCriterion().calculate(series,
                        tradingRecord));
        // // Maximum drawdown
        // logger.info("Maximum drawdown: " + new
        // MaximumDrawdownCriterion().calculate(series, tradingRecord));
        // Reward-risk ratio
        // logger.info("Return over maximum drawdown: "
        //                 + new ReturnOverMaxDrawdownCriterion().calculate(series, tradingRecord));
        logger.info("-------------------------------------------------------------------------"); //long line for better log redability
        // // Total transaction cost
        // logger.info("Total transaction cost (from $1000): "
        // + new LinearTransactionCostCriterion(1000, 0.005).calculate(series,
        // tradingRecord));
        // // Buy-and-hold
        // logger.info("Buy-and-hold return: " + new
        // BuyAndHoldReturnCriterion().calculate(series, tradingRecord));
        // Total profit vs buy-and-hold
        // logger.info("Custom strategy return vs buy-and-hold strategy return: "
        // + new VersusBuyAndHoldCriterion(totalReturn).calculate(series,
        // tradingRecord));
    }

}
