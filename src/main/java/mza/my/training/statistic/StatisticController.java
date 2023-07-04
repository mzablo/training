package mza.my.training.statistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
@Log4j2
public class StatisticController {

    private final StatisticsService statisticsService;

    @GetMapping("statistic")
    String statistic(Model model,String sortingField, boolean ascending) {
        model.addAttribute("isAscending", !ascending);
        model.addAttribute("weeklyStatisticList", statisticsService.getWeeklyStatistics(sortingField, ascending));
        model.addAttribute("monthlyStatisticList", statisticsService.getMonthlyStatistics(sortingField, ascending));
        model.addAttribute("yearlyStatisticList", statisticsService.getYearlyStatistics(sortingField, ascending));
        return "statistic";
    }
}
