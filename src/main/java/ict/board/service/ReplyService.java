package ict.board.service;


import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.repsoitory.ReplyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {

    private final ReplyRepository replyRepository;

    @Transactional
    public Long save(Reply reply) {
        return replyRepository.save(reply).getId();
    }

    @Transactional
    public void updateReply(Long replyId, String newContent) {
        Reply reply = replyRepository.findById(replyId).orElse(null);
        reply.updateContent(newContent);
    }

    @Transactional
    public void deleteReply(Long replyId) {
        replyRepository.deleteById(replyId);
    }


    public List<Reply> getCommentsByPostId(Long id) {
        return replyRepository.findRepliesByBoard(id);
    }

    public List<Reply> getCommentsByMember(Member member) {
        return replyRepository.findRepliesByMember(member);
    }

}
