package ict.board.domain.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

@Entity
@Table(name = "software")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Software {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    @OneToMany(mappedBy = "software", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private Set<ClassroomSoftware> classroomSoftware = new HashSet<>();

    public void addToClassroom(Classroom classroom, LocalDate installationDate) {
        ClassroomSoftware classroomSoftware = new ClassroomSoftware(classroom, this, installationDate);
        this.classroomSoftware = new HashSet<>(this.classroomSoftware);
        this.classroomSoftware.add(classroomSoftware);
        classroom.addSoftware(this, installationDate);
    }

    public Set<ClassroomSoftware> getClassroomSoftware() {
        return Collections.unmodifiableSet(classroomSoftware);
    }

    public void updateCategory(Category newCategory) {
        if (this.category != null) {
            this.category.removeSoftware(this);
        }
        this.category = newCategory;
        if (newCategory != null) {
            newCategory.addSoftware(this);
        }
    }

    public Software update(String name, String version, Category category) {
        Software updatedSoftware = Software.builder()
                .id(this.id)
                .name(name)
                .version(version)
                .category(category)
                .classroomSoftware(new HashSet<>(this.classroomSoftware))
                .build();

        updatedSoftware.updateCategory(category);
        return updatedSoftware;
    }

    public String getCategoryName() {
        return category != null ? category.getName() : null;
    }
}