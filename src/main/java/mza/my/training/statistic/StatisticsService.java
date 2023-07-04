package mza.my.training.statistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mza.my.training.load.LoadServiceFactory;
import mza.my.training.load.TrainingDto;
import mza.my.training.util.Conversions;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsService {
    private static final String RUN_CAT = "bieganie";
    private static final Map<String, TemporalAdjuster> ADJUSTERS = Map.of(
            "week", TemporalAdjusters.previousOrSame(DayOfWeek.of(1)),
            "month", TemporalAdjusters.firstDayOfMonth(),
            "year", TemporalAdjusters.firstDayOfYear()
    );

    private final LoadServiceFactory loadServiceFactory;

    public List<StatisticDto> getWeeklyStatistics() {
        var result = buildStatisticList(getTrainings(), "week");
        result.sort(Comparator.comparing(StatisticDto::getDate).reversed());
        return result;
    }

    public List<StatisticDto> getMonthlyStatistics() {
        var result = buildStatisticList(getTrainings(), "month");
        result.sort(Comparator.comparing(StatisticDto::getDate).reversed());
        return result;
    }

    public List<StatisticDto> getYearlyStatistics() {
        var result = buildStatisticList(getTrainings(), "year");
        result.sort(Comparator.comparing(StatisticDto::getDate).reversed());
        return result;
    }

    private List<TrainingDto> getTrainings() {
        return loadServiceFactory.load(null, null, false).trainings();
    }

    private List<StatisticDto> buildStatisticList(List<TrainingDto> trainings, String adjuster) {
        return trainings.stream()
                .filter(t -> RUN_CAT.equals(t.getCategory()))
                .collect(Collectors.groupingBy(item -> item.getDate()
                        .with(ADJUSTERS.get(adjuster))))
                .values()
                .stream()
                .map(this::calculateAndBuild)
                .collect(Collectors.toList());
    }

    private StatisticDto calculateAndBuild(List<TrainingDto> trainings) {
        var timeSum = getTimeSum(trainings);
        var distanceSum = getDistanceSum(trainings);
        return StatisticDto.builder()
                .date(trainings.get(0).getDate())
                .distanceSum(BigDecimal.valueOf(distanceSum).setScale(2, RoundingMode.HALF_UP))
                .timeSum(timeSum)
                .timeDescSum(Conversions.getTimeDesc(timeSum))
                .avgDistance(getDistanceAvg(trainings))
                .avgTime(timeSum.dividedBy(trainings.size()))
                .avgTimeDesc(Conversions.getTimeDesc(timeSum.dividedBy(trainings.size())))
                .avgSpeed(Conversions.calculateSpeedKmPerH(0, distanceSum, timeSum.getSeconds()))
                .avgSpeedForOneKm(Conversions.calculateSpeedOneKm(0, distanceSum, timeSum.getSeconds()))
                .build();
    }

    private Duration getTimeSum(List<TrainingDto> trainings) {
        return trainings.stream()
                .map(TrainingDto::getTime)
                .reduce(Duration.ZERO, Duration::plus);
    }

    private double getDistanceSum(List<TrainingDto> trainings) {
        return trainings.stream()
                .map(TrainingDto::getDistance)
                .reduce(0.0, Double::sum);
    }

    private BigDecimal getDistanceAvg(List<TrainingDto> trainings) {
        return BigDecimal.valueOf(trainings.stream()
                .map(TrainingDto::getDistance)
                .reduce(0.0, Double::sum) / (trainings.size())).setScale(2, RoundingMode.HALF_UP);
    }
}
