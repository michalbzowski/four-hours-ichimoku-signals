package pl.bzowski.bot.strategies.ichimoku;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.indicators.ichimoku.*;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;
import pl.bzowski.bot.strategies.StrategyBuilder;
import pl.bzowski.bot.strategies.StrategyWithIndicators;

public class WeakTenkanKinjunCrossStrategy implements StrategyBuilder {

    private final String symbol;

    public WeakTenkanKinjunCrossStrategy(String symbol) {
        this.symbol = symbol;
    }

    /*
     * Type of signal: Strong Bullish
     * Type of crossing: Conversion Line > Base line
     * 1st condition - Crossing position: Crossing below the cloud
     * 2nd condition - Price vs cloud: Price is over the cloud
     * 3rd condition - Lagging span position: Over the price 26 periods ago
     */
    @Override
    public StrategyWithIndicators getLongStrategy(BarSeries series) {
        ClosePriceIndicator cpi = new ClosePriceIndicator(series);

        PreviousValueIndicator priceTwentySixPeriodsAgo = new PreviousValueIndicator(cpi, 26);
        IchimokuChikouSpanIndicator chikou = new IchimokuChikouSpanIndicator(series);
        PreviousValueIndicator chikouTwentySixPeriodsAgo = new PreviousValueIndicator(chikou, 26);

        IchimokuSenkouSpanAIndicator spanA = new IchimokuSenkouSpanAIndicator(series);
        IchimokuSenkouSpanBIndicator spanB = new IchimokuSenkouSpanBIndicator(series);

        IchimokuTenkanSenIndicator tenkan = new IchimokuTenkanSenIndicator(series);
        IchimokuKijunSenIndicator kinjun = new IchimokuKijunSenIndicator(series);

        Rule enterRule = new CrossedUpIndicatorRule(tenkan, kinjun)
                .and(new UnderIndicatorRule(tenkan, spanA))
                .and(new UnderIndicatorRule(tenkan, spanB))
                .and(new UnderIndicatorRule(kinjun, spanA))
                .and(new UnderIndicatorRule(kinjun, spanB))
                .and(new OverIndicatorRule(cpi, spanA))
                .and(new OverIndicatorRule(cpi, spanB))
                .and(new OverIndicatorRule(chikouTwentySixPeriodsAgo, priceTwentySixPeriodsAgo));

        Rule exitRule = new CrossedDownIndicatorRule(tenkan, kinjun);

        return new StrategyWithIndicators("WeakTenkanKinjunCross-LONG", symbol, enterRule, exitRule, cpi, tenkan, kinjun, chikouTwentySixPeriodsAgo, spanA, spanB, priceTwentySixPeriodsAgo);
    }

    /*
     * Type of signal: Strong Bearish
     * Type of crossing: Conversion Line < Base line
     * 1st condition - Crossing position: Crossing over the cloud
     * 2nd condition - Price vs cloud: Price is below the cloud
     * 3rd condition - Lagging span position: Below the price 26 periods ago
     */
    @Override
    public StrategyWithIndicators getShortStrategy(BarSeries series) {
        ClosePriceIndicator cpi = new ClosePriceIndicator(series);

        PreviousValueIndicator priceTwentySixPeriodsAgo = new PreviousValueIndicator(cpi, 26);
        IchimokuChikouSpanIndicator chikou = new IchimokuChikouSpanIndicator(series);
        PreviousValueIndicator chikouTwentySixPeriodsAgo = new PreviousValueIndicator(chikou, 26);

        IchimokuSenkouSpanAIndicator spanA = new IchimokuSenkouSpanAIndicator(series);
        IchimokuSenkouSpanBIndicator spanB = new IchimokuSenkouSpanBIndicator(series);

        IchimokuTenkanSenIndicator tenkan = new IchimokuTenkanSenIndicator(series);
        IchimokuKijunSenIndicator kinjun = new IchimokuKijunSenIndicator(series);

        Rule enterRule = new CrossedDownIndicatorRule(tenkan, kinjun)
                .and(new OverIndicatorRule(tenkan, spanA))
                .and(new OverIndicatorRule(tenkan, spanB))
                .and(new OverIndicatorRule(kinjun, spanA))
                .and(new OverIndicatorRule(kinjun, spanB))
                .and(new UnderIndicatorRule(cpi, spanA))
                .and(new UnderIndicatorRule(cpi, spanB))
                .and(new UnderIndicatorRule(chikouTwentySixPeriodsAgo, priceTwentySixPeriodsAgo));

        Rule exitRule = new CrossedUpIndicatorRule(tenkan, kinjun);

        return new StrategyWithIndicators("WeakTenkanKinjunCross-SHORT", symbol, enterRule, exitRule, cpi, tenkan, kinjun, chikouTwentySixPeriodsAgo, spanA, spanB, priceTwentySixPeriodsAgo);
    }
}
