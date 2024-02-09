package ict.board.domain.board;

import ict.board.domain.CreateTime;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Entity
@Getter
public class Board extends CreateTime {

    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Reply> replies = new ArrayList<>();

    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private BoardStatus boardStatus;

    public Board() {

    }

    public Board (String title, String content) {
        this.title = title;
        this.content = content;
        this.boardStatus = BoardStatus.UNCHECKED;
    }

    public void addMember(Member member) {
        member.getBoards().add(this);
        this.member = member;
    }

    public void changeStatus(BoardStatus boardStatus) {
        if (boardStatus.equals(BoardStatus.UNCHECKED)) {
            this.boardStatus = BoardStatus.UNCHECKED;
        } else if (boardStatus.equals(BoardStatus.IN_PROGRESS)) {
            this.boardStatus = BoardStatus.IN_PROGRESS;
        } else if (boardStatus.equals(BoardStatus.COMPLETED)) {
            this.boardStatus = BoardStatus.COMPLETED;
        }
    }
}
