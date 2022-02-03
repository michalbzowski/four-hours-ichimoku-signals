package pl.bzowski.candles;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.ta4j.core.BaseBar;

import io.agroal.api.AgroalDataSource;
import pl.bzowski.trading.SeriesService;
import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.records.RateInfoRecord;

@ApplicationScoped
public class CandlesRepository {

  private Jdbi jdbi;

  @Inject
  public CandlesRepository(AgroalDataSource defaultDataSource) {
    this.jdbi = Jdbi.create(defaultDataSource);
  }

  public void saveAll(List<RateInfoRecord> rateInfos, int digits, String symbol, PERIOD_CODE periodCode) {
    jdbi.useHandle(handle -> {
      PreparedBatch batch = handle.prepareBatch(
          "INSERT INTO CANDLES (id, reference_id, symbol, duration, begining, price_open, price_close, price_high, price_low, finished) VALUES (:id, :reference_id, :symbol, :duration, :begining, :price_open, :price_close, :price_high, :price_low, :finished) ON CONFLICT DO NOTHING");
      for (RateInfoRecord r : rateInfos) {
        BaseBar bar = SeriesService.getBaseBarWithCountedPrices(periodCode, digits, r);

        Optional<UUID> referenceId = handle.createQuery(
            "SELECT reference_id, max(audit_cd) FROM candles WHERE symbol = :symbol and duration = :duration and begining = :begining and finished =  false group by reference_id")
            .bind("symbol", symbol)
            .bind("duration", periodCode.getCode())
            .bind("begining", Timestamp.valueOf(bar.getBeginTime().toLocalDateTime()))
            .mapTo(UUID.class)
            .findOne();

        UUID uuid = UUID.randomUUID();
        UUID oldCandleUuid;
        if (referenceId.isPresent()) {
          oldCandleUuid = referenceId.get();
        } else {
          oldCandleUuid = uuid;
        }
        batch.bind("id", uuid)
            .bind("reference_id", oldCandleUuid)
            .bind("symbol", symbol)
            .bind("duration", periodCode.getCode())
            .bind("begining", Timestamp.valueOf(bar.getBeginTime().toLocalDateTime()))
            .bind("price_open", BigDecimal.valueOf(bar.getOpenPrice().doubleValue()))
            .bind("price_close", BigDecimal.valueOf(bar.getClosePrice().doubleValue()))
            .bind("price_high", BigDecimal.valueOf(bar.getHighPrice().doubleValue()))
            .bind("price_low", BigDecimal.valueOf(bar.getLowPrice().doubleValue()))
            .bind("finished", !bar.inPeriod(ZonedDateTime.now()))
            .add();
      }
      batch.execute();
    });

  }

  public void addPrice(String symbol, PERIOD_CODE periodCode, ZonedDateTime begiDateTime, double close) {
    jdbi.useHandle(handle -> {
      handle.createUpdate(
          "INSERT INTO candles SELECT uuid_generate_v4(), reference_id, symbol, duration, begining, price_open, :priceClose, CASE WHEN :priceClose > price_high THEN :priceClose ELSE price_high END , CASE WHEN price_low > :priceClose THEN :priceClose ELSE price_low END, :finished FROM candles " +
          "WHERE symbol = :symbol and duration = :duration and begining = :begining  ")
          .bind("priceClose", BigDecimal.valueOf(close))
          .bind("finished", false)
          .bind("symbol", symbol)
          .bind("duration", periodCode.getCode())
          .bind("begining", Timestamp.valueOf(begiDateTime.toLocalDateTime()))
          .execute();

    });
  }

  public void addBar(String symbol, PERIOD_CODE periodCode, BaseBar newBar) {
    jdbi.useHandle(handle -> {
      // zamkniecie bara po przez skopiowanie wartosci ale ustawienie finished na true
      // jezeli podaczas startu aplikacji zostal juz dodany taki bar, to sprawdzam to
      int finishedBarCount = handle.createQuery(
          "SELECT count(1) FROM candles WHERE symbol = :symbol and duration = :duration and begining = :begining and finished = true")
          .bind("symbol", symbol)
          .bind("duration", periodCode.getCode())
          .bind("begining", newBar.getBeginTime().minus(Duration.ofMinutes(periodCode.getCode())))
          .mapTo(Integer.class)
          .one();
      if (finishedBarCount == 0) {
        handle.createUpdate(
            " INSERT INTO candles " +
            " SELECT  uuid_generate_v4() as id, reference_id, symbol, duration, begining, price_open, price_close, price_high, price_low, true " +
            " FROM (" +
            " SELECT id, reference_id, symbol, duration, begining, price_open, price_close, price_high, price_low, true, ROW_NUMBER () OVER (PARTITION BY symbol, duration, begining ORDER BY audit_cd desc) as rn " +
            " FROM candles  WHERE symbol = :symbol and duration = :duration and begining = :begining and finished = false order by audit_cd desc ) ordered_candles " +
            " where rn = 1 ")
            .bind("symbol", symbol)
            .bind("duration", periodCode.getCode())
            .bind("begining", newBar.getBeginTime().minus(Duration.ofMinutes(periodCode.getCode())))
            .execute();
      }
      UUID uuid = UUID.randomUUID();
      handle.createUpdate(
          "INSERT INTO candles VALUES(:id, :referenceId, :symbol, :duration, :begining, :priceOpen, :priceClose, :priceHigh, :priceLow, :finished)")
          .bind("id", uuid)
          .bind("referenceId", uuid)
          .bind("symbol", symbol)
          .bind("duration", newBar.getTimePeriod().toMinutes())
          .bind("begining", Timestamp.valueOf(newBar.getBeginTime().toLocalDateTime()))
          .bind("priceOpen", newBar.getOpenPrice().doubleValue())
          .bind("priceClose", newBar.getClosePrice().doubleValue())
          .bind("priceHigh", newBar.getHighPrice().doubleValue())
          .bind("priceLow", newBar.getLowPrice().doubleValue())
          .bind("finished", false)
          .execute();

    });
  }

}
