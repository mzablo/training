package mza.my.training;

import lombok.RequiredArgsConstructor;
import mza.my.training.load.LoadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class XController {


    @GetMapping("/")
    public String start(Model model) {
//nie wchodzi tu
        return "start";
    }
}
