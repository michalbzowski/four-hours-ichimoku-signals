package pl.bzowski.bot.states;

public interface PositionState {

    boolean isOpened();

    boolean canBeClosed(long positionId);

    long getPositionId();

}
