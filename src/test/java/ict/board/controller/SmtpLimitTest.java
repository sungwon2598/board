//package ict.board.controller;
//
//import ict.board.service.mail.MailService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@SpringBootTest
//public class SmtpLimitTest {
//
//    @Autowired
//    private MailService mailService;
//
//    @Test
//    void testSmtpRateLimit() throws InterruptedException {
//        String testEmail = "tjddnjs2598@naver.com";
//
//        // 빠른 속도로 연속 발송하여 예외 발생 확인
//        for (int i = 1; i <= 120; i++) {
//            try {
//                log.info("Attempting to send email #{}", i);
//                mailService.sendEmail(
//                        testEmail,
//                        "SMTP 제한 테스트 #" + i,
//                        "SMTP 서버의 rate limit 테스트 메일입니다. 순번: " + i
//                );
//                // 아주 짧은 간격으로 발송
//                Thread.sleep(100);
//            } catch (Exception e) {
//                log.error("Email #{} failed with exception: {}", i, e.getMessage());
//                log.error("Exception type: {}", e.getClass().getName());
//                // 예외 발생 시 잠시 대기 후 계속
//                Thread.sleep(1000);
//            }
//        }
//
//        // 최종 처리 대기
//        Thread.sleep(5000);
//    }
//}