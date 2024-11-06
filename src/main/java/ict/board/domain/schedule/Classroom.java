package ict.board.domain.schedule;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;

@Entity
@Table(name = "classroom")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Unique
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int capacity;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RegularSchedule> regularSchedules = new HashSet<>();

    @JsonBackReference
    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MakeupClass> makeupClasses = new HashSet<>();

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ClassroomSoftware> classroomSoftware = new HashSet<>();

    public void addRegularSchedule(RegularSchedule schedule) {
        regularSchedules.add(schedule);
        if (schedule.getClassroom() != this) {
            schedule.changeClassroom(this);
        }
    }

    public void removeRegularSchedule(RegularSchedule schedule) {
        regularSchedules.remove(schedule);
        if (schedule.getClassroom() == this) {
            schedule.changeClassroom(null);
        }
    }

    public void addMakeupClass(MakeupClass makeupClass) {
        makeupClasses = new HashSet<>(makeupClasses);
        makeupClasses.add(makeupClass);
        makeupClass.assignToClassroom(this);
    }

    public void addSoftware(Software software, LocalDate installationDate) {
        ClassroomSoftware classroomSoftware = new ClassroomSoftware(this, software, installationDate);
        this.classroomSoftware = new HashSet<>(this.classroomSoftware);
        this.classroomSoftware.add(classroomSoftware);
    }

    public Set<RegularSchedule> getRegularSchedules() {
        return regularSchedules;
    }

    public Set<MakeupClass> getMakeupClasses() {
        return Collections.unmodifiableSet(makeupClasses);
    }

    public Set<ClassroomSoftware> getClassroomSoftware() {
        return Collections.unmodifiableSet(classroomSoftware);
    }
}