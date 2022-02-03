package pl.bzowski.bot.trend;

public class BearishTrend implements Trend {
    @Override
    public boolean isBullish() {
        return false;
    }

    @Override
    public boolean isBearish() {
        return true;
    }

    @Override
    public boolean noTrend() {
        return false;
    }

    @Override
    public String toString() {
        return "DOWN_TREND";
    }
}
