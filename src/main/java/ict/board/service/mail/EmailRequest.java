package ict.board.service.mail;

import lombok.Data;

@Data
public class EmailRequest {
    private String to;
    private String subject;
    private String text;
    private int retryCount;

    public EmailRequest(String to, String subject, String text) {
        this.to = to;
        this.subject = subject;
        this.text = text;
        this.retryCount = 0;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public void resetRetryCount() {
        this.retryCount = 0;
    }
}