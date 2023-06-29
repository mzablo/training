package mza.my.training.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

@UtilityClass
@Slf4j
public class Conversions {

    public  static String getTimeDesc(Duration duration) {
        var s = duration.getSeconds();
        return String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
    }

    public BigDecimal calculateSpeedKmPerH(int rowNum, double distanceKm, long timeInSec) {
        try {
            var distanceM = distanceKm * 1000;
            var speedMPerSec = distanceM / timeInSec;
            var speedKmPerH = speedMPerSec * 3.6;
            var result = BigDecimal.valueOf(speedKmPerH);
            result = result.setScale(2, RoundingMode.HALF_UP);
            // System.out.println("speed: " + result);
            return result;
        } catch (Exception e) {
            log.error("Problem for row {} calculating speed km per h dist:{} sec:{}, {}", rowNum, distanceKm, timeInSec, e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateSpeedOneKm(int rowNum, double distanceKm, long timeInSec) {
        try {
            var simplyResult = (timeInSec / distanceKm) / 60;
            var intSimplyResult = (int) simplyResult;
            var seconds = ((simplyResult % intSimplyResult) * 60) / 100;
            var result = BigDecimal.valueOf(intSimplyResult).add(BigDecimal.valueOf(seconds));
            result = result.setScale(2, RoundingMode.HALF_UP);
            //  System.out.println("speed1km: " + result);
            return result;
        } catch (Exception e) {
            log.error("Problem for row {} calculating speed 1km dist:{} sec:{}, {}", rowNum, distanceKm, timeInSec, e.getMessage());
        }
        return BigDecimal.ZERO;
    }
}
