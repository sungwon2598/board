package ict.board.controller.board;

import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.reply.Reply;
import ict.board.service.BoardService;
import ict.board.service.ReplyService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final ReplyService replyService;

    @GetMapping("/board/new")
    public String createBoardForm(Model model) {
        model.addAttribute("BoardForm", new BoardForm());
        return "/board/boardform";
    }

    @PostMapping("/board/new")
    public String create(@Valid BoardForm form, BindingResult result) throws IOException, InterruptedException {

        if (result.hasErrors()) {
            return "/board/new";
        }

        Board board = new Board(form.getTitle(), form.getContent());
        boardService.save(board, form.getEmail(), form.getPassword());
        return "redirect:/";
    }

    @GetMapping("/")
    public String listBoards(Model model, @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC)
                             Pageable pageable) {
        Page<Board> boards = boardService.findAllBoards(pageable);
        model.addAttribute("boards", boards);
        return "index";
    }

    @GetMapping("/board/{id}")
    public String postDetail(@PathVariable Long id, Model model) {
        Board board = boardService.findOneBoard(id);
        if (board == null) {
            return "redirect:/";
        }
        model.addAttribute("board", board);

        List<Reply> comments = replyService.getCommentsByPostId(id);
        model.addAttribute("comments", comments);

        return "board/postDetail";
    }

    @PostMapping("/board/{id}/editForm")
    public String editForm(@PathVariable Long id, String userId, String password, Model model) {
        Board board = boardService.findOneBoard(id);
        if (board == null || !boardService.checkCredentials(id, userId, password)) {
            return "redirect:/board/" + id + "?error=auth";
        }

        model.addAttribute("board", board);
        return "board/editForm";
    }

    @PostMapping("/board/{id}/edit")
    public String editPost(@PathVariable Long id, String title, String content, Model model) {
        Board board = boardService.findOneBoard(id);
        if (board == null) {
            return "redirect:/";
        }
        boardService.update(id, title, content);
        return "redirect:/board/" + id;
    }

    @PostMapping("/board/{id}/delete")
    public String deletePost(@PathVariable Long id, String userId, String password,
                             RedirectAttributes redirectAttributes) {
        Board board = boardService.findOneBoard(id);
        if (board == null || !boardService.checkCredentials(id, userId, password)) {
            redirectAttributes.addFlashAttribute("error", "인증 실패");
            return "redirect:/board/" + id;
        }

        boardService.delete(id);
        return "redirect:/";
    }

    @PostMapping("board/{id}/changeStatus")
    public String changeStatus(@PathVariable Long id, String adminPassword, String status,
                               RedirectAttributes redirectAttributes) {
        if (!adminPassword.equals("024907345")) {
            redirectAttributes.addFlashAttribute("error", "인증 실패");
            return "redirect:/board/" + id;
        }

        boardService.changeBoardStatus(id, BoardStatus.valueOf(status));
        return "redirect:/board/" + id;
    }

}
