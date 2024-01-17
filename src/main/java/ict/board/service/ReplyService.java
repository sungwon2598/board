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
    public void save(Reply reply) {
        replyRepository.save(reply);
    }

    @Transactional
    public void deleteAll(List<Long> ids) {
        replyRepository.deleteAll(ids);
    }

    @Transactional
    public void deleteOne(Long id) {
        replyRepository.deleteOne(id);
    }

    public List<Reply> getCommentsByPostId(Long id) {
        return replyRepository.findBoardsReplies(id);
    }

    public List<Reply> getCommentsByMember(Member member) {
        return replyRepository.findRepliesByMember(member);
    }

}
