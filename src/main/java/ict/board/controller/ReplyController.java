package ict.board.controller;

import ict.board.config.argumentresolver.Login;
import ict.board.config.argumentresolver.LoginMemberArgumentResolver.LoginSessionInfo;
import ict.board.dto.ReplyForm;
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

    private final ReplyService replyService;

    @PostMapping("/board/{boardId}/reply")
    public String addReply(@PathVariable Long boardId, @ModelAttribute ReplyForm replyForm,
                           @Login LoginSessionInfo loginSessionInfo) {
        replyService.saveByReplyForm(replyForm, boardId, loginSessionInfo.getEmail());
        return "redirect:/board/" + boardId;
    }

    @PostMapping("/board/{boardId}/reply/delete/{replyId}")
    public String deleteReply(@PathVariable Long replyId, @PathVariable Long boardId,
                              @Login LoginSessionInfo loginSessionInfo) {
        // 관리자 권한 확인
        if (!"MANAGER".equals(loginSessionInfo.getRole().name())) {
            return "redirect:/board/" + boardId + "?error=auth";
        }
        replyService.deleteReply(replyId);
        return "redirect:/board/" + boardId;
    }

    @PutMapping("/board/{boardId}/reply/update/{replyId}")
    public String updateReply(@RequestParam String content, @PathVariable Long replyId, @PathVariable Long boardId,
                              @Login LoginSessionInfo loginSessionInfo) {
        replyService.updateReply(replyId, content);
        return "redirect:/board/" + boardId;
    }
}