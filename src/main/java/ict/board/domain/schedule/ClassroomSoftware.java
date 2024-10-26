package ict.board.domain.schedule;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "classroom_software")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassroomSoftware {
    @EmbeddedId
    private ClassroomSoftwareId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("classroomId")
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("softwareId")
    @JoinColumn(name = "software_id")
    private Software software;

    @Column(nullable = false)
    private LocalDate installationDate;

    public ClassroomSoftware(Classroom classroom, Software software, LocalDate installationDate) {
        this.id = new ClassroomSoftwareId(classroom.getId(), software.getId());
        this.classroom = classroom;
        this.software = software;
        this.installationDate = installationDate;
    }
}
