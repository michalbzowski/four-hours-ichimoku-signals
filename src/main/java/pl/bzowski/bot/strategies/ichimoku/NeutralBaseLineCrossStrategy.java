package pl.bzowski.bot.strategies.ichimoku;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.indicators.ichimoku.*;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.InPipeRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;
import pl.bzowski.bot.strategies.StrategyBuilder;
import pl.bzowski.bot.strategies.StrategyWithIndicators;

public class NeutralBaseLineCrossStrategy implements StrategyBuilder {

    private final String symbol;

    public NeutralBaseLineCrossStrategy(String symbol) {
        this.symbol = symbol;
    }

    /*
        Type of signal: Neutral Bullish
        Type of crossing: Price > Base line
        1st condition - Crossing position: Crossing inside the cloud
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
      .and(new InPipeRule(cpi, spanA, spanB))
      .and(new InPipeRule(kinjun, spanA, spanB));

      Rule exitRule = new CrossedDownIndicatorRule(cpi, kinjun);

      return new StrategyWithIndicators("NeutralBaseLineCrossStrategy-LONG", symbol, enterRule, exitRule, cpi, kinjun, spanA, spanB);
    }

        /*
        Type of signal: Neutral Bearish
        Type of crossing: Price < Base line
        1st condition - Crossing position: Crossing inside the cloud
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
      .and(new InPipeRule(cpi, spanA, spanB))
      .and(new InPipeRule(kinjun, spanA, spanB));

      Rule exitRule = new CrossedUpIndicatorRule(cpi, kinjun);

      return new StrategyWithIndicators("NeutralBaseLineCrossStrategy-SHORT", symbol, enterRule, exitRule, cpi, kinjun, spanA, spanB);
    }

  }