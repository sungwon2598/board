package ict.board.controller;

import ict.board.config.argumentresolver.Login;
import ict.board.domain.board.Board;
import ict.board.domain.board.BoardStatus;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.dto.BoardForm;
import ict.board.dto.PostDetail;
import ict.board.service.BoardService;
import ict.board.service.MemberService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final ReplyService replyService;
    private final MemberService memberService;

    @GetMapping("board/new")
    public String createBoardForm(Model model) {
        model.addAttribute("BoardForm", new BoardForm());
        return "board/boardform";
    }

    @PostMapping("board/new")
    public String create(@Valid BoardForm form, BindingResult result, @Login String loginMemberEmail)
            throws IOException, InterruptedException {

        if (result.hasErrors()) {
            return "board/new";
        }

        Board board = new Board(form.getTitle(), form.getContent());
        boardService.save(board, loginMemberEmail);
        return "redirect:/";
    }

    @GetMapping("/")
    public String listBoards(
            @Login String loginMemberEmail,
            Model model, @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC)
            Pageable pageable) {

        Page<Board> boards = boardService.findAllBoards(pageable);
        Member loginMember = memberService.findMemberByEmail(loginMemberEmail);

        model.addAttribute("loginMember", loginMember);
        model.addAttribute("boards", boards);
        return "Index";
    }

    @GetMapping("/board/{id}")
    public String postDetail(@PathVariable Long id, Model model, @Login String loginMemberEmail) {
        Board board = boardService.findOneBoard(id);

        if (board == null) {
            return "redirect:/";
        }

        boolean isLogin = board.getMember().getEmail().equals(loginMemberEmail);
        Member loginMember = memberService.findMemberByEmail(loginMemberEmail);
        boolean isManager = loginMember.getTeam().equals("ict지원실");
        List<Reply> comments = replyService.getCommentsByPostId(id);

        PostDetail postDetail = new PostDetail(isLogin, isManager, loginMemberEmail, board, comments);
        model.addAttribute("postDetail", postDetail);

        return "board/postDetail";
    }

    @GetMapping("/board/{id}/editForm")
    public String editForm(@Login String loginMemberEmail, @PathVariable Long id, Model model) {

        Board board = boardService.findOneBoard(id);

        if (board == null || loginMemberEmail == null) {
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
    public String deletePost(@Login String loginMemberEmail, @PathVariable Long id,
                             RedirectAttributes redirectAttributes) {

        Board board = boardService.findOneBoard(id);

        if (board == null || loginMemberEmail == null) {
            redirectAttributes.addFlashAttribute("error", "인증 실패");
            return "redirect:/board/" + id;
        }

        boardService.delete(id);
        return "redirect:/";
    }

    @PostMapping("/board/{id}/changeStatus")
    public String changeStatus(@PathVariable Long id, String adminPassword, String status,
                               RedirectAttributes redirectAttributes) {

        BoardStatus boardStatus = BoardStatus.valueOf(status);

        boolean success = boardService.changeBoardStatus(id, boardStatus, adminPassword);

        if (!success) {
            redirectAttributes.addFlashAttribute("error", "인증 실패");
            return "redirect:/board/" + id;
        }

        return "redirect:/board/" + id;
    }

}
