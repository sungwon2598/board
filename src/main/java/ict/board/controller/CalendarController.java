package ict.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalendarController {

    @GetMapping("/calendar")
    public String goCalendar() {
        return "calendar";
    }
}
