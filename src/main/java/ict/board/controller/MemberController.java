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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
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

    @PostMapping("/members/email-verification")
    public String sendVerificationEmail(@ModelAttribute MemberForm form, BindingResult result, Model model,
                                        HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("memberForm", form);
            return "members/emailVerificationForm";
        }

        String verificationCode = generator.generateRandomString();
        log.info("form.getEmail={}", form.getEmail());
        log.info("verificationCode={}", verificationCode);

        verificationCodeCache.storeCode(form.getEmail(), verificationCode);
        mailService.sendEmail(form.getEmail(), "Verification Code", verificationCode);

        session.setAttribute("userEmail", form.getEmail());

        model.addAttribute("emailSent", true);
        model.addAttribute("memberForm", form);
        return "members/memberRegistrationForm";
    }

    @PostMapping("/members/new")
    public String create(MemberForm form, BindingResult result, Model model, HttpSession session) {
        String sessionEmail = (String) session.getAttribute("userEmail");
        boolean validCode = verificationCodeCache.isValidCode(sessionEmail, form.getVerificationCode());
        if (!validCode) {
            log.info("!valid!!!!!!");
            model.addAttribute("memberForm", form);
            model.addAttribute("codeError", "Verification code is invalid or expired.");
            return "members/memberRegistrationForm";
        }

        log.info("sessionEmail={}", sessionEmail);

        String hashedPassword = BCrypt.hashpw(form.getPassword(), BCrypt.gensalt());
        Location location = new Location(Building.valueOf(form.getBuilding()), form.getRoomNumber());
        Member member = new Member(sessionEmail, form.getName(), hashedPassword, location, form.getTeam(),
                form.getMemberNumber());

        memberService.join(member);

        return "redirect:/";
    }


    @GetMapping("/mypage")
    public String myPage(@Login String loginMemberEmail, Model model) {
        MemberInfo memberInfo = memberService.getMemberInfo(loginMemberEmail);
        model.addAttribute("memberInfo", memberInfo);
        return "members/mypage";
    }

}