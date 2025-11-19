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
@Table(name = "state")
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false,unique = true)
    private String name;

    // ✅ Cascade on child, not parent
//    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnoreProperties({"state"})
//    private Set<City> cities = new LinkedHashSet<>();
//
//    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnoreProperties({"state"})
//    private Set<Doctor> doctors = new LinkedHashSet<>();


}