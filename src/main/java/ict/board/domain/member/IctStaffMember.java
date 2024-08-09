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

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name = "member_id")
public class IctStaffMember extends Member {

    @ElementCollection(fetch = FetchType.LAZY)
    private final List<LocalDate> usedLeaveDates = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type")
    private ShiftType shiftType;

    @Column(name = "overtime_vacation_hours")
    private Integer overtimeVacationHours;

    @Column(name = "remaining_leave_days")
    private Integer remainingLeaveDays;

    @Column(name = "network_construction_hours")
    private Integer networkConstructionHours;

    public IctStaffMember() {
        super();
    }

    public IctStaffMember(String email, String name, String password, Location location, String team,
                          String memberNumber, ShiftType shiftType, Role role) {
        super(email, name, password, location, team, memberNumber);
        this.shiftType = shiftType;
        this.overtimeVacationHours = 0;
        this.remainingLeaveDays = 0;
        this.networkConstructionHours = 0;
        setRole(role);
    }

    public void setRole(Role role) {
        super.role = role;
    }
}