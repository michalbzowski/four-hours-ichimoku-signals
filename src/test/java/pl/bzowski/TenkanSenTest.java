package pl.bzowski;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.ichimoku.IchimokuTenkanSenIndicator;
import org.ta4j.core.num.Num;

@QuarkusTest
public class TenkanSenTest {

  @Test
  public void lol() {
    List<Bar> bars = new ArrayList<>();
    Duration timePeriod = Duration.ofHours(4);
    ZonedDateTime endTime = ZonedDateTime.of(2022, 01, 30, 8, 0, 0, 0, ZoneId.systemDefault());
    for (int i = 0; i < 9; i++) {
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * i), 1, 1, 1, 1, 1));
    }

    BarSeries series = new BaseBarSeries(bars);
    IchimokuTenkanSenIndicator tenkanSenIndicator = new IchimokuTenkanSenIndicator(series);
    
    int endIndex = tenkanSenIndicator.getBarSeries().getEndIndex();
    assertEquals(8, endIndex);

    Num value = tenkanSenIndicator.getValue(endIndex);
    assertEquals(1d, value.doubleValue());
  }

  @Test
  public void ninePeriodAveregeOfHigh2AndLow0ShouldBe1() {
    List<Bar> bars = new ArrayList<>();
    Duration timePeriod = Duration.ofHours(4);
    ZonedDateTime endTime = ZonedDateTime.of(2022, 01, 30, 8, 0, 0, 0, ZoneId.systemDefault());
    
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 0), 1, 2, 1, 1, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 1), 1, 1, 1, 1, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 2), 1, 1, 1, 1, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 3), 1, 1, 1, 1, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 4), 1, 1, 1, 1, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 5), 1, 1, 1, 1, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 6), 1, 1, 1, 1, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 7), 1, 1, 1, 1, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 8), 1, 1, 0, 1, 1));

    BarSeries series = new BaseBarSeries(bars);
    IchimokuTenkanSenIndicator tenkanSenIndicator = new IchimokuTenkanSenIndicator(series);
    
    int endIndex = tenkanSenIndicator.getBarSeries().getEndIndex();
    assertEquals(8, endIndex);

    Num value = tenkanSenIndicator.getValue(endIndex);
    assertEquals(1d, value.doubleValue());
  }

  @Test
  public void ninePeriodAveregeOfHigh6AndLow3ShouldBe4and5() {
    List<Bar> bars = new ArrayList<>();
    Duration timePeriod = Duration.ofHours(4);
    ZonedDateTime endTime = ZonedDateTime.of(2022, 01, 30, 8, 0, 0, 0, ZoneId.systemDefault());
    
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 0), 6, 6, 3, 4, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 1), 6, 4, 4, 4, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 2), 6, 4, 4, 4, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 3), 6, 4, 4, 4, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 4), 6, 4, 4, 4, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 5), 4, 4, 4, 4, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 6), 2, 4, 4, 4, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 7), 9, 4, 4, 4, 1));
      bars.add(new BaseBar(timePeriod, endTime.plusHours(4 * 8), 4, 4, 4, 4, 1));

    BarSeries series = new BaseBarSeries(bars);
    IchimokuTenkanSenIndicator tenkanSenIndicator = new IchimokuTenkanSenIndicator(series);
    
    int endIndex = tenkanSenIndicator.getBarSeries().getEndIndex();
    assertEquals(8, endIndex);

    Num value = tenkanSenIndicator.getValue(endIndex);
    assertEquals(4.5d, value.doubleValue());
  }

}
