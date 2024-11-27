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

    private static long hourlyRetryDelay = TimeUnit.HOURS.toMillis(1);

    private final JavaMailSender mailSender;
    private final BlockingQueue<EmailRequest> immediateRetryQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<DelayedEmailRequest> hourlyRetryQueue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService hourlyRetryExecutor = Executors.newScheduledThreadPool(1);
    private final ExecutorService emailExecutor = Executors.newFixedThreadPool(2);

    @Value("${spring.mail.username}")
    private String from;

    public void setHourlyRetryDelay(long delay) {
        this.hourlyRetryDelay = delay;
    }

    private static final int MAX_BATCH_SIZE = 3;
    private static final int MAX_IMMEDIATE_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 2000;
    private static final long BATCH_DELAY_MS = 2000;
   // private static final long HOURLY_RETRY_DELAY_MS = TimeUnit.HOURS.toMillis(1);

    @PostConstruct
    public void init() {
        startImmediateRetryProcessor();
        startHourlyRetryProcessor();
    }

    private void startImmediateRetryProcessor() {
        retryExecutor.scheduleWithFixedDelay(() -> {
            try {
                EmailRequest request = immediateRetryQueue.poll();
                if (request != null && request.getRetryCount() < MAX_IMMEDIATE_RETRY_ATTEMPTS) {
                    Thread.sleep(RETRY_DELAY_MS);
                    processEmailWithRetry(request);
                } else if (request != null) {
                    log.info("Moving to hourly retry queue for email to: {}", request.getTo());
                    hourlyRetryQueue.offer(new DelayedEmailRequest(request));
                }
            } catch (Exception e) {
                log.error("Error in immediate retry processor: ", e);
            }
        }, 0, RETRY_DELAY_MS, TimeUnit.MILLISECONDS);
    }

    private void startHourlyRetryProcessor() {
        hourlyRetryExecutor.scheduleWithFixedDelay(() -> {
            try {
                DelayedEmailRequest delayedRequest = hourlyRetryQueue.peek();
                if (delayedRequest != null) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime >= delayedRequest.getNextRetryTime()) {
                        delayedRequest = hourlyRetryQueue.poll();
                        if (delayedRequest != null) {
                            log.info("Attempting hourly retry for email to: {}", delayedRequest.getRequest().getTo());
                            EmailRequest request = delayedRequest.getRequest();
                            request.resetRetryCount();
                            processEmailWithRetry(request);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error in hourly retry processor: ", e);
            }
        }, 0, 1, TimeUnit.SECONDS); // 1초마다 체크하도록 변경
    }

    public void sendEmail(String to, String subject, String text) {
        if (!isValidEmail(to)) {
            log.error("Invalid email address: {}", to);
            return;
        }
        EmailRequest request = new EmailRequest(to, subject, text);
        processEmailWithRetry(request);
    }

    private void processEmailWithRetry(EmailRequest request) {
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

            request.incrementRetryCount();
            if (request.getRetryCount() < MAX_IMMEDIATE_RETRY_ATTEMPTS) {
                immediateRetryQueue.offer(request);
            } else {
                hourlyRetryQueue.offer(new DelayedEmailRequest(request));
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
                        () -> processEmailWithRetry(request),
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
        hourlyRetryExecutor.shutdown();
        emailExecutor.shutdown();
    }

    public ScheduledExecutorService getRetryExecutor() {
        return retryExecutor;
    }

    public ExecutorService getEmailExecutor() {
        return emailExecutor;
    }

    // DelayedEmailRequest 내부 클래스 추가
    private static class DelayedEmailRequest {
        private final EmailRequest request;
        private final long nextRetryTime;

        public DelayedEmailRequest(EmailRequest request) {
            this.request = request;
            this.nextRetryTime = System.currentTimeMillis() + hourlyRetryDelay;
        }

        public EmailRequest getRequest() {
            return request;
        }

        public long getNextRetryTime() {
            return nextRetryTime;
        }
    }
}