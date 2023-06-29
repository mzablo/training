package mza.my.training.statistic;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

record StatisticDto(
        LocalDate date,
        BigDecimal distanceSum,
        Duration timeSum,
        String timeDescSum,

        BigDecimal avgDistance,
        Duration avgTime,
        String avgTimeDesc,
        BigDecimal avgSpeed,
        BigDecimal avgSpeedForOneKm
        ) {
    @Builder
    public StatisticDto {
    }

    LocalDate getDate(){
        return date;
    }
}
