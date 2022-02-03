package pl.bzowski.actualtrends;

import java.util.UUID;

public class CandleIdBegining {

  private UUID candleId;
  private Long candleBeginingTime;

  public CandleIdBegining(UUID candleId, Long candleBeginingTime) {
    this.candleId = candleId;
    this.candleBeginingTime = candleBeginingTime;
  }

  public UUID getCandleId() {
    return candleId;
  }
  
  public Long getCandleBeginingTime() {
    return candleBeginingTime;
  }

}
