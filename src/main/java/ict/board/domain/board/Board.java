package ict.board.domain.board;

import ict.board.domain.CreateTime;
import ict.board.domain.reply.Reply;
import ict.board.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.Setter;

@Entity
@Getter @Setter
public class Board extends CreateTime {

    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public void addMember(Member member) {
        member.getBoards().add(this);
        this.setMember(member);
    }

    @OneToMany(mappedBy = "board")
    private List<Reply> replies = new ArrayList<>();

    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
}
