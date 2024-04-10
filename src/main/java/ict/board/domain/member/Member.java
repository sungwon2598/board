package ict.board.domain.member;

import ict.board.domain.CreateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Member extends CreateTime {

    public Member(String email, String name, String password, Location location, String team, String memberNumber) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.location = location;
        this.team = team;
        this.memberNumber = memberNumber;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

//    @OneToMany(mappedBy = "member")
//    private List<Board> boards = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member")
//    private List<Reply> replies = new ArrayList<>();

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    private String team;

//    @Enumerated(EnumType.STRING)
//    private Building building;

    @Embedded
    private Location location;

    @Column(name = "member_number")
    private String memberNumber;

    public Member() {

    }
}