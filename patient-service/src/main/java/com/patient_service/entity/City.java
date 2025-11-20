package com.patient_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false,unique = true)
    private String name;

    // ✅ Correct cascade — affects only child (Doctor)
//    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnoreProperties({"city", "state"})
//    private Set<Doctor> doctors = new LinkedHashSet<>();
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "state_id", nullable = false)
//    @JsonIgnoreProperties({"cities", "doctors"})
//    private State state;

}