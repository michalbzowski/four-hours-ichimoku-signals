package pl.bzowski.actualtrends;

import javax.enterprise.context.ApplicationScoped;

import pl.bzowski.bot.trend.Trend;

@ApplicationScoped
public class ActualTrendsFacade {

  private ActualTrendsRepository actualTrendsRepository;
  
  public ActualTrendsFacade(ActualTrendsRepository actualTrendsRepository) {
    this.actualTrendsRepository = actualTrendsRepository;

  }

  public void setActualTrend(String symbol, long periodCode, Trend trend) {
    String trendDatabaseEnumCode = trend.toString();
    actualTrendsRepository.update(symbol, periodCode, trendDatabaseEnumCode);
  }
}
