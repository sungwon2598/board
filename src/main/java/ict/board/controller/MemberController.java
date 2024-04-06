package ict.board.controller;

import ict.board.config.argumentresolver.Login;
import ict.board.domain.board.Board;
import ict.board.domain.member.Building;
import ict.board.domain.member.Member;
import ict.board.domain.reply.Reply;
import ict.board.dto.MemberForm;
import ict.board.dto.MemberInfo;
import ict.board.service.BoardService;
import ict.board.service.MemberService;
import ict.board.service.ReplyService;
import jakarta.validation.Valid;
import java.util.List;
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

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("memberForm", form); // 사용자가 입력한 데이터를 모델에 추가
            return "members/createMemberForm";
        }

        String hashedPassword = BCrypt.hashpw(form.getPassword(), BCrypt.gensalt());
        Member member = new Member(form.getEmail(), form.getName(), hashedPassword,
                Building.valueOf(form.getBuilding()), form.getTeam(), form.getMemberNumber());

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
