package ict.board.controller;

import ict.board.config.argumentresolver.Login;
import ict.board.config.argumentresolver.LoginMemberArgumentResolver.LoginSessionInfo;
import ict.board.domain.member.Building;
import ict.board.domain.member.Location;
import ict.board.domain.member.Member;
import ict.board.domain.member.Role;
import ict.board.dto.MemberForm;
import ict.board.dto.MemberInfo;
import ict.board.repository.IctStaffMemberRepository;
import ict.board.service.MailService;
import ict.board.service.MemberService;
import ict.board.util.CombinedRandomStringGenerator;
import ict.board.util.cache.VerificationCodeCache;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;
    private final VerificationCodeCache verificationCodeCache;
    private final IctStaffMemberRepository ictStaffMemberRepository;

    private final CombinedRandomStringGenerator generator = new CombinedRandomStringGenerator();

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

            String hashedPassword = BCrypt.hashpw(form.getPassword(), BCrypt.gensalt());
            Location location = new Location(Building.valueOf(form.getBuilding()), form.getRoomNumber());
            Member member = new Member(sessionEmail, form.getName(), hashedPassword, location, form.getTeam(),
                    form.getMemberNumber());

            memberService.join(member);

            return "redirect:/";
        }

        return "redirect:/";
    }

    @PostMapping("/members/sendVerificationCode")
    @ResponseBody
    public Map<String, Object> sendVerificationCode(@RequestParam String email, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (memberService.findMemberByEmail(email) != null) {
            response.put("success", false);
            response.put("message", "이미 존재하는 이메일입니다.");
            return response;
        }

        String verificationCode = generator.generateRandomString();
        log.info("email={}", email);
        log.info("verificationCode={}", verificationCode);

        verificationCodeCache.storeCode(email, verificationCode);
        mailService.sendEmail(email, "Verification Code", verificationCode);

        session.setAttribute("userEmail", email);
        session.setAttribute("emailSent", true);

        response.put("success", true);
        return response;
    }

    @GetMapping("/mypage")
    public String myPage(@Login LoginSessionInfo loginSessionInfo, Model model) {
        MemberInfo memberInfo = memberService.getMemberInfo(loginSessionInfo.getEmail());
        if(memberInfo.getRole() == Role.ADMIN ) {
            //List<Member> members = memberService.getAllMembers();
            List<Member> members = ictStaffMemberRepository.findAllByRoleIsNotNull();
            model.addAttribute("members", members);
            model.addAttribute("role", "ADMIN");
        }
        model.addAttribute("memberInfo", memberInfo);
        return "members/mypage";
    }
}