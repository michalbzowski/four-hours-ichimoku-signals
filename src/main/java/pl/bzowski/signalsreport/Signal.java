package pl.bzowski.signalsreport;

import java.beans.ConstructorProperties;

public class Signal {

  private String symbol;
  private String signalType;
  private String trend;
  private Boolean shouldEnter;
  private Boolean shouldExit;

  @ConstructorProperties({"symbol", "signalType", "trend", "shouldEnter", "shouldExit"})
  public Signal(String symbol, String signalType, String trend, Boolean shouldEnter, Boolean shouldExit) {
    this.symbol = symbol;
    this.signalType = signalType;
    this.trend = trend;
    this.shouldEnter = shouldEnter;
    this.shouldExit = shouldExit;
  }

  public String getSymbol() {
    return symbol;
  }

  public String getSignalType() {
    return signalType;
  }

  public String getTrend() {
    return trend;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public void setSignalType(String signalType) {
    this.signalType = signalType;
  }

  public void setTrend(String trend) {
    this.trend = trend;
  }

  public Boolean getShouldEnter() {
    return shouldEnter;
  }

  public Boolean getShouldExit() {
    return shouldExit;
  }

  public void setShouldEnter(Boolean shouldEnter) {
    this.shouldEnter = shouldEnter;
  }

  public void setShouldExit(Boolean shouldExit) {
    this.shouldExit = shouldExit;
  }
}
