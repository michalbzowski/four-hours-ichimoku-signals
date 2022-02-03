package pl.bzowski.bot.trend;

public class BullishTrend implements Trend {

    @Override
    public boolean isBullish() {
        return true;
    }

    @Override
    public boolean isBearish() {
        return false;
    }

    @Override
    public boolean noTrend() {
        return false;
    }

    @Override
    public String toString() {
        return "UP_TREND";
    }
}
