package ict.board.controller.Member;

import ict.board.domain.member.Address;
import ict.board.domain.member.Building;
import ict.board.domain.member.Member;
import ict.board.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public String create(@Valid MemberForm form, BindingResult result) {

        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getTeam(), Building.valueOf(form.getBuilding()), form.getZipcode());
        Member member = new Member(form.getEmail(), form.getName(), form.getPassword(), address);

        memberService.join(member);
        return "redirect:/";
    }

}
