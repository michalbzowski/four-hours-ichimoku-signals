package pl.bzowski.candles;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.UUID;

public class CandleDto {

  UUID id;
  UUID referenceId;
  String symbol;
  Integer duration;
  Timestamp begining;
  BigDecimal priceOpen;
  BigDecimal priceClose;
  BigDecimal priceHigh;
  BigDecimal priceLow;
  Boolean finished;

  public CandleDto() {
    //
  }

  @ConstructorProperties({"id", "referenceId", "symbol", "duration", "begining", "priceOpen", "priceClose", "priceHigh", "priceLow", "finished"})
  public CandleDto(UUID id, UUID referenceId, String symbol, Integer duration, Timestamp begining, BigDecimal priceOpen,
      BigDecimal priceClose, BigDecimal priceHigh, BigDecimal priceLow, Boolean finished) {
    this.id = id;
    this.referenceId = referenceId;
    this.symbol = symbol;
    this.duration = duration;
    this.begining = begining;
    this.priceOpen = priceOpen;
    this.priceClose = priceClose;
    this.priceHigh = priceHigh;
    this.priceLow = priceLow;
    this.finished = finished;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(UUID referenceId) {
    this.referenceId = referenceId;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public Timestamp getBegining() {
    return begining;
  }

  public void setBegining(Timestamp begining) {
    this.begining = begining;
  }

  public BigDecimal getPriceOpen() {
    return priceOpen;
  }

  public void setPriceOpen(BigDecimal priceOpen) {
    this.priceOpen = priceOpen;
  }

  public BigDecimal getPriceClose() {
    return priceClose;
  }

  public void setPriceClose(BigDecimal priceClose) {
    this.priceClose = priceClose;
    if(this.priceHigh.compareTo(priceClose) < 0) {
      this.priceHigh = priceClose;
    } else if (this.priceLow.compareTo(priceClose) > 0) {
      this.priceLow = priceClose;
    }
  }

  public BigDecimal getPriceHigh() {
    return priceHigh;
  }

  public void setPriceHigh(BigDecimal priceHigh) {
    this.priceHigh = priceHigh;
  }

  public BigDecimal getPriceLow() {
    return priceLow;
  }

  public void setPriceLow(BigDecimal priceLow) {
    this.priceLow = priceLow;
  }

  public Boolean getFinished() {
    return finished;
  }

  public void setFinished(Boolean finished) {
    this.finished = finished;
  }

  

  
  
}
