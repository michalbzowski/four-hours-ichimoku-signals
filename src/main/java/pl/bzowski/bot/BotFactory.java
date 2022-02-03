package pl.bzowski.bot;

import org.jboss.logging.Logger;
import org.ta4j.core.BarSeries;

import pl.bzowski.actualtrends.ActualTrendsFacade;
import pl.bzowski.bot.commands.ChartRangeCommand;
import pl.bzowski.bot.trend.TrendChecker;
import pl.bzowski.candles.CandlesFacade;
import pl.bzowski.signals.SignalsFacade;
import pl.bzowski.trading.SeriesService;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.ChartRangeInfoRecord;
import pro.xstore.api.message.records.RateInfoRecord;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.ChartResponse;

import java.time.Duration;
import java.util.List;

public class BotFactory {

        int ICHIMOKU_MAX_PAST_PERIODS = SeriesService.INITIAL_AND_MAX_CANDLES_COUNT;

        private Logger LOG = Logger.getLogger(BotFactory.class);

        private final SeriesService seriesService;
        private final ChartRangeCommand chartRangeCommand;
        private final TrendChecker trendChecker;

        private ActualTrendsFacade actualTrendsFacade;
        private CandlesFacade candlesFacade;
        private SignalsFacade signalsFacade;

        public BotFactory(SeriesService seriesService, ChartRangeCommand chartRangeCommand, ActualTrendsFacade actualTrendsFacade, CandlesFacade candlesFacade, SignalsFacade signalsFacade) {
                this.seriesService = seriesService;
                this.chartRangeCommand = chartRangeCommand;
                this.actualTrendsFacade = actualTrendsFacade;
                this.candlesFacade = candlesFacade;
                this.signalsFacade = signalsFacade;
                this.trendChecker = new TrendChecker(seriesService);
        }

        public IchimokuTrendAndSignalBot createBotInstance(SymbolRecord symbolRecord) {
                try {
                        String symbol = symbolRecord.getSymbol();

                        long firstPeriodMillisDuration = Duration.ofMinutes(SeriesService.FIRST_PERIOD_CODE.getCode()).toMillis();
                        ChartResponse firstPeriodChartResponse = getArchiveCandles(symbol, SeriesService.FIRST_PERIOD_CODE, firstPeriodMillisDuration);
                        List<RateInfoRecord> firstPeriodRateInfos = firstPeriodChartResponse.getRateInfos();
                        candlesFacade.saveAll(firstPeriodRateInfos, firstPeriodChartResponse.getDigits(), symbol, SeriesService.FIRST_PERIOD_CODE);
                        seriesService.fillSeries(firstPeriodRateInfos, firstPeriodChartResponse.getDigits(), symbol, SeriesService.FIRST_PERIOD_CODE);

                        // long secondPeriodMillisDuration = Duration.ofMinutes(SeriesService.SECOND_PERIOD_CODE.getCode()).toMillis();
                        // ChartResponse secondPeriodChartResponse = getArchiveCandles(symbol, SeriesService.SECOND_PERIOD_CODE, secondPeriodMillisDuration);
                        // List<RateInfoRecord> secondPeriodRateInfos = secondPeriodChartResponse.getRateInfos();
                        // candlesFacade.saveAll(secondPeriodRateInfos, secondPeriodChartResponse.getDigits(), symbol, SeriesService.SECOND_PERIOD_CODE);
                        // seriesService.fillSeries(secondPeriodRateInfos, secondPeriodChartResponse.getDigits(), symbol, SeriesService.SECOND_PERIOD_CODE);

                        // long thirdPeriodMillisDuration = Duration.ofMinutes(SeriesService.THIRD_PERIOD_CODE.getCode()).toMillis();
                        // ChartResponse thirdPeriodChartResponse = getArchiveCandles(symbol, SeriesService.THIRD_PERIOD_CODE, thirdPeriodMillisDuration);
                        // List<RateInfoRecord> thirdPeriodRateInfos = thirdPeriodChartResponse.getRateInfos();
                        // candlesFacade.saveAll(thirdPeriodRateInfos,  thirdPeriodChartResponse.getDigits(), symbol, SeriesService.THIRD_PERIOD_CODE);
                        // seriesService.fillSeries(thirdPeriodRateInfos, thirdPeriodChartResponse.getDigits(), symbol, SeriesService.THIRD_PERIOD_CODE);

                        return new IchimokuTrendAndSignalBot(symbol,
                                        seriesService.createSeriesFor(symbol).get(SeriesService.FIRST_PERIOD_CODE),
                                        trendChecker, actualTrendsFacade, signalsFacade);
                } catch (APIErrorResponse | APICommunicationException | APIReplyParseException
                                | APICommandConstructionException apiErrorResponse) {
                        apiErrorResponse.printStackTrace();
                }
                return null;
        }

        private ChartResponse getArchiveCandles(String symbol, PERIOD_CODE periodCode, long duration)
                        throws APIErrorResponse, APICommunicationException, APIReplyParseException,
                        APICommandConstructionException {
                long NOW = System.currentTimeMillis();
                
                long fromTime = NOW - duration * ICHIMOKU_MAX_PAST_PERIODS;
        
                ChartRangeInfoRecord record = new ChartRangeInfoRecord(symbol, periodCode, fromTime, NOW);
                //
                return chartRangeCommand.execute(record);
        }
}