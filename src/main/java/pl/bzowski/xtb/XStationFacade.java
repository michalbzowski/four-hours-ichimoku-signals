package pl.bzowski.xtb;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.resource.spi.IllegalStateException;

import org.jboss.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import pl.bzowski.bot.IchimokuTrendAndSignalBot;
import pl.bzowski.bot.TradeBotStreamListener;
import pl.bzowski.bot.commands.ChartRangeCommand;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.ChartRangeInfoRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.AllSymbolsResponse;
import pro.xstore.api.message.response.ChartResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.SyncAPIConnector;
import pro.xstore.api.sync.ServerData.ServerEnum;
import org.eclipse.microprofile.faulttolerance.Retry;

@ApplicationScoped
public class XStationFacade {

  private static final Logger LOG = Logger.getLogger(XStationFacade.class);

  private SyncAPIConnector connector;
  private Credentials credentials;

  private LoginResponse loginResponse;

  @Inject
  public XStationFacade(Credentials credentials) {
    this.credentials = credentials;

  }

  @PostConstruct
  public void init() {
    signIn();
  }

  @Retry(maxRetries = 4)
  public void signIn() {
    try {
      this.connector = new SyncAPIConnector(ServerEnum.DEMO);
      loginResponse = APICommandFactory.executeLoginCommand(connector, credentials);
    } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException | APIErrorResponse
        | IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
    if (!loginResponse.getStatus()) {
      throw new RuntimeException("You are not signed in! We will retry");
    }
  }

  @Retry(maxRetries = 4)
  public AllSymbolsResponse allSymbols() {
    AllSymbolsResponse allSymbolsResponse = null;
    try {
      allSymbolsResponse = APICommandFactory.executeAllSymbolsCommand(connector);
    } catch (APICommandConstructionException | APIReplyParseException | APICommunicationException | APIErrorResponse e) {
      LOG.error(e.getLocalizedMessage(), e);
      throw new RuntimeException("Some error occurr during getting all symbols. We shold retry!");
    }
    return allSymbolsResponse;
  }

  @Retry(maxRetries = 4)
  public ChartResponse chartRange(ChartRangeInfoRecord record) {
    ChartRangeCommand chartRangeCommand = new ChartRangeCommand(connector);
    try {
      ChartResponse chartResponse = chartRangeCommand.execute(record);
      String body = chartResponse.getBody();
      try (FileWriter file = new FileWriter(String.format("C:\\Users\\mbzowski\\IdeaProjects\\ichimoku-signals\\testdata\\%s_%s_%s_%s", record.getSymbol(), record.getPeriod().getCode(), record.getStart(), record.getEnd()))) {
        file.write(body);
    } catch (IOException e) {
        e.printStackTrace();
    }
      return chartResponse;
    } catch (APICommandConstructionException | APIReplyParseException | APIErrorResponse | APICommunicationException e) {
      throw new RuntimeException("Some error occurr during getting all symbolchart range. We shold retry!");
    }
  }

  public void ping() {
    try {
      APICommandFactory.executePingCommand(connector);
    } catch (APICommandConstructionException | APICommunicationException | APIReplyParseException
        | APIErrorResponse e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  void destroy(Map<String, IchimokuTrendAndSignalBot> bots) {
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

  public void connect(Map<String, IchimokuTrendAndSignalBot> bots, TradeBotStreamListener tradeBotStreamListener) {
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
      LOG.error(e.getLocalizedMessage(), e);
    }
  }

}
