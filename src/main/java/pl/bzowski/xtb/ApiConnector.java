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

import pro.xstore.api.sync.SyncAPIConnector;
import pro.xstore.api.sync.ServerData.ServerEnum;

@Startup
@ApplicationScoped
public class ApiConnector {

  private static final Logger LOG = Logger.getLogger(ApiConnector.class);
  private final Map<String, IchimokuTrendAndSignalBot> bots = new HashMap<>();

  private final SeriesService seriesService;

  private ActualTrendsFacade actualTrendsFacade;
  private CandlesFacade candlesFacade;
  private SignalsFacade signalsFacade;
  private XStationFacade xStationFacade;

  @Inject
  public ApiConnector(SeriesService seriesService, ActualTrendsFacade actualTrendsFacade, CandlesFacade candlesFacade,
      SignalsFacade signalsFacade, XStationFacade xStationFacade) {
    this.seriesService = seriesService;
    this.actualTrendsFacade = actualTrendsFacade;
    this.candlesFacade = candlesFacade;
    this.signalsFacade = signalsFacade;
    this.xStationFacade = xStationFacade;

  }

  @PostConstruct
  void init() {

    AllSymbolsResponse allSymbolsResponse = xStationFacade.allSymbols();

    Set<SymbolRecord> symbolRecords = allSymbolsResponse.getSymbolRecords()
        .stream()
        .filter(p -> !p.isLongOnly() && p.isCurrencyPair())
        .collect(Collectors.toSet());

    BotFactory botFactory = new BotFactory(seriesService, xStationFacade, actualTrendsFacade, candlesFacade,
        signalsFacade);

    int i = 0;
    for (SymbolRecord symbolRecord : symbolRecords) {
      IchimokuTrendAndSignalBot botInstance = botFactory.createBotInstance(symbolRecord);
      bots.put(symbolRecord.getSymbol(), botInstance);
      LOG.info(String.format("%s of %s", ++i, symbolRecords.size()));
      logMemoryUsage();
    }

    TradeBotStreamListener tradeBotStreamListener = new TradeBotStreamListener(bots, seriesService);
    xStationFacade.connect(bots, tradeBotStreamListener);
  }

  @Scheduled(every = "5m")
  public void pingXtb() {
    try {
      xStationFacade.ping();
      LOG.info("Ping!");
    } catch (Exception e) {
      LOG.warn(e.getLocalizedMessage());
    }
  }

  @PreDestroy
  void destroy() {
    xStationFacade.destroy(bots);
  }

  private void logMemoryUsage() {
    Runtime rt = Runtime.getRuntime();
    long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
    LOG.info(String.format("Memory usage %s MB", usedMB));
  }
}
