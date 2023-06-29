package mza.my.training.load;

import mza.my.training.util.Conversions;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.thymeleaf.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class XlsxReader {
    public static void main(String[] args) {
        String filePath = "c:/gosia/bieganie.xlsx";
        // getGeneric(filePath);
        //getFormal(filePath);
        x();
    }

    private static void x() {
        List<Integer> arr = List.of(123, 5465, 7876, 232, 667, 2);
        var list = arr.stream().sorted().collect(Collectors.toList());
        var sub = list.subList(list.size() - 4, list.size());
        System.out.println(sub);
        // BigInteger
        Optional<Integer> res1 = sub.stream().reduce(Integer::sum);
        BigInteger s = BigInteger.valueOf(res1.isPresent() ? res1.get() : 0);

    }

    private static void getFormal(String filePath) {
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(filePath))) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() > 0) {
                    var distance = getDistance(row);
                    var time = getTime(row);
                    var t = TrainingDto.builder()
                            .date(getTrainingDate(row))
                            .distance(distance)
                            .time(time)
                            .competition(getCompetition(row))
                            .mountainCompetition(getMountainCompetition(row))
                            .speed(Conversions.calculateSpeedKmPerH(row.getRowNum(), distance, time.getSeconds()))
                            .speedForOneKm(Conversions.calculateSpeedOneKm(row.getRowNum(), distance, time.getSeconds()))
                            .description(getDescription(row))
                            .build();
                    System.out.print(t);
                    if (row.getRowNum() > 5) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        double decimPart = (d - intPart) * 100;
        return Duration.ofSeconds(intPart * 60 + (int) decimPart);
    }

    private static LocalDate getTrainingDate(Row row) {
        return row.getCell(0).getDateCellValue().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private static void getGeneric(String filePath) {
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(filePath))) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                for (Cell cell : row) {
                    CellType cellType = cell.getCellType();
                    if (cellType == CellType.STRING) {
                        System.out.print(cellType + " " + cell.getStringCellValue() + "\t");
                    } else if (cellType == CellType.NUMERIC) {
                        System.out.print(cellType + " " + cell.getNumericCellValue() + "\t");
                    } // Add more conditions for different cell types if needed
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
