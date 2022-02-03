package pl.bzowski.bot.strategies;

import org.ta4j.core.BarSeries;

public interface StrategyBuilder {
    StrategyWithIndicators getLongStrategy(BarSeries series);

    StrategyWithIndicators getShortStrategy(BarSeries series);
}
