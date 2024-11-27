//package ict.board.controller;
//
//
//import static org.hibernate.validator.internal.util.Contracts.assertTrue;
//
//import ict.board.domain.member.Member;
//import ict.board.repository.IctStaffMemberRepository;
//import ict.board.service.mail.EmailRequest;
//import ict.board.service.mail.MailService;
//import java.util.List;
//import java.util.stream.Collectors;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//@SpringBootTest
//@ActiveProfiles("local")
//public class MailServiceICTTest {
//
//    @Autowired
//    private MailService mailService;
//
//    @Autowired
//    private IctStaffMemberRepository ictStaffMemberRepository;
//
//    @Test
//    void testBulkEmailSending() throws InterruptedException {
//        // given
//        List<Member> members = ictStaffMemberRepository.findAllByRoleIsNotNull();
//        List<EmailRequest> emailRequests = members.stream()
//                .map(member -> new EmailRequest(
//                        member.getEmail(),
//                        "테스트 이메일입니다",
//                        "안녕하세요, 테스트 이메일입니다."))
//                .collect(Collectors.toList());
//
//        // when
//        mailService.sendBulkEmails(emailRequests);
//
//        // 비동기 작업 완료 대기
//        Thread.sleep(5000);  // 5초 대기
//
//        // then
//        assertTrue(true, "Bulk email sending completed");  // 예외가 발생하지 않으면 성공
//    }
//
//    @Test
//    void testBulkEmailSendingWithLargeVolume() throws InterruptedException {
//        // given
//        List<Member> baseMembers = ictStaffMemberRepository.findAllByRoleIsNotNull();
//
//        // 각 멤버당 3개의 이메일로 제한
//        List<EmailRequest> emailRequests = baseMembers.stream()
//                .flatMap(member -> {
//                    return java.util.stream.IntStream.range(0, 2)
//                            .mapToObj(i -> new EmailRequest(
//                                    member.getEmail(),
//                                    "대량 테스트 이메일 #" + i,
//                                    "안녕하세요, " + member.getEmail() + "님. 테스트 이메일 #" + i + " 입니다."));
//                })
//                .collect(Collectors.toList());
//
//        // when
//        mailService.sendBulkEmails(emailRequests);
//
//        // 비동기 작업 완료 대기
//        Thread.sleep(10000);  // 10초 대기
//
//        // then
//        assertTrue(true, "Large volume email sending completed");  // 예외가 발생하지 않으면 성공
//    }
//}