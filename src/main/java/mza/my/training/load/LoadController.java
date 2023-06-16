package mza.my.training.load;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoadController {

    private final LoadService loadService;

    @PostMapping("load")
    String load(String filePath) {
        loadService.load(filePath);
        return "traning";
    }
}
