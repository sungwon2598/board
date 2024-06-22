package ict.board.dto;

import ict.board.domain.board.Board;
import ict.board.domain.reply.Reply;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostDetail {

    public boolean isLogin;

    public boolean isManager;

    public String loginMemberEmail;

    public Board board;

    public List<Reply> replies;

}
