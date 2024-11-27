package ict.board.service.mail;


import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final Executor emailExecutor = Executors.newFixedThreadPool(10);

    @Value("${spring.mail.username}")
    private String from;

    private static final int MAX_BATCH_SIZE = 50;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    @Retryable(
            value = {Exception.class},
            maxAttempts = MAX_RETRY_ATTEMPTS,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            listeners = "retryListenerSupport"
    )
    public void sendEmail(String to, String subject, String text) throws Exception {
        try {
            log.debug("Attempting to send email to: {} (Attempt {})", to, getCurrentRetryCount());
            MimeMessage mimeMessage = createMimeMessage(to, subject, text);
            mailSender.send(mimeMessage);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {} (Attempt {}). Error: {}",
                    to, getCurrentRetryCount(), e.getMessage());
            throw e; // 원래 예외를 다시 throw하여 retry 메커니즘이 동작하도록 함
        }
    }

    @Recover
    public void recover(Exception e, String to, String subject, String text) {
        log.error("All attempts to send email to {} failed. Final error: {}", to, e.getMessage());
        // 여기서 실패 처리 로직을 구현할 수 있습니다 (예: DB에 실패 기록, 관리자에게 알림 등)
    }

    public void sendBulkEmails(List<EmailRequest> requests) {
        log.info("Starting bulk email send for {} recipients", requests.size());
        for (int i = 0; i < requests.size(); i += MAX_BATCH_SIZE) {
            int endIndex = Math.min(i + MAX_BATCH_SIZE, requests.size());
            List<EmailRequest> batch = requests.subList(i, endIndex);
            log.debug("Processing batch {} to {} of {}", i, endIndex, requests.size());
            processBatch(batch);
        }
    }

    private void processBatch(List<EmailRequest> batch) {
        batch.forEach(request ->
                CompletableFuture.runAsync(() -> {
                            try {
                                sendEmail(request.getTo(), request.getSubject(), request.getText());
                            } catch (Exception e) {
                                log.error("Failed to send email in batch to: {}. Error: {}",
                                        request.getTo(), e.getMessage());
                            }
                        }, emailExecutor)
                        .exceptionally(throwable -> {
                            log.error("Async execution failed for recipient: {}. Error: {}",
                                    request.getTo(), throwable.getMessage());
                            return null;
                        })
        );
    }

    private MimeMessage createMimeMessage(String to, String subject, String text) throws Exception {
        log.debug("Creating MimeMessage for recipient: {}", to);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
        messageHelper.setFrom(from + "@naver.com");
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(text);
        return mimeMessage;
    }

    // 현재 재시도 횟수를 가져오는 메서드 (Spring Retry의 RetryContext에서 가져올 수 있음)
    private int getCurrentRetryCount() {
        try {
            return org.springframework.retry.support.RetrySynchronizationManager
                    .getContext().getRetryCount();
        } catch (Exception e) {
            return 0;
        }
    }
}