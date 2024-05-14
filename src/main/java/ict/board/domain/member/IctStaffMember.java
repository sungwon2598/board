package ict.board.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrimaryKeyJoinColumn;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name = "member_id") // 상위 클래스의 기본 키를 하위 클래스의 기본 키와 조인 컬럼으로 사용
public class IctStaffMember extends Member {

    @ElementCollection(fetch = FetchType.LAZY)
    private List<LocalDate> usedLeaveDates = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type")
    private ShiftType shiftType; // 주간 / 야간조 분류

    @Column(name = "overtime_vacation_hours")
    private Integer overtimeVacationHours; // 잔여 초과근무 휴가(단위: 시간)

    @Column(name = "remaining_leave_days")
    private Integer remainingLeaveDays; // 잔여 월차(단위: 일)

    @Column(name = "network_construction_hours")
    private Integer networkConstructionHours; // 네트워크 시공 작업 누적시간

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    public IctStaffMember() {
        super();
    }

    public IctStaffMember(String email, String name, String password, Location location, String team,
                          String memberNumber,
                          ShiftType shiftType, Role role, Integer overtimeVacationHours,
                          Integer remainingLeaveDays, Integer networkConstructionHours) {
        super(email, name, password, location, team, memberNumber);
        this.role = role;
        this.shiftType = shiftType;
        this.overtimeVacationHours = 0;
        this.remainingLeaveDays = 0;
        this.networkConstructionHours = 0;
    }

}