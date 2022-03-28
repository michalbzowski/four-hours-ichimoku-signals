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

public class StrongPriceCloudBreakoutStrategy implements StrategyBuilder {

    private final String symbol;

    public StrongPriceCloudBreakoutStrategy(String symbol) {
        this.symbol = symbol;
    }

    /*
        Type of signal: Strong Bullish
        Type of crossing: Price > Cloud
        1st condition - Base line pointing Upward //TODO: Na razie niech mi sie pokaze w ogole jakikolwiek sygnal kupna
        2nd condition - Color of future cloud: green //TODO: Na razie niech mi sie pokaze w ogole jakikolwiek sygnal kupna
        3rd condition - LAGGING SPAN POSITION: Over the Cloud 26 periods ago //TODO
     */
    @Override
    public StrategyWithIndicators getLongStrategy(BarSeries series) {
      ClosePriceIndicator cpi = new ClosePriceIndicator(series);

      IchimokuSenkouSpanAIndicator spanA = new IchimokuSenkouSpanAIndicator(series);
      IchimokuSenkouSpanBIndicator spanB = new IchimokuSenkouSpanBIndicator(series);

      Rule priceBreakUpSpanA = new CrossedUpIndicatorRule(cpi, spanA).and(new OverIndicatorRule(spanA, spanB));
      Rule priceBreakUpSpanB = new CrossedUpIndicatorRule(cpi, spanB).and(new OverIndicatorRule(spanB, spanA));

      IchimokuChikouSpanIndicator chikou = new IchimokuChikouSpanIndicator(series);
      PreviousValueIndicator chikouTwentySixPeriodsAgo = new PreviousValueIndicator(chikou, 26);
      Rule chickouSpanOverCloud26PeriodsAgo = new OverIndicatorRule(chikouTwentySixPeriodsAgo,  new PreviousValueIndicator(spanA, 26))
      .and(new OverIndicatorRule(chikouTwentySixPeriodsAgo,  new PreviousValueIndicator(spanB, 26)));
      
      Rule enterRule = priceBreakUpSpanA.or(priceBreakUpSpanB);
      Rule exitRule = new CrossedDownIndicatorRule(cpi, spanA).or(new CrossedDownIndicatorRule(cpi, spanB));

      return new StrategyWithIndicators("StrongPriceCloudBreakoutStrategy-LONG", symbol, enterRule, exitRule, cpi, spanA, spanB);
    }

    /*
        Type of signal: Strong Bearish
        Type of crossing: Price < Cloud
        1st condition - Base line pointing Downward //TODO: Na razie niech mi sie pokaze w ogole jakikolwiek sygnal kupna
        2nd condition - Color of future cloud: red //TODO: Na razie niech mi sie pokaze w ogole jakikolwiek sygnal kupna
        3rd condition - LAGGING SPAN POSITION: below the Cloud 26 periods ago //TODO
     */
    @Override
    public StrategyWithIndicators getShortStrategy(BarSeries series) {
      ClosePriceIndicator cpi = new ClosePriceIndicator(series);

      IchimokuSenkouSpanAIndicator spanA = new IchimokuSenkouSpanAIndicator(series);
      IchimokuSenkouSpanBIndicator spanB = new IchimokuSenkouSpanBIndicator(series);

      Rule priceBreakUpSpanA = new CrossedDownIndicatorRule(cpi, spanA).and(new UnderIndicatorRule(spanA, spanB));
      Rule priceBreakUpSpanB = new CrossedDownIndicatorRule(cpi, spanB).and(new UnderIndicatorRule(spanB, spanA));
      Rule enterRule = priceBreakUpSpanA.or(priceBreakUpSpanB);
      Rule exitRule = new CrossedUpIndicatorRule(cpi, spanA).or(new CrossedUpIndicatorRule(cpi, spanB));

      return new StrategyWithIndicators("StrongBaseLineCrossStrategy-SHORT", symbol, enterRule, exitRule, cpi, spanA, spanB);
    }

  }