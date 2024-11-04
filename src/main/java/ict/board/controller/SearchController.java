package ict.board.controller;

import ict.board.domain.board.Board;
import ict.board.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final BoardService boardService;

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false, defaultValue = "UNCHECKED") String status,
            Pageable pageable,
            Model model) {

        Page<Board> boards = boardService.findAllBoardsByStatus(pageable, status);
        model.addAttribute("boards", boards);
        model.addAttribute("selectedStatus", status);
        return "search";
    }
}
