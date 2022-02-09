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

public class StrongBaseLineCrossStrategy implements StrategyBuilder {

    private final String symbol;

    public StrongBaseLineCrossStrategy(String symbol) {
        this.symbol = symbol;
    }

    /*
        Type of signal: Strong Bullish
        Type of crossing: Price > Base line
        1st condition - Crossing position: Crossing over the cloud
        Optional alert: BASE LINE POINTG UPWARD //TODO: Implent that somewho
     */
    @Override
    public StrategyWithIndicators getLongStrategy(BarSeries series) {
      ClosePriceIndicator cpi = new ClosePriceIndicator(series);
      IchimokuKijunSenIndicator kinjun = new IchimokuKijunSenIndicator(series);

      IchimokuSenkouSpanAIndicator spanA = new IchimokuSenkouSpanAIndicator(series);
      IchimokuSenkouSpanBIndicator spanB = new IchimokuSenkouSpanBIndicator(series);

      CrossedUpIndicatorRule cuir = new CrossedUpIndicatorRule(cpi, kinjun);
      
      Rule enterRule = cuir
      .and(new OverIndicatorRule(cpi, spanA))
      .and(new OverIndicatorRule(cpi, spanB))
      .and(new OverIndicatorRule(kinjun, spanA))
      .and(new OverIndicatorRule(kinjun, spanB));

      Rule exitRule = new CrossedDownIndicatorRule(cpi, kinjun);

      return new StrategyWithIndicators("StrongBaseLineCrossStrategy-LONG", symbol, enterRule, exitRule, cpi, kinjun, spanA, spanB);
    }

        /*
        Type of signal: Strong Bearish
        Type of crossing: Price < Base line
        1st condition - Crossing position: Crossing below the cloud
        Optional alert: BASE LINE POINTG DOWNWARD //TODO: Implent that somewho
     */
    @Override
    public StrategyWithIndicators getShortStrategy(BarSeries series) {
      ClosePriceIndicator cpi = new ClosePriceIndicator(series);
      IchimokuKijunSenIndicator kinjun = new IchimokuKijunSenIndicator(series);

      IchimokuSenkouSpanAIndicator spanA = new IchimokuSenkouSpanAIndicator(series);
      IchimokuSenkouSpanBIndicator spanB = new IchimokuSenkouSpanBIndicator(series);

      CrossedDownIndicatorRule cuir = new CrossedDownIndicatorRule(cpi, kinjun);
      
      Rule enterRule = cuir
      .and(new UnderIndicatorRule(cpi, spanA))
      .and(new UnderIndicatorRule(cpi, spanB))
      .and(new UnderIndicatorRule(kinjun, spanA))
      .and(new UnderIndicatorRule(kinjun, spanB));

      Rule exitRule = new CrossedUpIndicatorRule(cpi, kinjun);

      return new StrategyWithIndicators("StrongBaseLineCrossStrategy-SHORT", symbol, enterRule, exitRule, cpi, kinjun, spanA, spanB);
    }

  }