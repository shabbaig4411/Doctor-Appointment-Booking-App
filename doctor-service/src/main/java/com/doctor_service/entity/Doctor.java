package com.doctor_service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    @Column(name = "doctor_id", nullable = false, unique = true)
    private String doctorId;

    @Column(name = "imageUrl", length = 2000)
    private String imageUrl;

    @Column(name = "specialization", nullable = false)
    private String specialization;

    @Column(name = "qualification", nullable = false)
    private String qualification;

    @Column(name = "experience", nullable = false)
    private String experience;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "city_id")
    private City city;

    @Column(name = "address", nullable = false, length = 1000)
    private String address;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "area_id")
    private Area area;

}