package ict.board.controller;

import ict.board.config.argumentresolver.Login;
import ict.board.domain.member.Building;
import ict.board.domain.member.Member;
import ict.board.dto.MemberForm;
import ict.board.dto.MemberInfo;
import ict.board.service.MailService;
import ict.board.service.MemberService;
import ict.board.util.CombinedRandomStringGenerator;
import ict.board.util.cache.VerificationCodeCache;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor @Slf4j
public class MemberController {

    @Autowired
    private final MemberService memberService;
    @Autowired
    private final MailService mailService;
    @Autowired
    private final VerificationCodeCache verificationCodeCache;

    private final CombinedRandomStringGenerator generator = new CombinedRandomStringGenerator();

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/emailVerificationForm";
    }

//    @PostMapping("/members/email-verification")
//    public String sendVerificationEmail(@ModelAttribute MemberForm form, BindingResult result, Model model) {
//        if (result.hasErrors()) {
//            model.addAttribute("memberForm", form);
//            return "members/createMemberForm";
//        }
//
//        String verificationCode = generator.generateRandomString();
//        log.info("form.getEmail={}",form.getEmail());
//        log.info("verificationCode={}",verificationCode);
//
//        verificationCodeCache.storeCode(form.getEmail(), verificationCode);
//        mailService.sendEmail(form.getEmail(), verificationCode, verificationCode);
//
//        model.addAttribute("emailSent", true);
//        model.addAttribute("memberForm", form); // 폼 데이터 유지
//        return "members/createMemberForm"; // 이메일 전송 후 동일 폼 반환
//    }

    @PostMapping("/members/email-verification")
    public String sendVerificationEmail(@ModelAttribute MemberForm form, BindingResult result, Model model,
                                        HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("memberForm", form);
            return "members/emailVerificationForm"; // 이메일 인증 폼으로 이동
        }

        String verificationCode = generator.generateRandomString();
        log.info("form.getEmail={}", form.getEmail());
        log.info("verificationCode={}", verificationCode);

        verificationCodeCache.storeCode(form.getEmail(), verificationCode);
        mailService.sendEmail(form.getEmail(), "Verification Code", verificationCode); // 제목 명시

        session.setAttribute("userEmail", form.getEmail());

        model.addAttribute("emailSent", true);
        model.addAttribute("memberForm", form); // 폼 데이터 유지
        return "members/memberRegistrationForm"; // 이메일 전송 후 동일 폼 반환
    }


//    @PostMapping("/members/new")
//    public String create(@Valid MemberForm form, BindingResult result, Model model) throws Exception {
//
//        if (result.hasErrors()) {
//            model.addAttribute("memberForm", form);
//            return "members/createMemberForm";
//        }
//
//        boolean validCode = verificationCodeCache.isValidCode(form.getEmail(), form.getVerificationCode());
//        if (!validCode) {
//            // 인증 코드가 유효하지 않을 경우, 오류 메시지를 설정
//            model.addAttribute("memberForm", form);
//            model.addAttribute("codeError", "Verification code is invalid or expired.");
//            return "members/createMemberForm"; // 사용자를 회원 가입 양식 페이지로 다시 이동
//        }
//
//        String hashedPassword = BCrypt.hashpw(form.getPassword(), BCrypt.gensalt());
//        Member member = new Member(form.getEmail(), form.getName(), hashedPassword,
//                Building.valueOf(form.getBuilding()), form.getTeam(), form.getMemberNumber());
//
//        memberService.join(member);
//
//        return "redirect:/";
//    }

    @PostMapping("/members/new")
    public String create(MemberForm form, BindingResult result, Model model, HttpSession session) {
        String sessionEmail = (String) session.getAttribute("userEmail"); // 세션에서 이메일 가져오기
        boolean validCode = verificationCodeCache.isValidCode(sessionEmail, form.getVerificationCode());
        if (!validCode) {
            log.info("!valid!!!!!!");
            model.addAttribute("memberForm", form);
            model.addAttribute("codeError", "Verification code is invalid or expired.");
            return "members/memberRegistrationForm"; // 오류 메시지와 함께 회원 정보 입력 폼으로 다시 이동
        }

        log.info("sessionEmail={}", sessionEmail);
        String hashedPassword = BCrypt.hashpw(form.getPassword(), BCrypt.gensalt());
        Member member = new Member(sessionEmail, form.getName(), hashedPassword,
                Building.valueOf(form.getBuilding()), form.getTeam(), form.getMemberNumber());

        memberService.join(member);

        return "redirect:/"; // 회원가입 완료 후 홈페이지로 리다이렉션
    }


    @GetMapping("/mypage")
    public String myPage(@Login String loginMemberEmail, Model model) {
        MemberInfo memberInfo = memberService.getMemberInfo(loginMemberEmail);
        model.addAttribute("memberInfo", memberInfo);
        return "members/mypage";
    }

}
