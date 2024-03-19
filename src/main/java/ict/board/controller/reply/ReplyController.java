package ict.board.controller.reply;


import ict.board.argumentresolver.Login;
import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.service.BoardService;
import ict.board.service.MemberService;
import ict.board.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final BoardService boardService;
    private final ReplyService replyService;
    private final MemberService memberService;

    @PostMapping("/board/{boardId}/reply")
    public String addReply(@PathVariable Long boardId, @ModelAttribute ReplyForm replyForm, @Login Member loginMember) {

        Board board = boardService.findOneBoard(boardId);
        Member member = memberService.findMemberByEmail(loginMember.getEmail());

        Reply reply = new Reply(replyForm.getReply(), board, member);
        replyService.save(reply);
        return "redirect:/board/" + boardId;
    }

    @PostMapping("/board/{boardId}/reply/delete/{replyId}")
    public String deleteReply(@PathVariable Long replyId, @PathVariable Long boardId, @Login Member loginMember,
                              Model model) {

        replyService.deleteReply(replyId);

        return "redirect:/board/" + boardId;
    }

}