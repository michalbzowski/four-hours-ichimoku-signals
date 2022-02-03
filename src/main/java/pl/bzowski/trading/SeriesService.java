package pl.bzowski.trading;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.aggregator.BarAggregator;
import org.ta4j.core.aggregator.BaseBarSeriesAggregator;
import org.ta4j.core.aggregator.DurationBarAggregator;
import org.ta4j.core.num.DecimalNum;

import pl.bzowski.candles.CandlesFacade;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.records.RateInfoRecord;
import pro.xstore.api.message.records.SCandleRecord;

@ApplicationScoped
public class SeriesService {

  public static final int INITIAL_AND_MAX_CANDLES_COUNT = 128;
  public static final PERIOD_CODE FIRST_PERIOD_CODE = PERIOD_CODE.PERIOD_H4;
  // public static final PERIOD_CODE SECOND_PERIOD_CODE = PERIOD_CODE.PERIOD_D1;
  // public static final PERIOD_CODE THIRD_PERIOD_CODE = PERIOD_CODE.PERIOD_W1;

  private static final Logger LOG = Logger.getLogger(SeriesService.class);
  

  private final Map<String, BarSeries> firstPeriodSeries;
  // private final Map<String, BarSeries> secondPeriodSeries;
  // private final Map<String, BarSeries> thirdPeriodSeries;
  private CandlesFacade candlesFacade;

  @Inject
  public SeriesService(CandlesFacade candlesFacade) {
    this.candlesFacade = candlesFacade;
    this.firstPeriodSeries = new HashMap<>(INITIAL_AND_MAX_CANDLES_COUNT);
    // this.secondPeriodSeries = new HashMap<>(INITIAL_AND_MAX_CANDLES_COUNT);
    // this.thirdPeriodSeries = new HashMap<>(INITIAL_AND_MAX_CANDLES_COUNT);
  }

  public Map<PERIOD_CODE, BarSeries> createSeriesFor(String symbol) {
    if (this.firstPeriodSeries.containsKey(symbol)) {
      return Map.of(
          FIRST_PERIOD_CODE, firstPeriodSeries.get(symbol)
          // ,
          // SECOND_PERIOD_CODE, secondPeriodSeries.get(symbol),
          // THIRD_PERIOD_CODE, thirdPeriodSeries.get(symbol)
          );
    }
    BarSeries firstPeriodBarSeries = new BaseBarSeries(symbol);
    firstPeriodBarSeries.setMaximumBarCount(INITIAL_AND_MAX_CANDLES_COUNT);
    // BarSeries secondPeriodBarSeries = new BaseBarSeries(symbol);
    // secondPeriodBarSeries.setMaximumBarCount(INITIAL_AND_MAX_CANDLES_COUNT);
    // BarSeries thirdPeriodBarSeries = new BaseBarSeries(symbol);
    // thirdPeriodBarSeries.setMaximumBarCount(INITIAL_AND_MAX_CANDLES_COUNT);
    this.firstPeriodSeries.put(symbol, firstPeriodBarSeries);
    // this.secondPeriodSeries.put(symbol, secondPeriodBarSeries);
    // this.thirdPeriodSeries.put(symbol, thirdPeriodBarSeries);
    return Map.of(
        FIRST_PERIOD_CODE, firstPeriodSeries.get(symbol)
        // ,
        // SECOND_PERIOD_CODE, secondPeriodSeries.get(symbol),
        // THIRD_PERIOD_CODE, thirdPeriodSeries.get(symbol)
        );
  }

  public void fillSeries(List<RateInfoRecord> archiveCandles, int digits, String symbol, PERIOD_CODE periodCode) {
    BarSeries series = createSeriesFor(symbol).get(periodCode);
    
    LOG.info(String.format("%s:  Candles count: %s. Period %s minutes", symbol, archiveCandles.size(), periodCode.getCode()));
    archiveCandles.forEach(record -> {
      BaseBar bar = getBaseBarWithCountedPrices(periodCode, digits, record);

      series.addBar(bar);
    });
  }

  public static BaseBar getBaseBarWithCountedPrices(PERIOD_CODE periodCode, int digits, RateInfoRecord record) {
    BigDecimal divider = BigDecimal.valueOf(Math.pow(10, digits));
    long ctm = record.getCtm();
    long code = periodCode.getCode();
    BigDecimal o = BigDecimal.valueOf(record.getOpen());
    BigDecimal c = BigDecimal.valueOf(record.getClose());
    BigDecimal h = BigDecimal.valueOf(record.getHigh());
    BigDecimal l = BigDecimal.valueOf(record.getLow());
    BigDecimal close = o.add(c).divide(divider);
    BigDecimal open = o.divide(divider);
    BigDecimal high = o.add(h).divide(divider);
    BigDecimal low = o.add(l).divide(divider);
    BaseBar bar = getBaseBar(code, ctm, close, open, high, low);
    return bar;
  }

