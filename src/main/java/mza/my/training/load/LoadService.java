package mza.my.training.load;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mza.my.training.util.Conversions;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
class LoadService implements LoadServiceFactory {
    private static final String FILE_PATH = "c:/gosia/bieganie.xlsx";
    private final CacheManager cacheManager;

    @Override
    @Cacheable(value = "trainings")
    public LoadResultDto load(TrainingFilters trainingFilters, String sortingField, boolean ascending) {
        log.debug("sortingField: {}:{}, trainingFilters: {}", sortingField, ascending, trainingFilters);
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(FILE_PATH))) {
            Sheet sheet = workbook.getSheetAt(0);
            return new LoadResultDto(getTrainingListWithFiltersAndSorting(sortingField, ascending, trainingFilters, buildTrainingList(sheet)));
        } catch (IOException e) {
            log.error("Error loading file " + FILE_PATH + e.getMessage());
            return null;
        }
    }

    @CacheEvict(value = "trainings")
    public void evict() {
        log.debug("Evicting cache trainings.");
    }

    private List<TrainingDto> getTrainingListWithFiltersAndSorting(String sortingField, boolean ascending, TrainingFilters trainingFilters, List<TrainingDto> trainingDtoList) {
        return Optional.of(TrainingFilters.isFilled(trainingFilters))
                .map(f -> filterTrainingsAndSort(sortingField, ascending, trainingFilters, trainingDtoList))
                .orElse(trainingDtoList);
    }

    private List<TrainingDto> filterTrainingsAndSort(String sortingField, boolean ascending, TrainingFilters trainingFilters, List<TrainingDto> trainingDtoList) {
        return trainingDtoList.stream()
                .filter(t -> trainingMatchFilters(trainingFilters, t))
                .sorted(getSort(sortingField, ascending))
                .collect(Collectors.toList());
    }

    private Comparator<TrainingDto> getDefaultSort() {
        return Comparator.comparing(TrainingDto::getDate).reversed();
    }

    private Comparator<TrainingDto> getSort(String sortingField, boolean ascending) {
        if (ascending && Objects.equals("id", sortingField)) {
            return Comparator.comparing(TrainingDto::getId);
        } else if (!ascending && Objects.equals("id", sortingField)) {
            return Comparator.comparing(TrainingDto::getId).reversed();

        } else if (ascending && Objects.equals("category", sortingField)) {
            return Comparator.comparing(TrainingDto::getCategory);
        } else if (!ascending && Objects.equals("category", sortingField)) {
            return Comparator.comparing(TrainingDto::getCategory).reversed();

        } else if (ascending && Objects.equals("competition", sortingField)) {
            return Comparator.comparing(TrainingDto::isCompetition);
        } else if (!ascending && Objects.equals("competition", sortingField)) {
            return Comparator.comparing(TrainingDto::isCompetition).reversed();

        } else if (ascending && Objects.equals("mountainCompetition", sortingField)) {
            return Comparator.comparing(TrainingDto::isMountainCompetition);
        } else if (!ascending && Objects.equals("mountainCompetition", sortingField)) {
            return Comparator.comparing(TrainingDto::isMountainCompetition).reversed();

        } else if (ascending && Objects.equals("date", sortingField)) {
            return Comparator.comparing(TrainingDto::getDate);
        } else if (!ascending && Objects.equals("date", sortingField)) {
            return Comparator.comparing(TrainingDto::getDate).reversed();

        } else if (ascending && Objects.equals("speed", sortingField)) {
            return Comparator.comparing(TrainingDto::getSpeed);
        } else if (!ascending && Objects.equals("speed", sortingField)) {
            return Comparator.comparing(TrainingDto::getSpeed).reversed();

        } else if (ascending && Objects.equals("speedForOneKm", sortingField)) {
            return Comparator.comparing(TrainingDto::getSpeedForOneKm);
        } else if (!ascending && Objects.equals("speedForOneKm", sortingField)) {
            return Comparator.comparing(TrainingDto::getSpeedForOneKm).reversed();

        } else if (ascending && Objects.equals("distance", sortingField)) {
            return Comparator.comparing(TrainingDto::getDistance);
        } else if (!ascending && Objects.equals("distance", sortingField)) {
            return Comparator.comparing(TrainingDto::getDistance).reversed();

        } else if (ascending && Objects.equals("time", sortingField)) {
            return Comparator.comparing(TrainingDto::getTime);
        } else if (!ascending && Objects.equals("time", sortingField)) {
            return Comparator.comparing(TrainingDto::getTime).reversed();

        } else if (ascending && Objects.equals("description", sortingField)) {
            return Comparator.comparing(TrainingDto::getDescription);
        } else if (!ascending && Objects.equals("description", sortingField)) {
            return Comparator.comparing(TrainingDto::getDescription).reversed();
        }
        return getDefaultSort();
    }


    private boolean trainingMatchFilters(TrainingFilters trainingFilters, TrainingDto trainingDto) {
        return Objects.isNull(trainingFilters)
                || (trainingFilters.getCategory() == null || trainingDto.getCategory().contains(trainingFilters.getCategory()))
                && (trainingFilters.getDescription() == null || trainingDto.getDescription().contains(trainingFilters.getDescription()))
                && (trainingFilters.getCompetition() == null || trainingDto.isCompetition() == trainingFilters.getCompetition())
                && (trainingFilters.getMountainCompetition() == null || trainingDto.isMountainCompetition() == trainingFilters.getMountainCompetition())
                && (trainingFilters.getId() == null || trainingDto.getId() == trainingFilters.getId())
                && (trainingFilters.getDate() == null || trainingDto.getDate().equals(trainingFilters.getDate()))
                && (trainingFilters.getSpeed() == null || trainingDto.getSpeed().equals(trainingFilters.getSpeed()))
                && (trainingFilters.getSpeedForOneKm() == null || trainingDto.getSpeedForOneKm().equals(trainingFilters.getSpeedForOneKm()))
                && (trainingFilters.getDistance() == null || trainingDto.getDistance() == trainingFilters.getDistance())
                ;
    }

    public List<TrainingDto> buildTrainingList(Sheet sheet) {
        var result = new ArrayList<TrainingDto>();
        for (Row row : sheet) {
            if (row.getRowNum() > 0 && !isEmptyRow(row)) {
                result.add(buildTrainingDto(row, getDistance(row), getTime(row)));
                if (isEmptyRow(row)) {
                    break;
                }
//                if (row.getRowNum() > 155) {
                //                  break;//!!!
                //            }
            }
        }
        //      result.sort(Comparator.comparing(TrainingDto::getDate).reversed());
        log.info("Loaded {} trainings.", result.size());
        return result;
    }

    private boolean isEmptyRow(Row row) {
        return Objects.isNull(row.getCell(0)) || CellType.BLANK.equals(row.getCell(0).getCellType());
    }

    private TrainingDto buildTrainingDto(Row row, double distance, Duration time) {
        return TrainingDto.builder()
                .id(row.getRowNum())
                .date(getTrainingDate(row))
                .distance(distance)
                .time(time)
                .timeDesc(Conversions.getTimeDesc(time))
                .competition(getCompetition(row))
                .mountainCompetition(getMountainCompetition(row))
                .speed(Conversions.calculateSpeedKmPerH(row.getRowNum(), distance, time.getSeconds()))
                .speedForOneKm(Conversions.calculateSpeedOneKm(row.getRowNum(), distance, time.getSeconds()))
                .category(getCategory(row))
                .description(getDescription(row))
                .build();
    }

    private static String getDescription(Row row) {
        try {
            return Optional.ofNullable(row.getCell(10)).map(Cell::getStringCellValue).orElse("");
        } catch (Exception e) {
            log.error("Problem getting description for row {} :{}", row.getRowNum(), e.getMessage());
        }
        return "";
    }

    private static String getCategory(Row row) {
        try {
            return Optional.ofNullable(row.getCell(7)).map(Cell::getStringCellValue).orElse("");
        } catch (Exception e) {
            log.error("Problem getting category for row {} :{}", row.getRowNum(), e.getMessage());
        }
        return "";
    }

    private static boolean getCompetition(Row row) {
        try {
            return StringUtils.equals(Optional.ofNullable(row.getCell(9)).map(Cell::getStringCellValue).orElse(""), "tak");
        } catch (Exception e) {
            log.error("Problem getting competition for row {} :{}", row.getRowNum(), e.getMessage());
        }
        return false;
    }

    private static boolean getMountainCompetition(Row row) {
        try {
            return StringUtils.equals(Optional.ofNullable(row.getCell(9)).map(Cell::getStringCellValue).orElse(""), "gtak");
        } catch (Exception e) {
            log.error("Problem getting mountain competition for row {} :{}", row.getRowNum(), e.getMessage());
        }
        return false;
    }

    private static double getDistance(Row row) {
        try {
            return row.getCell(2).getNumericCellValue();
        } catch (Exception e) {
            log.error("Problem getting distance for row {} :{}", row.getRowNum(), e.getMessage());
        }
        return 0;
    }

    private static Duration getTime(Row row) {
        var d = row.getCell(3).getNumericCellValue();
        int intPart = (int) d;
        double decimalPart = (d - intPart) * 100;
        return Duration.ofSeconds(intPart * 60 + (int) decimalPart);
    }

    private static LocalDate getTrainingDate(Row row) {
        return row.getCell(0).getDateCellValue().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
