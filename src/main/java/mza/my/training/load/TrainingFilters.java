package mza.my.training.load;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
class TrainingFilters {
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private Double distance;
    private BigDecimal speed;
    private BigDecimal speedForOneKm;
    private String category;
    private String description;
    private Boolean mountainCompetition;
    private Boolean competition;

    static boolean isFilled(TrainingFilters trainingFilters) {
        return Objects.nonNull(trainingFilters)
                && (Objects.nonNull(trainingFilters.getId())
                || Objects.nonNull(trainingFilters.getDate())
                || Objects.nonNull(trainingFilters.getDistance())
                || Objects.nonNull(trainingFilters.getSpeed())
                || Objects.nonNull(trainingFilters.getSpeedForOneKm())
                || Objects.nonNull(trainingFilters.getCategory())
                || Objects.nonNull(trainingFilters.getCompetition())
                || Objects.nonNull(trainingFilters.getMountainCompetition())
                || Objects.nonNull(trainingFilters.getDescription()));
    }
}
