package ict.board.dto;

import ict.board.domain.reply.Reply;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReplyDto {
    private Long id;
    private String content;
    private String memberName;    // 복호화된 이름
    private String memberEmail;   // 복호화된 이메일
    private String team;
    private LocalDateTime createdDate;

    public ReplyDto(Reply reply) {
        this.id = reply.getId();
        this.content = reply.getContent();
        this.memberName = reply.getMember().getName();    // 자동으로 복호화됨
        this.memberEmail = reply.getMember().getEmail();  // 자동으로 복호화됨
        this.team = reply.getMember().getTeam();
        this.createdDate = reply.getCreatedAt();
    }
}