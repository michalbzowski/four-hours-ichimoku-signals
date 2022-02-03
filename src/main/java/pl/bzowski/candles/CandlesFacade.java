package pl.bzowski.candles;

import java.time.ZonedDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ta4j.core.BaseBar;

import pro.xstore.api.message.codes.PERIOD_CODE;
import pro.xstore.api.message.records.RateInfoRecord;

@ApplicationScoped
public class CandlesFacade {

  private CandlesRepository candlesRepository;

  @Inject
  public CandlesFacade(CandlesRepository candlesRepository) {
    this.candlesRepository = candlesRepository;

  }

  public void saveAll(List<RateInfoRecord> rateInfos, int digits, String symbol, PERIOD_CODE periodCode) {
    // candlesRepository.saveAll(rateInfos, digits, symbol, periodCode);
  }

  public void addPrice(String symbol, PERIOD_CODE periodCode, ZonedDateTime begining, double close) {
    // candlesRepository.addPrice(symbol, periodCode, begining, close);
  }

  public void addBar(String symbol, PERIOD_CODE periodCode, BaseBar newBar) {
    // candlesRepository.addBar(symbol, periodCode, newBar);
  }
  
}
