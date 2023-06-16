package mza.my.training.load;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

//float or double
record TrainingDto(
        LocalDate date,
        double distance,
        Duration time,
        BigDecimal speed,
        BigDecimal speedForOneKm,
        boolean competition,
        boolean mountainCompetition,
        String description) {
    @Builder
    public TrainingDto {
    }
}
