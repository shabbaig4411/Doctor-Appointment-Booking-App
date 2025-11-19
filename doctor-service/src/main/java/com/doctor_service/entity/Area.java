package com.doctor_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "area")
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false,unique = true)
    private String name;

    // ✅ Orphan removal ensures doctor rows are deleted if area is removed
//    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnoreProperties({"area"})
//    private Set<Doctor> doctors = new LinkedHashSet<>();


}