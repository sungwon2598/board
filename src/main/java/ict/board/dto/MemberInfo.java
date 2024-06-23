package ict.board.dto;

import ict.board.domain.board.Board;
import ict.board.domain.member.Role;
import ict.board.domain.reply.Reply;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MemberInfo {

    private final List<Board> boards;
    private final List<Reply> replies;
    private final String memberName;
    private final String memberEmail;
    private final String memberTeam;
    private final String role;

}