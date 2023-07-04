package mza.my.training.load;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
//todo sortowanie na statystykach

@Controller
@RequiredArgsConstructor
@Log4j2
public class LoadController {

    private final LoadService loadService;

    @GetMapping("load")
    String load(Model model, TrainingFilters trainingFilters, String sortingField, boolean ascending) {
        setModelData(model, trainingFilters, sortingField, ascending);
        return "training";
    }

    @GetMapping("refresh-load")
    String evictAndLoad(Model model, TrainingFilters trainingFilters, String sortingField, boolean ascending) {
        loadService.evict();
        setModelData(model, trainingFilters, sortingField, ascending);
        return "training";
    }

    @GetMapping("training")
    String training(Model model, TrainingFilters trainingFilters, String sortingField, boolean ascending) {
        setModelData(model, trainingFilters, sortingField, ascending);
        return "training";
    }

    private void setModelData(Model model, TrainingFilters trainingFilters, String sortingField, boolean ascending) {
        model.addAttribute("trainingList", loadService.load(trainingFilters, sortingField, ascending).trainings());
        model.addAttribute("isAscending", !ascending);
    }
}
