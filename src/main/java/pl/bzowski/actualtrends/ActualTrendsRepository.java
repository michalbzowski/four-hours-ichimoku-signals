package pl.bzowski.actualtrends;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jdbi.v3.core.Jdbi;

import io.agroal.api.AgroalDataSource;

@ApplicationScoped
public class ActualTrendsRepository {

  private Jdbi jdbi;

  @Inject
  public ActualTrendsRepository(AgroalDataSource defaultDataSource) {
    jdbi = Jdbi.create(defaultDataSource);

  }

  public void update(String symbol, long duration, String trend) {
    jdbi.useHandle(handle -> {
      handle.createUpdate(
        "insert into actual_trends(symbol, trend, duration) values(:symbol, :trend::TREND, :duration) ON CONFLICT  ON CONSTRAINT sym_dur_u_constraint   DO UPDATE SET trend = :trend::TREND WHERE actual_trends.symbol = :symbol AND actual_trends.duration = :duration")
        .bind("trend", trend.toString())
        .bind("symbol", symbol)
        .bind("duration", duration)
        .execute();
    });
  }

}
