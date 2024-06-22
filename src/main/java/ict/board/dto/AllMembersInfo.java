package ict.board.dto;

import ict.board.domain.member.IctStaffMember;
import ict.board.domain.member.Member;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AllMembersInfo {

    private final List<Member> members;
    private final List<IctStaffMember> ictMembers;

}