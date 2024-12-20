package ict.board.controller.member;

import ict.board.domain.member.Building;
import ict.board.domain.member.Location;
import ict.board.domain.member.Member;
import ict.board.dto.MemberForm;
import ict.board.dto.MemberInfo;

import ict.board.service.MemberService;
import ict.board.service.mail.MailService;
import ict.board.util.CombinedRandomStringGenerator;
import ict.board.util.cache.VerificationCodeCache;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;
    private final VerificationCodeCache verificationCodeCache;
    private final CombinedRandomStringGenerator generator;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        MemberForm memberForm = new MemberForm();
        model.addAttribute("memberForm", memberForm);
        model.addAttribute("emailSent", false);
        return "members/registrationForm";
    }

    @PostMapping("/members/new")
    public String createOrVerify(@Valid @ModelAttribute("memberForm") MemberForm form, BindingResult result,
                                 Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("memberForm", form);
            model.addAttribute("emailSent",
                    session.getAttribute("emailSent") != null && (Boolean) session.getAttribute("emailSent"));
            return "members/registrationForm";
        }

        String sessionEmail = (String) session.getAttribute("userEmail");
        if (form.getVerificationCode() != null) {
            boolean validCode = verificationCodeCache.isValidCode(sessionEmail, form.getVerificationCode());
            if (!validCode) {
                model.addAttribute("memberForm", form);
                model.addAttribute("codeError", "Verification code is invalid or expired.");
                model.addAttribute("emailSent", true);
                return "members/registrationForm";
            }

            String encodedPassword = passwordEncoder.encode(form.getPassword());
            Location location = new Location(Building.valueOf(form.getBuilding()), form.getRoomNumber());
            Member member = new Member(sessionEmail, form.getName(), encodedPassword, location, form.getTeam(),
                    form.getMemberNumber());

            memberService.join(member);

            return "redirect:/";
        }

        return "redirect:/";
    }

    @PostMapping("/members/sendVerificationCode")
    @ResponseBody
    public Map<String, Object> sendVerificationCode(@RequestParam String email, HttpSession session) throws Exception {
        Map<String, Object> response = new HashMap<>();
        if (memberService.findMemberByEmail(email) != null) {
            response.put("success", false);
            response.put("message", "이미 존재하는 이메일입니다.");
            return response;
        }

        String verificationCode = generator.generateRandomString();
        log.info("email={}", email);

        verificationCodeCache.storeCode(email, verificationCode);
        mailService.sendEmail(email, "Verification Code", verificationCode);

        session.setAttribute("userEmail", email);
        session.setAttribute("emailSent", true);

        response.put("success", true);
        return response;
    }

    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        MemberInfo memberInfo = memberService.getMemberInfo(userDetails.getUsername());
        model.addAttribute("memberInfo", memberInfo);
        return "members/mypage";
    }
}