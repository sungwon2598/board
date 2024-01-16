package ict.board.domain.reply;

import ict.board.domain.CreateTime;
import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Reply extends CreateTime {

    @Id
    @GeneratedValue
    @Column(name = "reply_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    public void addBoard(Board board) {
        board.getReplies().add(this);
        this.setBoard(board);
    }

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public void addMember(Member member) {
        member.getReplies().add(this);
        this.setMember(member);
    }

    @Lob
    @Column(length = 10000)
    private String content;

}
