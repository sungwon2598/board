package ict.board.domain.member;

import ict.board.domain.CreateTime;
import ict.board.domain.board.Board;
import ict.board.domain.reply.Reply;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Entity
@Getter
public class Member extends CreateTime {

    public Member(String email, String name, String password, Address address) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.address = address;
    }

    @Id
    @GeneratedValue
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

    @Embedded
    private Address address;

    public Member() {

    }
}
