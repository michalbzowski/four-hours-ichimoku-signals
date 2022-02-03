package pl.bzowski.signals;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import pl.bzowski.bot.trend.Trend;

@ApplicationScoped
public class SignalsFacade {

  private SignalsRepository signalsRepository;

  @Inject
  public SignalsFacade(SignalsRepository signalsRepository) {
    this.signalsRepository = signalsRepository;
  }

  public void addSignal(String symbol, String name, Trend trend, boolean shouldEnter, boolean shouldExit, long signalTime) {
    signalsRepository.addSignal(symbol, name, trend, shouldEnter, shouldExit, signalTime);
  }
}
