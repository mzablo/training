package mza.my.training.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class Conversions {
    public BigDecimal calculateSpeedKmPerH(double distanceKm, long timeInSec) {
        var distanceM = distanceKm * 1000;
        var speedMPerSec = distanceM / timeInSec;
        var speedKmPerH = speedMPerSec * 3.6;
        var result = BigDecimal.valueOf(speedKmPerH);
        result = result.setScale(2, RoundingMode.HALF_UP);
        // System.out.println("speed: " + result);
        return result;
    }

    public BigDecimal calculateSpeedOneKm(double distanceKm, long timeInSec) {
        var simplyResult = (timeInSec / distanceKm) / 60;
        var intSimplyResult = (int) simplyResult;
        var seconds = ((simplyResult % intSimplyResult) * 60) / 100;
        var result = BigDecimal.valueOf(intSimplyResult).add(BigDecimal.valueOf(seconds));
        result = result.setScale(2, RoundingMode.HALF_UP);
        //  System.out.println("speed1km: " + result);
        return result;
    }
}
