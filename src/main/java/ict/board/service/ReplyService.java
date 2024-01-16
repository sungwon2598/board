package ict.board.service;


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

    public void save(Reply reply) {
        replyRepository.save(reply);
    }

    public List<Reply> getCommentsByPostId(Long id) {
        return replyRepository.findBoardsReplies(id);
    }
}
