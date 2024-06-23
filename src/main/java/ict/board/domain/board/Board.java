package ict.board.domain.board;

import ict.board.domain.CreateTime;
import ict.board.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Getter
public class Board extends CreateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private BoardStatus boardStatus;

    @Column(name = "requester")
    private String requester;

    @Column(name = "requester_location")
    private String requesterLocation;

    @Column(name = "image_path")
    private String imagePath;

    public Board() {}

    public Board(String title, String content, String requester, String requesterLocation, String imagePath) {
        this.title = title;
        this.content = content;
        this.requester = requester;
        this.requesterLocation = requesterLocation;
        this.imagePath = imagePath;
        this.boardStatus = BoardStatus.UNCHECKED;
    }

    public void addMember(Member member) {
        this.member = member;
    }

    public void changeStatus(BoardStatus boardStatus) {
        this.boardStatus = boardStatus;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeRequester(String requester) {
        this.requester = requester;
    }

    public void changeRequesterLocation(String requesterLocation) {
        this.requesterLocation = requesterLocation;
    }

    public void changeImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}