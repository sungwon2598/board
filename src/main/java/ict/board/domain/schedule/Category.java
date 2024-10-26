package ict.board.domain.schedule;

import jakarta.persistence.*;
import java.util.Collections;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category")
    @Builder.Default
    private Set<Software> software = new HashSet<>();

    public Category(String name) {
        this.name = name;
    }

    public void addSoftware(Software software) {
        this.software = new HashSet<>(this.software);
        this.software.add(software);
    }

    public void removeSoftware(Software software) {
        this.software = new HashSet<>(this.software);
        this.software.remove(software);
    }

    public Set<Software> getSoftware() {
        return Collections.unmodifiableSet(software);
    }

    public Category update(String name) {
        return Category.builder()
                .id(this.id)
                .name(name)
                .software(new HashSet<>(this.software))
                .build();
    }
}