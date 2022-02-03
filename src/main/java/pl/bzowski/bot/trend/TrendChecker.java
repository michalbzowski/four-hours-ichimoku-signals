package pl.bzowski.bot.trend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuSenkouSpanAIndicator;
import org.ta4j.core.indicators.ichimoku.IchimokuSenkouSpanBIndicator;
import org.ta4j.core.rules.InPipeRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import pl.bzowski.trading.SeriesService;
import pro.xstore.api.message.codes.PERIOD_CODE;

import java.security.InvalidParameterException;

public class TrendChecker {

    private Logger logger = LoggerFactory.getLogger(TrendChecker.class);

    private final SeriesService seriesService;

    public TrendChecker(SeriesService seriesService) {
        this.seriesService = seriesService;
    }

    public Trend checkTrend(String symbol, PERIOD_CODE periodCode) {
        BarSeries series = seriesService.createSeriesFor(symbol).get(periodCode);
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        IchimokuSenkouSpanAIndicator spanA = new IchimokuSenkouSpanAIndicator(series);
        IchimokuSenkouSpanBIndicator spanB = new IchimokuSenkouSpanBIndicator(series);
        int endIndex = series.getEndIndex();
        Bar lastBar = series.getLastBar();
        logger.debug("End index: {} Symbol: {}, PC: {}. B: {}, E: {}, P: {}, CPI: {}", endIndex, symbol, periodCode,
                lastBar.getBeginTime(), lastBar.getEndTime(), lastBar.getTimePeriod().toMinutes(),
                lastBar.getClosePrice());
        logger.debug("CPI: {}", closePriceIndicator.getValue(endIndex));
        logger.debug("A: {}", spanA.getValue(endIndex));
        logger.debug("B: {}", spanB.getValue(endIndex));
        OverIndicatorRule cpiOverSpanA = new OverIndicatorRule(closePriceIndicator, spanA);
        // logger.info("CPIOverSpanA: {}", cpiOverSpanA.isSatisfied(endIndex));
        OverIndicatorRule cpiOverSpanB = new OverIndicatorRule(closePriceIndicator, spanB);
        // logger.info("CPIOverSpanB: {}", cpiOverSpanB.isSatisfied(endIndex));
        if (cpiOverSpanA.isSatisfied(endIndex) && cpiOverSpanB.isSatisfied(endIndex)) {
            return new BullishTrend();
        } else if (cpiOverSpanA.isSatisfied(endIndex) || cpiOverSpanB.isSatisfied(endIndex)) {
            return new NoTrend();
        } else if (!cpiOverSpanA.isSatisfied(endIndex) && !cpiOverSpanB.isSatisfied(endIndex)) {
            return new BearishTrend();
        }
        throw new InvalidParameterException("Invalid trend?");
    }
}
