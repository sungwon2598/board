//package ict.board.controller;
//
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.mockito.ArgumentMatchers.any;
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
//import java.util.concurrent.TimeUnit;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.test.util.ReflectionTestUtils;
//
//@ExtendWith(MockitoExtension.class)
//class MailServiceTest {
//
//    @Mock
//    private JavaMailSender mailSender;
//
//    @Mock
//    private MimeMessage mimeMessage;
//
//    @InjectMocks
//    private MailService mailService;
//
//    @BeforeEach
//    void setUp() {
//        ReflectionTestUtils.setField(mailService, "from", "test");
//    }
//
//    @Test
//    void sendEmail_Success() throws Exception {
//        // Given
//        String to = "recipient@example.com";
//        String subject = "Test Subject";
//        String text = "Test Content";
//
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//        doNothing().when(mailSender).send(any(MimeMessage.class));
//
//        // When & Then
//        assertDoesNotThrow(() -> mailService.sendEmail(to, subject, text));
//        verify(mailSender, times(1)).send(any(MimeMessage.class));
//    }
//
//    @Test
//    void sendBulkEmails_Success() throws Exception {
//        // Given
//        List<EmailRequest> requests = Arrays.asList(
//                new EmailRequest("recipient1@example.com", "Subject 1", "Content 1"),
//                new EmailRequest("recipient2@example.com", "Subject 2", "Content 2"),
//                new EmailRequest("recipient3@example.com", "Subject 3", "Content 3")
//        );
//
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//        doNothing().when(mailSender).send(any(MimeMessage.class));
//
//        // When
//        mailService.sendBulkEmails(requests);
//
//        // Then
//        // Add delay to allow async operations to complete
//        TimeUnit.SECONDS.sleep(2);
//        verify(mailSender, times(3)).createMimeMessage();
//        verify(mailSender, times(3)).send(any(MimeMessage.class));
//    }
//
//    @Test
//    void sendBulkEmails_WithBatching() throws Exception {
//        // Given
//        List<EmailRequest> requests = Arrays.asList(
//                new EmailRequest("recipient1@example.com", "Subject 1", "Content 1"),
//                new EmailRequest("recipient2@example.com", "Subject 2", "Content 2"),
//                new EmailRequest("recipient3@example.com", "Subject 3", "Content 3"),
//                new EmailRequest("recipient4@example.com", "Subject 4", "Content 4"),
//                new EmailRequest("recipient5@example.com", "Subject 5", "Content 5")
//        );
//
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//        doNothing().when(mailSender).send(any(MimeMessage.class));
//
//        // When
//        mailService.sendBulkEmails(requests);
//
//        // Then
//        // Add delay to allow async operations to complete
//        TimeUnit.SECONDS.sleep(2);
//        verify(mailSender, times(5)).createMimeMessage();
//        verify(mailSender, times(5)).send(any(MimeMessage.class));
//    }
//}