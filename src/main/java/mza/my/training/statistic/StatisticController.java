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
    String statistic(Model model) {
        model.addAttribute("weeklyStatisticList", statisticsService.getWeeklyStatistics());
        model.addAttribute("monthlyStatisticList", statisticsService.getMonthlyStatistics());
        model.addAttribute("yearlyStatisticList", statisticsService.getYearlyStatistics());
        return "statistic";
    }
}
