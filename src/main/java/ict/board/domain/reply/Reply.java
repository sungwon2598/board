package ict.board.domain.reply;

import ict.board.domain.CreateTime;
import ict.board.domain.board.Board;
import ict.board.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class Reply extends CreateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @Lob
    @Column(length = 10000)
    private String content;

    public Reply(String content, Board board, Member member) {
        this.content = content;
        this.board = board;
        addMember(member);
    }

    public Reply() {

    }

    private void addMember(Member member) {
        //member.getReplies().add(this);
        this.member = member;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

}
