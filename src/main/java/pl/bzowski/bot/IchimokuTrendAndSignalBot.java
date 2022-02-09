package pl.bzowski.bot;

import org.jboss.logging.Logger;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BarSeriesManager;
import org.ta4j.core.Strategy;
import org.ta4j.core.TradingRecord;

import io.undertow.websockets.util.ContextSetupHandler.Action;
import pl.bzowski.actualtrends.ActualTrendsFacade;
import pl.bzowski.bot.strategies.StrategyWithIndicators;
import pl.bzowski.bot.strategies.ichimoku.NeutralBaseLineCrossStrategy;
import pl.bzowski.bot.strategies.ichimoku.NeutralTenkanKinjunCrossStrategy;
import pl.bzowski.bot.strategies.ichimoku.StrongBaseLineCrossStrategy;
import pl.bzowski.bot.strategies.ichimoku.StrongPriceCloudBreakoutStrategy;
import pl.bzowski.bot.strategies.ichimoku.StrongTenkanKinjunCrossStrategy;
import pl.bzowski.bot.strategies.ichimoku.WeakBaseLineCrossStrategy;
import pl.bzowski.bot.strategies.ichimoku.WeakTenkanKinjunCrossStrategy;
import pl.bzowski.bot.trend.Trend;
import pl.bzowski.bot.trend.TrendChecker;
import pl.bzowski.signals.SignalsFacade;
import pl.bzowski.trading.SeriesService;
import pro.xstore.api.message.records.SCandleRecord;
import pl.bzowski.signalsreport.Signal;

import java.util.HashSet;
import java.util.Set;

public class IchimokuTrendAndSignalBot {

    private static final Logger LOG = Logger.getLogger(IchimokuTrendAndSignalBot.class);
    private final String symbol;
    private final TrendChecker trendChecker;
    private final Set<StrategyWithIndicators> strategies = new HashSet<>();
    private ActualTrendsFacade actualTrendsFacade;
    private SignalsFacade signalsFacade;

    public IchimokuTrendAndSignalBot(String symbol, BarSeries series, TrendChecker trendChecker, ActualTrendsFacade actualTrendsFacade, SignalsFacade signalsFacade) {
        this.symbol = symbol;
        this.trendChecker = trendChecker;
        this.actualTrendsFacade = actualTrendsFacade;
        this.signalsFacade = signalsFacade;
        loadStrategies(series);
    }

    private void loadStrategies(BarSeries series) {
        strategies.add(new StrongTenkanKinjunCrossStrategy(symbol).getLongStrategy(series));
        strategies.add(new StrongTenkanKinjunCrossStrategy(symbol).getShortStrategy(series));
        strategies.add(new NeutralTenkanKinjunCrossStrategy(symbol).getLongStrategy(series));
        strategies.add(new NeutralTenkanKinjunCrossStrategy(symbol).getShortStrategy(series));
        strategies.add(new WeakTenkanKinjunCrossStrategy(symbol).getLongStrategy(series));
        strategies.add(new WeakTenkanKinjunCrossStrategy(symbol).getShortStrategy(series));

        strategies.add(new StrongBaseLineCrossStrategy(symbol).getLongStrategy(series));
        strategies.add(new StrongBaseLineCrossStrategy(symbol).getShortStrategy(series));
        strategies.add(new NeutralBaseLineCrossStrategy(symbol).getLongStrategy(series));
        strategies.add(new NeutralBaseLineCrossStrategy(symbol).getShortStrategy(series));
        strategies.add(new WeakBaseLineCrossStrategy(symbol).getLongStrategy(series));
        strategies.add(new WeakBaseLineCrossStrategy(symbol).getShortStrategy(series));

        strategies.add(new StrongPriceCloudBreakoutStrategy(symbol).getLongStrategy(series)); //Na razie tylko jedno, bo nie wiem jak poloczyc or'em rozne wskazniki
        strategies.add(new StrongPriceCloudBreakoutStrategy(symbol).getShortStrategy(series));//Jak sobie sciagne jakies dane do testow to sobie napisze testy :D a moze to dobra okazja?
    }

    public void onTick(int endIndex, SCandleRecord candleRecord) {
        LOG.info(String.format("%s TICK!!!!!!!!!!!", SeriesService.FIRST_PERIOD_CODE));
        
        // Trend thirdPeriodTrend = trendChecker.checkTrend(symbol, SeriesService.THIRD_PERIOD_CODE);
        // actualTrendsFacade.setActualTrend(symbol, SeriesService.THIRD_PERIOD_CODE.getCode(), thirdPeriodTrend);
        // logger.info("{} {} trend is: {}", SeriesService.THIRD_PERIOD_CODE.toString(), symbol, thirdPeriodTrend.toString());
     
        // Trend secondPeriodTrend = trendChecker.checkTrend(symbol, SeriesService.SECOND_PERIOD_CODE);
        // actualTrendsFacade.setActualTrend(symbol, SeriesService.SECOND_PERIOD_CODE.getCode(), secondPeriodTrend);
        // logger.info("{} {} trend is: {}", SeriesService.SECOND_PERIOD_CODE, symbol, secondPeriodTrend.toString());
     
        Trend firstPeriodTrend = trendChecker.checkTrend(symbol, SeriesService.FIRST_PERIOD_CODE);
        LOG.info(String.format("%s %s trend is: %s", SeriesService.FIRST_PERIOD_CODE, symbol, firstPeriodTrend.toString()));
        long candleEndMilliseconds = candleRecord.getCtm() + 60_000L;
        for (StrategyWithIndicators strategy : strategies) {
            boolean shouldEnter = strategy.shouldEnter(endIndex);
            boolean shouldExit = strategy.shouldExit(endIndex);
            LOG.info(String.format("Strategy: %s should enter %s or exit %s", strategy.getName(), shouldEnter, shouldExit));
            signalsFacade.addSignal(symbol, strategy.getName(), firstPeriodTrend, shouldEnter, shouldExit, candleEndMilliseconds);       
        }
    }
}
