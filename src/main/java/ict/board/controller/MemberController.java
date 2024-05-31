package ict.board.controller;

import ict.board.config.argumentresolver.Login;
import ict.board.domain.member.Building;
import ict.board.domain.member.Location;
import ict.board.domain.member.Member;
import ict.board.dto.MemberForm;
import ict.board.dto.MemberInfo;
import ict.board.service.MailService;
import ict.board.service.MemberService;
import ict.board.util.CombinedRandomStringGenerator;
import ict.board.util.cache.VerificationCodeCache;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;
    private final VerificationCodeCache verificationCodeCache;

    private final CombinedRandomStringGenerator generator = new CombinedRandomStringGenerator();

    @GetMapping("/members/new")
    public String createForm(Model model) {
        MemberForm memberForm = new MemberForm();
        model.addAttribute("memberForm", memberForm);
        model.addAttribute("emailSent", false);
        return "members/registrationForm";
    }

    @PostMapping("/members/new")
    public String createOrVerify(MemberForm form, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("memberForm", form);
            model.addAttribute("emailSent", session.getAttribute("emailSent") != null && (Boolean) session.getAttribute("emailSent"));
            return "members/registrationForm";
        }

        if (form.getEmail() != null && form.getVerificationCode() == null) {
            // 이메일 인증 코드 발송
            if (memberService.findMemberByEmail(form.getEmail()) != null) {
                result.rejectValue("email", "duplicate", "이미 존재하는 이메일입니다.");
                model.addAttribute("memberForm", form);
                return "members/registrationForm";
            }

            String verificationCode = generator.generateRandomString();
            log.info("form.getEmail={}", form.getEmail());
            log.info("verificationCode={}", verificationCode);

            verificationCodeCache.storeCode(form.getEmail(), verificationCode);
            mailService.sendEmail(form.getEmail(), "Verification Code", verificationCode);

            session.setAttribute("userEmail", form.getEmail());
            session.setAttribute("emailSent", true);

            model.addAttribute("emailSent", true);
            model.addAttribute("memberForm", form);
            return "members/registrationForm";
        } else {
            // 회원가입 처리
            String sessionEmail = (String) session.getAttribute("userEmail");
            boolean validCode = verificationCodeCache.isValidCode(sessionEmail, form.getVerificationCode());
            if (!validCode) {
                log.info("!valid!!!!!!");
                model.addAttribute("memberForm", form);
                model.addAttribute("codeError", "Verification code is invalid or expired.");
                model.addAttribute("emailSent", true);
                return "members/registrationForm";
            }

            log.info("sessionEmail={}", sessionEmail);

            String hashedPassword = BCrypt.hashpw(form.getPassword(), BCrypt.gensalt());
            Location location = new Location(Building.valueOf(form.getBuilding()), form.getRoomNumber());
            Member member = new Member(sessionEmail, form.getName(), hashedPassword, location, form.getTeam(), form.getMemberNumber());

            memberService.join(member);

            return "redirect:/";
        }
    }

    @GetMapping("/mypage")
    public String myPage(@Login String loginMemberEmail, Model model) {
        MemberInfo memberInfo = memberService.getMemberInfo(loginMemberEmail);
        model.addAttribute("memberInfo", memberInfo);
        return "members/mypage";
    }

}