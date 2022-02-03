package pl.bzowski.bot.strategies;

 
import org.jboss.logging.Logger;
import org.ta4j.core.*;
import pl.bzowski.bot.states.*;

import java.util.Arrays;

public class StrategyWithIndicators extends BaseStrategy {
    private static final Logger LOG = Logger.getLogger(StrategyWithIndicators.class);
    
    private final Indicator[] indicators;
    private final String symbol;


    public StrategyWithIndicators(String name, String symbol, Rule entryRule, Rule exitRule, Indicator... indicators) {
        super(name, entryRule, exitRule);
        this.symbol = symbol;
        this.indicators = indicators;
    }

    @Override
    public boolean shouldEnter(int index) {
        boolean shouldEnter = super.shouldEnter(index);
        LOG.info(String.format("%s: %s should enter: %s. Indicators:", symbol, getName(), shouldEnter));
        Arrays.stream(indicators)
                .forEach(i -> LOG.info(String.format("%s: %s", i.getClass(), i.getValue(index))));
        return shouldEnter;
    }
}
