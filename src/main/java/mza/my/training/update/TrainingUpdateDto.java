package mza.my.training.update;

import java.time.Duration;
import java.time.LocalDate;

record TrainingUpdateDto(
    LocalDate date,
    double distance,
    Duration time,
    boolean competition,
    boolean mountainCompetition,
    String description)
{}
