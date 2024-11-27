package ict.board.service.mail;

@lombok.Data
@lombok.AllArgsConstructor
public class EmailRequest {
    private String to;
    private String subject;
    private String text;
}