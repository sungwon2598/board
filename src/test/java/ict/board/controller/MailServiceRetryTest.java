//package ict.board.controller;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.atLeastOnce;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import ict.board.service.mail.MailService;
//import jakarta.mail.internet.MimeMessage;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mail.MailSendException;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.util.ReflectionTestUtils;
//
//@Slf4j
//@SpringBootTest
//@ActiveProfiles("local")
//public class MailServiceRetryTest {
//
//    @Autowired
//    private MailService mailService;
//
//    @Autowired
//    private JavaMailSender originalMailSender;
//
//    private JavaMailSender mockMailSender;
//
//    @BeforeEach
//    void setUp() {
//        // 테스트를 위해 재시도 간격을 5초로 설정
//        mailService.setHourlyRetryDelay(TimeUnit.SECONDS.toMillis(5));
//
//        // Mock JavaMailSender 설정
//        mockMailSender = Mockito.mock(JavaMailSender.class);
//        when(mockMailSender.createMimeMessage()).thenReturn(originalMailSender.createMimeMessage());
//
//        // 처음 3번은 실패, 4번째는 성공하도록 설정
//        doThrow(new MailSendException("Simulated failure"))
//                .doThrow(new MailSendException("Simulated failure"))
//                .doThrow(new MailSendException("Simulated failure"))
//                .doNothing()
//                .when(mockMailSender).send(any(MimeMessage.class));
//
//        // Mock 교체
//        ReflectionTestUtils.setField(mailService, "mailSender", mockMailSender);
//    }
//
//    @Test
//    void testEmailRetryAfterDelay() throws InterruptedException {
//        // given
//        String testEmail = "test@example.com";
//        String subject = "Test Subject";
//        String content = "Test Content";
//
//        // when
//        mailService.sendEmail(testEmail, subject, content);
//
//        // 충분한 대기 시간 설정
//        // - 3번의 즉시 재시도 (각 2초) = 6초
//        // - hourly retry delay (5초)
//        // - 여유 시간 4초
//        Thread.sleep(TimeUnit.SECONDS.toMillis(15));
//
//        // then
//        verify(mockMailSender, times(4)).send(any(MimeMessage.class));
//
//        // 검증 실패 시 로그 확인
//        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
//        verify(mockMailSender, atLeastOnce()).send(messageCaptor.capture());
//        List<MimeMessage> sentMessages = messageCaptor.getAllValues();
//        log.info("Total messages sent: {}", sentMessages.size());
//    }
//
//    @AfterEach
//    void cleanup() {
//        // 원래의 mailSender로 복구
//        ReflectionTestUtils.setField(mailService, "mailSender", originalMailSender);
//        // 원래의 재시도 간격으로 복구
//        mailService.setHourlyRetryDelay(TimeUnit.HOURS.toMillis(1));
//    }
//}