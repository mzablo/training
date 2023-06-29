package mza.my.training.load;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

@Value
@Builder
@Getter
public class TrainingDto{
    long id;
    LocalDate date;
    double distance;
    Duration time;
    String timeDesc;
    BigDecimal speed;
    BigDecimal speedForOneKm;
    boolean competition;
    boolean mountainCompetition;
    String category;
    String description;
}