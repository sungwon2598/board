package ict.board.service.mail;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final BlockingQueue<EmailRequest> retryQueue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(1);
    private final ExecutorService emailExecutor = Executors.newFixedThreadPool(2); // 동시 처리 스레드 수 2로 감소

    @Value("${spring.mail.username}")
    private String from;

    private static final int MAX_BATCH_SIZE = 3; // 배치 크기 3으로 감소
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 2000; // 재시도 간격 2초로 증가
    private static final long BATCH_DELAY_MS = 2000; // 배치간 딜레이 2초로 증가

    @PostConstruct
    public void init() {
        startRetryProcessor();
    }

    private void startRetryProcessor() {
        retryExecutor.scheduleWithFixedDelay(() -> {
            try {
                EmailRequest request = retryQueue.poll();
                if (request != null && request.getRetryCount() < MAX_RETRY_ATTEMPTS) {
                    Thread.sleep(RETRY_DELAY_MS);
                    processEmail(request);
                } else if (request != null) {
                    log.error("Max retry attempts reached for email to: {}", request.getTo());
                }
            } catch (Exception e) {
                log.error("Error in retry processor: ", e);
            }
        }, 0, RETRY_DELAY_MS, TimeUnit.MILLISECONDS);
    }

    public void sendEmail(String to, String subject, String text) {
        if (!isValidEmail(to)) {
            log.error("Invalid email address: {}", to);
            return;
        }
        EmailRequest request = new EmailRequest(to, subject, text);
        processEmail(request);
    }

    private void processEmail(EmailRequest request) {
        try {
            if (!isValidEmail(request.getTo())) {
                log.error("Invalid email address: {}", request.getTo());
                return;
            }

            log.debug("Attempting to send email to: {} (Attempt {})",
                    request.getTo(), request.getRetryCount() + 1);

            MimeMessage mimeMessage = createMimeMessage(
                    request.getTo(), request.getSubject(), request.getText()
            );
            mailSender.send(mimeMessage);
            log.info("Email sent successfully to: {}", request.getTo());

        } catch (Exception e) {
            log.error("Failed to send email to: {} (Attempt {}). Error: {}",
                    request.getTo(), request.getRetryCount() + 1, e.getMessage());

            if (request.getRetryCount() < MAX_RETRY_ATTEMPTS) {
                request.incrementRetryCount();
                retryQueue.offer(request);
            }
        }
    }

    public void sendBulkEmails(List<EmailRequest> requests) {
        log.info("Starting bulk email send for {} recipients", requests.size());

        requests = requests.stream()
                .filter(request -> isValidEmail(request.getTo()))
                .toList();

        for (int i = 0; i < requests.size(); i += MAX_BATCH_SIZE) {
            int endIndex = Math.min(i + MAX_BATCH_SIZE, requests.size());
            List<EmailRequest> batch = requests.subList(i, endIndex);
            processBatch(batch);

            try {
                Thread.sleep(BATCH_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Batch processing interrupted", e);
            }
        }
    }

    private void processBatch(List<EmailRequest> batch) {
        batch.forEach(request ->
                CompletableFuture.runAsync(
                        () -> processEmail(request),
                        emailExecutor
                ).exceptionally(throwable -> {
                    log.error("Async execution failed for recipient: {}. Error: {}",
                            request.getTo(), throwable.getMessage());
                    return null;
                })
        );
    }

    private boolean isValidEmail(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private MimeMessage createMimeMessage(String to, String subject, String text)
            throws Exception {
        log.debug("Creating MimeMessage for recipient: {}", to);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
        messageHelper.setFrom(from + "@naver.com");
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(text);
        return mimeMessage;
    }

    @PreDestroy
    public void shutdown() {
        retryExecutor.shutdown();
        emailExecutor.shutdown();
    }

    public ScheduledExecutorService getRetryExecutor() {
        return retryExecutor;
    }

    public ExecutorService getEmailExecutor() {
        return emailExecutor;
    }
}