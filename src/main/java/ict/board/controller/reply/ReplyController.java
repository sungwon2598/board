package ict.board.controller.reply;


import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.service.BoardService;
import ict.board.service.LoginService;
import ict.board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final BoardService boardService;
    private final ReplyService replyService;
    private final LoginService loginService;


    @PostMapping("/board/{boardId}/reply")
    public String addReply(@PathVariable Long boardId, @ModelAttribute ReplyForm replyForm) {
        Board board = boardService.findOneBoard(boardId);

        try {
            Member member = loginService.login(replyForm.getUserId(), replyForm.getPassword());
            Reply reply = new Reply(replyForm.getReply(), board, member);

            replyService.save(reply);

            return "redirect:/board/" + boardId;
        } catch (IllegalStateException e) {
            return "redirect:/board/" + boardId;
        }

    }
}


