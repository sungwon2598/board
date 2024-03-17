package ict.board.domain.member;

import ict.board.domain.CreateTime;
import ict.board.domain.board.Board;
import ict.board.domain.reply.Reply;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Entity
@Getter
public class Member extends CreateTime {

    public Member(String email, String name, String password, Building building, String team, String memberNumber) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.building = building;
        this.team = team;
        this.memberNumber = memberNumber;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @OneToMany(mappedBy = "member")
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Reply> replies = new ArrayList<>();

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    private String team;

    @Enumerated(EnumType.STRING)
    private Building building;

    @Column(name = "member_number")
    private String memberNumber;

    public Member() {

    }
}
