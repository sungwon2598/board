package ict.board.controller;

import ict.board.domain.member.Building;
import ict.board.domain.member.IctStaffMember;
import ict.board.domain.member.Location;
import ict.board.domain.member.PendingIctStaffMember;
import ict.board.domain.member.Role;
import ict.board.domain.member.ShiftType;
import ict.board.dto.IctStaffMemberForm;
import ict.board.repository.IctStaffMemberRepository;
import ict.board.repository.PendingIctStaffMemberRepository;
import ict.board.service.IctStaffMemberService;
import ict.board.service.MailService;
import ict.board.service.MemberService;
import ict.board.util.CombinedRandomStringGenerator;
import ict.board.util.cache.VerificationCodeCache;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class IctStaffMemberController {

    private final IctStaffMemberService ictStaffMemberService;
    private final MemberService memberService;
    private final MailService mailService;
    private final VerificationCodeCache verificationCodeCache;
    private final IctStaffMemberRepository ictStaffMemberRepository;
    private final CombinedRandomStringGenerator generator;
    private final PendingIctStaffMemberRepository pendingIctStaffMemberRepository;

//    @GetMapping("/staff-join/7345")
//    public String staffJoin(Model model) {
//        model.addAttribute("memberForm", new IctStaffMemberForm());
//        return "members/ictmemberForm";
//    }
//
//    @PostMapping("/staff-join/7345")
//    public String staffJoinPost(@Valid @ModelAttribute("memberForm") IctStaffMemberForm form, BindingResult result,
//                                Model model, HttpSession session) {
//        if (result.hasErrors()) {
//            model.addAttribute("memberForm", form);
//            model.addAttribute("emailSent",
//                    session.getAttribute("emailSent") != null && (Boolean) session.getAttribute("emailSent"));
//            return "members/ictmemberForm";
//        }
//
//        String sessionEmail = (String) session.getAttribute("userEmail");
//        if (form.getVerificationCode() != null) {
//            boolean validCode = verificationCodeCache.isValidCode(sessionEmail, form.getVerificationCode());
//            if (!validCode) {
//                model.addAttribute("memberForm", form);
//                model.addAttribute("codeError", "인증번호가 만료되었습니다.");
//                model.addAttribute("emailSent", true);
//                return "members/ictmemberForm";
//            }
//
//            String hashedPassword = BCrypt.hashpw(form.getPassword(), BCrypt.gensalt());
//            Location location = new Location(Building.BY, "214");
//            IctStaffMember ictStaffMember = new IctStaffMember(sessionEmail, form.getName(), hashedPassword, location,
//                    "ICT지원실", form.getMemberNumber(), ShiftType.valueOf(form.getDayShift()), Role.STAFF);
//
//            ictStaffMemberService.joinIctmember(ictStaffMember);
//
//            return "redirect:/";
//        }
//
//        return "redirect:/";
//    }

    @GetMapping("/staff-join/7345")
    public String staffJoin(Model model) {
        model.addAttribute("memberForm", new IctStaffMemberForm());
        return "members/ictmemberForm";
    }

    @PostMapping("/staff-join/7345")
    public String staffJoinPost(@Valid @ModelAttribute("memberForm") IctStaffMemberForm form, BindingResult result,
                                Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("memberForm", form);
            model.addAttribute("emailSent", session.getAttribute("emailSent") != null && (Boolean) session.getAttribute("emailSent"));
            return "members/ictmemberForm";
        }

        String sessionEmail = (String) session.getAttribute("userEmail");
        if (form.getVerificationCode() != null) {
            boolean validCode = verificationCodeCache.isValidCode(sessionEmail, form.getVerificationCode());
            if (!validCode) {
                model.addAttribute("memberForm", form);
                model.addAttribute("codeError", "인증번호가 만료되었습니다.");
                model.addAttribute("emailSent", true);
                return "members/ictmemberForm";
            }

            String hashedPassword = BCrypt.hashpw(form.getPassword(), BCrypt.gensalt());
            Location location = new Location(Building.BY, "214");
            PendingIctStaffMember pendingMember = new PendingIctStaffMember(
                    null, sessionEmail, form.getName(), hashedPassword, location.toString(), "ICT지원실",
                    form.getMemberNumber(), form.getDayShift(), Role.STAFF.toString());

            pendingIctStaffMemberRepository.save(pendingMember);

            return "redirect:/";
        }

        return "redirect:/";
    }

    @GetMapping("/admin/approve-members")
    public String approveMembers(Model model) {
        model.addAttribute("pendingMembers", pendingIctStaffMemberRepository.findAll());
        return "members/approveMembers";
    }

    @PostMapping("/admin/approve-member/{id}")
    public String approveMember(@PathVariable Long id) {
        PendingIctStaffMember pendingMember = pendingIctStaffMemberRepository.findById(id).orElseThrow();
        Location location = new Location(Building.BY, "214");
        IctStaffMember ictStaffMember = new IctStaffMember(
                pendingMember.getEmail(), pendingMember.getName(), pendingMember.getPassword(), location,
                "ICT지원실", pendingMember.getMemberNumber(), ShiftType.valueOf(pendingMember.getShiftType()), Role.STAFF);

        ictStaffMemberService.joinIctmember(ictStaffMember);
        pendingIctStaffMemberRepository.delete(pendingMember);

        return "redirect:/members/approveMembers";
    }

    @PostMapping("/admin/reject-member/{id}")
    public String rejectMember(@PathVariable Long id) {
        pendingIctStaffMemberRepository.deleteById(id);
        return "redirect:/members/approveMembers";
    }

}
