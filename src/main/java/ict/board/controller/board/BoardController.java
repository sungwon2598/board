package ict.board.controller.board;

import ict.board.SessionConst;
import ict.board.argumentresolver.Login;
import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.service.BoardService;
import ict.board.service.ReplyService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final ReplyService replyService;

    @GetMapping("/board/new")
    public String createBoardForm(Model model) {
        model.addAttribute("BoardForm", new BoardForm());
        return "/board/boardform";
    }

    @PostMapping("/board/new")
    public String create(@Valid BoardForm form, BindingResult result, @Login Member loginMember)
            throws IOException, InterruptedException {

        if (result.hasErrors()) {
            return "/board/new";
        }

        Board board = new Board(form.getTitle(), form.getContent());
        boardService.save(board, loginMember);
        return "redirect:/";
    }

    @GetMapping("/")
    public String listBoards(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                             Model model, @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC)
                             Pageable pageable) {
        Page<Board> boards = boardService.findAllBoards(pageable);
        model.addAttribute("loginMember", loginMember);
        model.addAttribute("boards", boards);
        return "index";
    }

    @GetMapping("/board/{id}")
    public String postDetail(@PathVariable Long id, Model model, @Login Member loginMember) {
        Board board = boardService.findOneBoard(id);
        if (board == null) {
            return "redirect:/";
        }

        String loginMemberEmail = loginMember.getEmail();
        boolean isLogin = board.getMember().getEmail().equals(loginMember.getEmail());
        model.addAttribute("isLogin", isLogin);

        boolean isManager = loginMember.getTeam().equals("ict지원실");
        model.addAttribute("isManager", isManager);

        model.addAttribute("board", board);
        model.addAttribute("loginMemberEmail", loginMemberEmail);

        List<Reply> comments = replyService.getCommentsByPostId(id);
        model.addAttribute("comments", comments);

        return "board/postDetail";
    }

    @GetMapping("/board/{id}/editForm")
    public String editForm(@Login Member loginMember, @PathVariable Long id, Model model) {

        Board board = boardService.findOneBoard(id);

        if (board == null || loginMember == null) {
            return "redirect:/board/" + id + "?error=auth";
        }

        model.addAttribute("board", board);
        return "board/editForm";
    }

    @PostMapping("/board/{id}/edit")
    public String editPost(@PathVariable Long id, String title, String content) {
        Board board = boardService.findOneBoard(id);
        if (board == null) {
            return "redirect:/";
        }
        boardService.update(id, title, content);
        return "redirect:/board/" + id;
    }

    @PostMapping("/board/{id}/delete")
    public String deletePost(@Login Member loginMember, @PathVariable Long id, RedirectAttributes redirectAttributes) {

        Board board = boardService.findOneBoard(id);

        if (board == null || loginMember == null) {
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
