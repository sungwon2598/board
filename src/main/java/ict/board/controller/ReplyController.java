package ict.board.controller;


import ict.board.config.argumentresolver.Login;
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
                           @Login String loginMemberEmail) {
        replyService.saveByReplyForm(replyForm, boardId, loginMemberEmail);
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