package ict.board.dto;

import ict.board.domain.board.Board;
import ict.board.domain.member.Role;
import ict.board.domain.reply.Reply;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInfo {

    private final List<Board> boards;
    private final List<Reply> replies;
    private final String memberName;
    private final String memberEmail;
    private final String memberTeam;
    private final Role role;

    public MemberInfo(List<Board> boards, List<Reply> replies, String memberName, String memberEmail, String memberTeam,
                      Role role) {
        this.boards = boards;
        this.replies = replies;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.memberTeam = memberTeam;
        this.role = role;
    }

    public MemberInfo(List<Board> boards, List<Reply> replies, String memberName, String memberEmail,
                      String memberTeam) {
        this(boards, replies, memberName, memberEmail, memberTeam, null);
    }
}
