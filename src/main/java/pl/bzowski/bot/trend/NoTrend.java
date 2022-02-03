package pl.bzowski.bot.trend;

public class NoTrend implements Trend {
    @Override
    public boolean isBullish() {
        return false;
    }

    @Override
    public boolean isBearish() {
        return false;
    }

    @Override
    public boolean noTrend() {
        return true;
    }

    @Override
    public String toString() {
        return "NO_TREND";
    }
}
