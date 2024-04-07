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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;
    private final VerificationCodeCache verificationCodeCache;

    private final CombinedRandomStringGenerator generator = new CombinedRandomStringGenerator();

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    //추후 비즈니스 로직 다 분리해야함
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result, Model model) throws Exception {

        if (result.hasErrors()) {
            model.addAttribute("memberForm", form);
            return "members/createMemberForm";
        }

        String hashedPassword = BCrypt.hashpw(form.getPassword(), BCrypt.gensalt());
        Member member = new Member(form.getEmail(), form.getName(), hashedPassword,
                Building.valueOf(form.getBuilding()), form.getTeam(), form.getMemberNumber());

        String verifiactionCode = generator.generateRandomString();
        verificationCodeCache.storeCode(form.getEmail(), verifiactionCode);

        mailService.sendEmail("tjddnjs2598@gmail.com", "테스트 제목", verifiactionCode);
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