  public int updateSeriesWithOneMinuteCandle(SCandleRecord record) {
    BarSeries first = firstPeriodSeries.get(record.getSymbol());
    int endIndex = incorporateNewCandleRecordIntoSeries(first, FIRST_PERIOD_CODE, record);

    // BarSeries second = secondPeriodSeries.get(record.getSymbol());
    // incorporateNewCandleRecordIntoSeries(second, SECOND_PERIOD_CODE, record);

    // BarSeries third = thirdPeriodSeries.get(record.getSymbol());
    // incorporateNewCandleRecordIntoSeries(third, THIRD_PERIOD_CODE, record);

    return endIndex;
  }

  private int incorporateNewCandleRecordIntoSeries(BarSeries series, PERIOD_CODE periodCode, SCandleRecord record) {
    // Ostatni bar z serii może być jeszcze w trakcie rysowania
    LocalDateTime ofInstant = LocalDateTime.ofInstant(Instant.ofEpochMilli(record.getCtm()), ZoneId.systemDefault());
    ZonedDateTime atZone = ofInstant.atZone(ZoneId.systemDefault());
    if(series.isEmpty()){
      LOG.info(String.format("Empty series. New bar added to %s period series", periodCode.getCode()));
      BaseBar newBar = getBaseBar(periodCode.getCode(), record.getCtm(), BigDecimal.valueOf(record.getClose()),
          BigDecimal.valueOf(record.getOpen()),
          BigDecimal.valueOf(record.getHigh()), BigDecimal.valueOf(record.getLow()));
      candlesFacade.addBar(series.getName(), periodCode, newBar);
      series.addBar(newBar);
      if(!newBar.inPeriod(atZone.plus(Duration.ofMinutes(1)))) {
        return series.getEndIndex();//Next minute price will be new bar
      } else {
        return -1; // It means that next minute price belongs tp same bar
      }
    }
    Bar lastBar = series.getLastBar();

    if (lastBar.inPeriod(atZone)) {
      double close = record.getClose();
      candlesFacade.addPrice(series.getName(), periodCode, lastBar.getBeginTime(), close);
      series.addPrice(close);
      LOG.debug(String.format("Series of %s new close price", periodCode.getCode()));
      if(!lastBar.inPeriod(atZone.plus(Duration.ofMinutes(1)))) {
        return series.getEndIndex();//Next minute price will be new bar
      } else {
        return -1; // It means that next minute price belongs tp same bar
      }
    } else {
      LOG.info(String.format("New bar added to %s period series", periodCode.getCode()));
      BaseBar newBar = getBaseBar(periodCode.getCode(), record.getCtm(), BigDecimal.valueOf(record.getClose()),
          BigDecimal.valueOf(record.getOpen()),
          BigDecimal.valueOf(record.getHigh()), BigDecimal.valueOf(record.getLow()));
      candlesFacade.addBar(series.getName(), periodCode, newBar);
      series.addBar(newBar);
    }
    return -1;
  }

  public BarSeries convertToPeriod(String symbol, PERIOD_CODE periodCode) {
    BarAggregator barAggregator = new DurationBarAggregator(Duration.ofMinutes(periodCode.getCode()));
    BaseBarSeriesAggregator baseBarSeriesAggregator = new BaseBarSeriesAggregator(barAggregator);
    BarSeries series = firstPeriodSeries.get(symbol);
    return baseBarSeriesAggregator.aggregate(series);
  }

  private static BaseBar getBaseBar(long code, long ctm, BigDecimal close, BigDecimal open, BigDecimal high, BigDecimal low) {
    Duration timePeriod = Duration.ofMinutes(code);// .minus(Duration.ofMillis(1));
    LocalDateTime endTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(ctm).plus(timePeriod), ZoneId.systemDefault());
    ZonedDateTime endTimeAtZone = endTime.atZone(ZoneId.systemDefault());
    return BaseBar.builder()
        .closePrice(DecimalNum.valueOf(close))
        .openPrice(DecimalNum.valueOf(open))
        .highPrice(DecimalNum.valueOf(high))
        .lowPrice(DecimalNum.valueOf(low))
        .endTime(endTimeAtZone)
        .timePeriod(timePeriod)
        .build();
  }
}
