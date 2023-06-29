package mza.my.training.load;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
@Log4j2
public class LoadController {

    private final LoadService loadService;

    @GetMapping("load")
    String load(Model model) {
        var result = loadService.load();
        model.addAttribute("trainingList", result.trainings());
        return "training";
    }

    @GetMapping("refresh-load")
    String evictAndLoad(Model model) {
        loadService.evict();
        var result = loadService.load();
        model.addAttribute("trainingList", result.trainings());
        return "training";
    }

    @GetMapping("training")
    String training(Model model) {
        var result = loadService.load();
        model.addAttribute("trainingList", result.trainings());
        return "training";
    }
}
