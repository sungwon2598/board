package ict.board.controller.reply;


import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.service.BoardService;
import ict.board.service.LoginService;
import ict.board.service.MemberService;
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
    private final MemberService memberService;


    @PostMapping("/board/{boardId}/reply")
    public String addReply(@PathVariable Long boardId, @ModelAttribute ReplyForm replyForm) {
        Board board = boardService.findOneBoard(boardId);
        if (board == null) {
            // 게시글이 없을 경우 처리
            return "redirect:/";
        }

        try {
            // 사용자 인증
            loginService.login(replyForm.getUserId(), replyForm.getPassword());
        } catch (IllegalStateException e) {
            return "redirect:/board/" + boardId;
        }

        Reply reply = new Reply();
        Member member = memberService.findMemberByEmail(replyForm.getUserId());
        // 회원 정보 가져오기
        reply.addMember(member);
        if (reply.getMember() == null) {
            // 회원 정보 없음 처리
            return "redirect:/board/" + boardId;
        }
        reply.setContent(replyForm.getReply()); // 댓글 내용 설정
        reply.addBoard(board); // 댓글이 속한 게시글 설정

        replyService.save(reply);

        return "redirect:/board/" + boardId;
    }
}


