package ict.board.service.board;


import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.dto.ReplyForm;
import ict.board.repository.BoardRepository;
import ict.board.repository.MemberRepository;
import ict.board.repository.ReplyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long save(Reply reply) {
        replyRepository.save(reply);
        return reply.getId();
    }

    @Transactional
    public Long saveByReplyForm(ReplyForm replyForm, Long boardId, String loginMemberEmail) {

        Board board = boardRepository.findById(boardId).orElse(null);
        Member member = memberRepository.findMemberByEmail(loginMemberEmail).orElse(null);

        Reply reply = new Reply(replyForm.getReply(), board, member);
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

    @Transactional
    public void deleteRepliesByBoardId(Long boardId) {
        replyRepository.deleteByBoardId(boardId);
    }

    public List<Reply> getCommentsByPostId(Long id) {
        return replyRepository.findRepliesByBoard(id);
    }

    public List<Reply> getCommentsByMember(Member member) {
        return replyRepository.findRepliesByMember(member);
    }

}
