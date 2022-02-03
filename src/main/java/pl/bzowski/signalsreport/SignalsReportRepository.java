package pl.bzowski.signalsreport;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Slf4JSqlLogger;

import io.agroal.api.AgroalDataSource;

@ApplicationScoped
public class SignalsReportRepository {

  private Jdbi jdbi;

  @Inject
  public SignalsReportRepository(AgroalDataSource defaultDataSource) {
    jdbi = Jdbi.create(defaultDataSource);
  }

  public Collection<Signal> getSingals(ZonedDateTime signalTime) {
    return jdbi.withHandle(handle -> {
      return handle.createQuery("SELECT symbol, name, trend, should_enter, should_exit FROM signals WHERE signal_time = :signalTime  AND (should_enter = true OR should_exit = true)")
          .bind("signalTime", Timestamp.valueOf(signalTime.toLocalDateTime()))
          .map((rs, ctx) -> new Signal(rs.getString("symbol"), rs.getString("name"), rs.getString("trend"), rs.getBoolean("should_enter"), rs.getBoolean("should_exit")))
          .list();
    });
  }
}
