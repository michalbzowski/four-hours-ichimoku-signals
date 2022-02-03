package pl.bzowski.bot;
 
import pl.bzowski.trading.SeriesService;
import pro.xstore.api.message.records.SCandleRecord;
import pro.xstore.api.message.records.SKeepAliveRecord;
import pro.xstore.api.streaming.StreamingListener;

import java.util.Map;

import org.jboss.logging.Logger;

public class TradeBotStreamListener extends StreamingListener {

    private final Map<String, IchimokuTrendAndSignalBot> bots;
    private final SeriesService seriesService;
    Logger LOG = Logger.getLogger(TradeBotStreamListener.class);

    public TradeBotStreamListener(Map<String, IchimokuTrendAndSignalBot> strategies, SeriesService seriesService) {
        this.bots = strategies;
        this.seriesService = seriesService;
    }

    @Override
    public void receiveCandleRecord(SCandleRecord candleRecord) {
        LOG.debug("Stream candle record: " + candleRecord);
        IchimokuTrendAndSignalBot ichimokuTrendAndSignalBot = bots.get(candleRecord.getSymbol());
        int endIndex = seriesService.updateSeriesWithOneMinuteCandle(candleRecord);
        if (endIndex > 0) { 
            ichimokuTrendAndSignalBot.onTick(endIndex, candleRecord);
        }
    }

    public void receiveKeepAliveRecord(SKeepAliveRecord keepAliveRecord) {
        LOG.debug("Keep alive: " + keepAliveRecord.getTimestamp());
    }
}
