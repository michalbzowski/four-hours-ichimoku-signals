package pl.bzowski.bot.strategies;

public class LongShortStrategyPair {
    private final StrategyWithIndicators longStrategy;
    private final StrategyWithIndicators shortStrategy;

    public LongShortStrategyPair(StrategyWithIndicators longStrategy, StrategyWithIndicators shortStrategy) {

        this.longStrategy = longStrategy;
        this.shortStrategy = shortStrategy;
    }

    public StrategyWithIndicators getLongStrategy() {
        return longStrategy;
    }

    public StrategyWithIndicators getShortStrategy() {
        return shortStrategy;
    }
}
