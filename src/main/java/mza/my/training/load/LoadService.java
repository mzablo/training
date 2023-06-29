package mza.my.training.load;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mza.my.training.util.Conversions;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoadService {
    private static final String FILE_PATH = "c:/gosia/bieganie.xlsx";

    @Cacheable(value = "trainings")
    LoadResultDto load() {
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(FILE_PATH))) {
            Sheet sheet = workbook.getSheetAt(0);
            return new LoadResultDto(buildTrainingList(sheet));
        } catch (IOException e) {
            log.error("Error loading file " + FILE_PATH + e.getMessage());
            return null;
        }
    }

    @CacheEvict(value="trainings")
    public void evict() {
        log.debug("Evicting cache trainings.");
    }

    private List<TrainingDto> buildTrainingList(Sheet sheet) {
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
                .competition(getCompetition(row))
                .mountainCompetition(getMountainCompetition(row))
                .speed(Conversions.calculateSpeedKmPerH(row.getRowNum(), distance, time.getSeconds()))
                .speedForOneKm(Conversions.calculateSpeedOneKm(row.getRowNum(), distance, time.getSeconds()))
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
