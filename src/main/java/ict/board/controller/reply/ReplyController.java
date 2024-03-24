package ict.board.controller.reply;


import ict.board.config.argumentresolver.Login;
import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.service.BoardService;
import ict.board.service.MemberService;
import ict.board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final BoardService boardService;
    private final ReplyService replyService;
    private final MemberService memberService;

    @PostMapping("/board/{boardId}/reply")
    public String addReply(@PathVariable Long boardId, @ModelAttribute ReplyForm replyForm,
                           @Login String loginMemberEmail) {

        Board board = boardService.findOneBoard(boardId);
        Member member = memberService.findMemberByEmail(loginMemberEmail);

        Reply reply = new Reply(replyForm.getReply(), board, member);
        replyService.save(reply);
        return "redirect:/board/" + boardId;
    }

    @PostMapping("/board/{boardId}/reply/delete/{replyId}")
    public String deleteReply(@PathVariable Long replyId, @PathVariable Long boardId) {

        replyService.deleteReply(replyId);

        return "redirect:/board/" + boardId;
    }

    @PutMapping("/board/{boardId}/reply/delete/{replyId}")
    public String updateReply(@RequestParam String content, @PathVariable Long replyId, @PathVariable Long boardId) {
        replyService.updateReply(replyId, content);
        return "redirect:/board/" + boardId;
    }

}