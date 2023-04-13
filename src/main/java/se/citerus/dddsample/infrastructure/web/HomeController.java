package se.citerus.dddsample.infrastructure.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping(value = {"/", "/cargo/**", "/admin/**"})
    public String index() {
        return "/static/index";
    }

}
