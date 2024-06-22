package ict.board.controller;

import ict.board.domain.member.Building;
import ict.board.domain.member.IctStaffMember;
import ict.board.domain.member.Location;
import ict.board.domain.member.Member;
import ict.board.domain.member.PendingIctStaffMember;
import ict.board.domain.member.Role;
import ict.board.domain.member.ShiftType;
import ict.board.dto.AllMembersInfo;
import ict.board.repository.PendingIctStaffMemberRepository;
import ict.board.service.BoardService;
import ict.board.service.IctStaffMemberService;
import ict.board.service.MailService;
import ict.board.service.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/*")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final MailService mailService;
    private final BoardService boardService;
    private final IctStaffMemberService ictStaffMemberService;
    private final MemberService memberService;
    private final PendingIctStaffMemberRepository pendingIctStaffMemberRepository;

    @GetMapping("/approve-members")
    public String approveMembers(Model model) {
        model.addAttribute("pendingMembers", pendingIctStaffMemberRepository.findAll());
        return "members/approveMembers";
    }

    @PostMapping("/approve-member/{id}")
    public String approveMember(@PathVariable Long id) {
        PendingIctStaffMember pendingMember = pendingIctStaffMemberRepository.findById(id).orElseThrow();
        Location location = new Location(Building.BY, "214");
        IctStaffMember ictStaffMember = new IctStaffMember(
                pendingMember.getEmail(), pendingMember.getName(), pendingMember.getPassword(), location,
                "ICT지원실", pendingMember.getMemberNumber(), ShiftType.valueOf(pendingMember.getShiftType()), Role.STAFF);

        mailService.sendEmail(pendingMember.getEmail(), "ICT지원실 직원 가입 승인",
                "가입이 승인되었습니다. 다음 링크를 통해 슬랙을 가입해주세요 https://join.slack.com/t/ict-8je8988/shared_invite/zt-2l8q0lbuc-gIyhjnaM6mmjnTsI35gpyg");

        ictStaffMemberService.joinIctmember(ictStaffMember);
        pendingIctStaffMemberRepository.delete(pendingMember);

        return "redirect:members/approveMembers";
    }

    @PostMapping("/reject-member/{id}")
    public String rejectMember(@PathVariable Long id) {
        pendingIctStaffMemberRepository.deleteById(id);
        return "redirect:members/approveMembers";
    }

    @GetMapping("/members-control")
    public String membersControl(Model model) {

        List<Member> memberList = memberService.getAllMembers();
        List<IctStaffMember> ictMemberList = ictStaffMemberService.findAll();
        AllMembersInfo allMembersInfo = new AllMembersInfo(memberList, ictMemberList);

        model.addAttribute("allMembersInfo", allMembersInfo);

        return "members/membersControl";
    }

}
