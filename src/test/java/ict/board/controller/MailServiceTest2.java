//package ict.board.controller;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doAnswer;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import ict.board.service.mail.EmailRequest;
//import ict.board.service.mail.MailService;
//import jakarta.mail.internet.MimeMessage;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.test.util.ReflectionTestUtils;
//
//@ExtendWith(MockitoExtension.class)
//class MailServiceTest2 {
//
//    @Mock
//    private JavaMailSender mailSender;
//
//    @Mock
//    private MimeMessage mimeMessage;
//
//    private MailService mailService;
//
//    @BeforeEach
//    void setUp() {
//        mailService = new MailService(mailSender);
//        ReflectionTestUtils.setField(mailService, "from", "test");
//        mailService.init();
//    }
//
//    @Test
//    void sendEmail_Success() throws Exception {
//        // Given
//        String to = "test@example.com";
//        String subject = "Test Subject";
//        String text = "Test Content";
//
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//        doNothing().when(mailSender).send(any(MimeMessage.class));
//
//        // When
//        mailService.sendEmail(to, subject, text);
//
//        // Then
//        verify(mailSender, times(1)).send(any(MimeMessage.class));
//    }
//
//    @Test
//    void sendEmail_FailureAndRetry() throws Exception {
//        // Given
//        String to = "test@example.com";
//        String subject = "Test Subject";
//        String text = "Test Content";
//        CountDownLatch latch = new CountDownLatch(2); // 두 번의 시도를 기다림
//
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//
//        // 항상 실패하도록 설정
//        doAnswer(invocation -> {
//            latch.countDown();
//            throw new RuntimeException("Send failed");
//        }).when(mailSender).send(any(MimeMessage.class));
//
//        // When
//        mailService.sendEmail(to, subject, text);
//
//        // Then
//        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for email retries");
//        verify(mailSender, times(2)).send(any(MimeMessage.class));
//
//        // 큐에 실패한 요청이 있는지 확인
//        BlockingQueue<EmailRequest> retryQueue = (BlockingQueue<EmailRequest>)
//                ReflectionTestUtils.getField(mailService, "retryQueue");
//        assertFalse(retryQueue.isEmpty(), "Queue should contain failed request");
//    }
//
//    @Test
//    void sendBulkEmails_Success() throws Exception {
//        // Given
//        List<EmailRequest> requests = Arrays.asList(
//                new EmailRequest("test1@example.com", "Subject 1", "Text 1"),
//                new EmailRequest("test2@example.com", "Subject 2", "Text 2")
//        );
//        CountDownLatch latch = new CountDownLatch(2);
//
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//        doAnswer(invocation -> {
//            latch.countDown();
//            return null;
//        }).when(mailSender).send(any(MimeMessage.class));
//
//        // When
//        mailService.sendBulkEmails(requests);
//
//        // Then
//        assertTrue(latch.await(3, TimeUnit.SECONDS), "Timeout waiting for bulk emails");
//        verify(mailSender, times(2)).send(any(MimeMessage.class));
//    }
//
//    @Test
//    void sendEmail_MaxRetriesExceeded() throws Exception {
//        // Given
//        String to = "test@example.com";
//        String subject = "Test Subject";
//        String text = "Test Content";
//        CountDownLatch latch = new CountDownLatch(3); // 3번의 시도를 기다림
//
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//        doAnswer(invocation -> {
//            latch.countDown();
//            throw new RuntimeException("Send failed");
//        }).when(mailSender).send(any(MimeMessage.class));
//
//        // When
//        mailService.sendEmail(to, subject, text);
//
//        // Then
//        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for max retries");
//        verify(mailSender, times(3)).send(any(MimeMessage.class));  // 초기 시도 + 2번의 재시도
//    }
//
//    @Test
//    void testQueueSize_AfterFailure() throws Exception {
//        // Given
//        String to = "test@example.com";
//        String subject = "Test Subject";
//        String text = "Test Content";
//
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//        doThrow(new RuntimeException("Send failed")).when(mailSender).send(any(MimeMessage.class));
//
//        // When
//        mailService.sendEmail(to, subject, text);
//        Thread.sleep(100); // 큐에 들어가는 것을 기다림
//
//        // Then
//        BlockingQueue<EmailRequest> retryQueue = (BlockingQueue<EmailRequest>)
//                ReflectionTestUtils.getField(mailService, "retryQueue");
//
//        assertNotNull(retryQueue);
//        assertFalse(retryQueue.isEmpty(), "Queue should contain failed request");
//    }
//
//    @Test
//    void shutdown_ExecutorsTerminated() throws Exception {
//        // When
//        mailService.shutdown();
//
//        // Then
//        assertTrue(mailService.getRetryExecutor().isShutdown());
//        assertTrue(mailService.getEmailExecutor().isShutdown());
//    }
//}