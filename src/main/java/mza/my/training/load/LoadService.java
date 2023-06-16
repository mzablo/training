package mza.my.training.load;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mza.my.training.util.Conversions;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoadService {

    LoadResultDto load(String filePath) {
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(filePath))) {
            Sheet sheet = workbook.getSheetAt(0);
            return new LoadResultDto(buildTrainingList(sheet));
        } catch (IOException e) {
            log.error("Error loading file " + filePath + e.getMessage());
            return null;
        }
    }

    private List<TrainingDto> buildTrainingList(Sheet sheet) {
        var result = new ArrayList<TrainingDto>();
        for (Row row : sheet) {
            if (row.getRowNum() > 0) {
                result.add(buildTrainingDto(row, getDistance(row), getTime(row)));
            }
        }
        log.info("Loaded {} trainings.", result.size());
        return result;
    }

    private TrainingDto buildTrainingDto(Row row, double distance, Duration time) {
        return TrainingDto.builder()
                .date(getTrainingDate(row))
                .distance(distance)
                .time(time)
                .competition(getCompetition(row))
                .mountainCompetition(getMountainCompetition(row))
                .speed(Conversions.calculateSpeedKmPerH(distance, time.getSeconds()))
                .speedForOneKm(Conversions.calculateSpeedOneKm(distance, time.getSeconds()))
                .description(getDescription(row))
                .build();
    }

    private static String getDescription(Row row) {
        return row.getCell(10).getStringCellValue();
    }

    private static boolean getCompetition(Row row) {
        return StringUtils.equals(row.getCell(9).getStringCellValue(), "tak");
    }

    private static boolean getMountainCompetition(Row row) {
        return StringUtils.equals(row.getCell(9).getStringCellValue(), "gtak");
    }

    private static double getDistance(Row row) {
        return row.getCell(2).getNumericCellValue();
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
