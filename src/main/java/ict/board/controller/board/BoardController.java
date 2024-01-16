package ict.board.controller.board;

import ict.board.domain.board.Board;
import ict.board.domain.reply.Reply;
import ict.board.service.BoardService;
import ict.board.service.ReplyService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String create(@Valid BoardForm form, BindingResult result) {

        if (result.hasErrors()) {
            return "/board/new";
        }

        Board board = new Board();
        board.setTitle(form.getTitle());
        board.setContent(form.getContent());
        boardService.save(board, form.getEmail(), form.getPassword());
        return "redirect:/";
    }

    @GetMapping("/")
    public String listBoards(Model model) {
        List<Board> boards = boardService.findAllBoards();
        model.addAttribute("boards", boards);
        return "index"; // Thymeleaf 템플릿의 이름
    }

    @GetMapping("/board/{id}")
    public String postDetail(@PathVariable Long id, Model model) {
        // 게시글 서비스로부터 게시글 데이터 가져오기
        Board board = boardService.findOneBoard(id);
        if (board == null) {
            // 게시글이 없을 경우 처리
            return "redirect:/"; // 혹은 적절한 에러 페이지로 리다이렉트
        }
        // Model에 게시글 데이터 추가
        model.addAttribute("board", board);

        // 게시글에 대한 댓글 목록 가져오기
        List<Reply> comments = replyService.getCommentsByPostId(id);
        model.addAttribute("comments", comments);

        return "board/postDetail"; // 게시글 상세 페이지의 Thymeleaf 템플릿 이름
    }
}
