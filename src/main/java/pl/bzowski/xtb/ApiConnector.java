package pl.bzowski.xtb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import pl.bzowski.actualtrends.ActualTrendsFacade;
import pl.bzowski.bot.BotFactory;
import pl.bzowski.bot.IchimokuTrendAndSignalBot;
import pl.bzowski.bot.TradeBotStreamListener;
import pl.bzowski.bot.commands.ChartRangeCommand;
import pl.bzowski.candles.CandlesFacade;
import pl.bzowski.signals.SignalsFacade;
import pl.bzowski.trading.SeriesService;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.AllSymbolsResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.SyncAPIConnector;
import pro.xstore.api.sync.ServerData.ServerEnum;

@Startup
@ApplicationScoped
public class ApiConnector {

  private static final Logger LOG = Logger.getLogger(ApiConnector.class);
  private final Map<String, IchimokuTrendAndSignalBot> bots = new HashMap<>();

  private final SeriesService seriesService;
  private SyncAPIConnector connector;

  private ActualTrendsFacade actualTrendsFacade;
  private CandlesFacade candlesFacade;
  private SignalsFacade signalsFacade;
  private Credentials credentials;

  @Inject
  public ApiConnector(SeriesService seriesService, ActualTrendsFacade actualTrendsFacade, CandlesFacade candlesFacade, SignalsFacade signalsFacade, Credentials credentials) {
    this.seriesService = seriesService;
    this.actualTrendsFacade = actualTrendsFacade;
    this.candlesFacade = candlesFacade;
    this.signalsFacade = signalsFacade;
    this.credentials = credentials;
  }

  @PostConstruct
  void init() {
    try {
      this.connector = new SyncAPIConnector(ServerEnum.DEMO);
      LoginResponse loginResponse = APICommandFactory.executeLoginCommand(connector, credentials);
      if (loginResponse.getStatus()) {
        AllSymbolsResponse allSymbolsResponse = APICommandFactory.executeAllSymbolsCommand(connector);
        Set<SymbolRecord> symbolRecords = allSymbolsResponse.getSymbolRecords()
            .stream()
            .collect(Collectors.toSet());
        ChartRangeCommand chartRangeCommand = new ChartRangeCommand(connector);

        BotFactory botFactory = new BotFactory(seriesService, chartRangeCommand, actualTrendsFacade, candlesFacade,
            signalsFacade);

        int i = 0;
        for (SymbolRecord symbolRecord : symbolRecords) {
          IchimokuTrendAndSignalBot botInstance = botFactory.createBotInstance(symbolRecord);
          bots.put(symbolRecord.getSymbol(), botInstance);
          LOG.info(String.format("%s of %s", ++i, symbolRecords.size()));
          logMemoryUsage();
        }
      }
    } catch (IOException | APICommandConstructionException | APICommunicationException | APIReplyParseException
        | APIErrorResponse e) {
      LOG.error(e.getMessage());
    }

    TradeBotStreamListener tradeBotStreamListener = new TradeBotStreamListener(bots, seriesService);
    try {
      connector.connectStream(tradeBotStreamListener);
      connector.subscribeKeepAlive();
      bots.forEach((key, value) -> {
        try {
          connector.subscribeCandle(key);
        } catch (APICommunicationException e) {
          LOG.error(e.getLocalizedMessage());
        }
      });
    } catch (IOException | APICommunicationException e) {
      LOG.error(e.getLocalizedMessage());
    }
  }

  @Scheduled(every = "5m")
  public void pingXtb() {
    try {
      APICommandFactory.executePingCommand(connector);
      LOG.info("Ping!");
		} catch (Exception e) {
			LOG.warn(e.getLocalizedMessage());
		}
  }

  @PreDestroy
  void destroy() {
    try {
      bots.keySet().forEach(symbol -> {
        try {
          connector.unsubscribeCandle(symbol);
          connector.unsubscribeKeepAlive();
          connector.disconnectStream();
        } catch (APICommunicationException e) {
          e.printStackTrace();
        }
      });
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      connector.disconnectStream();
    }
  }

  private void logMemoryUsage() {
    Runtime rt = Runtime.getRuntime();
    long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
    LOG.info(String.format("Memory usage %s MB", usedMB));
  }
}
