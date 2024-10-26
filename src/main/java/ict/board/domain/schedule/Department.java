package ict.board.domain.schedule;

import ict.board.domain.member.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "department")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RegularSchedule> regularSchedules = new HashSet<>();

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MakeupClass> makeupClasses = new HashSet<>();

//    public void addRegularSchedule(RegularSchedule schedule) {
//        regularSchedules = new HashSet<>(regularSchedules);
//        regularSchedules.add(schedule);
//        //schedule.setDepartment(this);
//    }

//    public void addMakeupClass(MakeupClass makeupClass) {
//        makeupClasses = new HashSet<>(makeupClasses);
//        makeupClasses.add(makeupClass);
//        //makeupClass.assignToDepartment(this);
//    }

    public Set<RegularSchedule> getRegularSchedules() {
        return Collections.unmodifiableSet(regularSchedules);
    }

    public Set<MakeupClass> getMakeupClasses() {
        return Collections.unmodifiableSet(makeupClasses);
    }
}