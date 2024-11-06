package ict.board.domain.schedule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "regular_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RegularSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false)
    private String dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private String className;

    @Column(nullable = false)
    private String classSection;

    @Column(nullable = false)
    private String professorName;

    public void changeClassroom(Classroom newClassroom) {
        Classroom oldClassroom = this.classroom;
        this.classroom = newClassroom;

        if (oldClassroom != null && oldClassroom.getRegularSchedules().contains(this)) {
            oldClassroom.getRegularSchedules().remove(this);
        }

        if (newClassroom != null && !newClassroom.getRegularSchedules().contains(this)) {
            newClassroom.getRegularSchedules().add(this);
        }
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}