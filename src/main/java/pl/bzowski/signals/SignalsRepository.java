package pl.bzowski.signals;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jdbi.v3.core.Jdbi;

import io.agroal.api.AgroalDataSource;
import pl.bzowski.bot.trend.Trend;

@ApplicationScoped
public class SignalsRepository {

  private Jdbi jdbi;

  @Inject
  public SignalsRepository(AgroalDataSource defaultDataSource) {
    this.jdbi = Jdbi.create(defaultDataSource);
  }

  public void addSignal(String symbol, String name, Trend trend, boolean shouldEnter, boolean shouldExit, long signalTime) {
    jdbi.useHandle(handle -> {
      LocalDateTime ofInstant = LocalDateTime.ofInstant(Instant.ofEpochMilli(signalTime), ZoneId.systemDefault());
      ZonedDateTime atZone = ofInstant.atZone(ZoneId.systemDefault());
      handle.createUpdate("INSERT INTO signals VALUES(:symbol, :name, :trend::TREND, :shouldEnter, :shouldExit, :signalTime)")
      .bind("symbol", symbol)
      .bind("name", name)
      .bind("trend", trend.toString())
      .bind("shouldEnter", shouldEnter)
      .bind("shouldExit", shouldExit)
      .bind("signalTime", Timestamp.valueOf(atZone.toLocalDateTime()))
      .execute();
    });
  }

}
