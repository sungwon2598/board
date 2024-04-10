package ict.board.repsoitory;

import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("select r from Reply r where r.member = :member")
    List<Reply> findRepliesByMember(Member member);

    @Query("select r from Reply r join fetch r.board b join fetch r.member where b.id = :boardId")
    List<Reply> findRepliesByBoard(Long boardId);

}