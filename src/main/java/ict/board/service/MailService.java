//package ict.board.service;
//
//import jakarta.mail.internet.MimeMessage;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class MailService {
//
//    private final JavaMailSender mailSender;
//
//    @Value("${spring.mail.username}")
//    private String from;
//
//    public void sendEmail(String to, String subject, String text) {
//        try {
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
//            messageHelper.setFrom("tjddnjs2598@naver.com");
//            messageHelper.setTo(to);
//            messageHelper.setSubject(subject);
//            messageHelper.setText(text);
//            mailSender.send(mimeMessage);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}