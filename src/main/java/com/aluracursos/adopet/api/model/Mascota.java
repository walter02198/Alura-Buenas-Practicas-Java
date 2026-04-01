package com.aluracursos.adopet.api.model;

import com.aluracursos.adopet.api.dto.RegistroMascotaDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(name = "mascotas")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)

    @Column(name = "tipo")
    private TipoMascota tipo;


    @Column(name = "nombre")
    private String nombre;


    @Column(name = "raza")
    private String raza;


    @Column(name = "edad")
    private Integer edad;

    @NotBlank
    @Column(name = "color")
    private String color;

    @NotNull
    @Column(name = "peso")
    private Float peso;

    @Column(name = "adoptada")
    private Boolean adoptada;

    @ManyToOne
    @JsonBackReference("refugio_mascotas")
    @JoinColumn(name = "refugio_id")
    private Refugio refugio;

    @OneToOne(mappedBy = "mascota")
    @JsonBackReference("adopcion_mascotas")
    private Adopcion adopcion;

    public Mascota() {
    }

    public Mascota(RegistroMascotaDto dto, Refugio refugio) {
        this.tipo = dto.tipo();
        this.nombre = dto.nombre();
        this.raza = dto.raza();
        this.edad = dto.edad();
        this.color = dto.color();
        this.peso = dto.peso();
        this.refugio = refugio;
        this.adoptada = false;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mascota mascota = (Mascota) o;
        return Objects.equals(id, mascota.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public TipoMascota getTipo() {
        return tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRaza() {
        return raza;
    }

    public Integer getEdad() {
        return edad;
    }

    public String getColor() {
        return color;
    }

    public Float getPeso() {
        return peso;
    }

    public Boolean getAdoptada() {
        return adoptada;
    }

    public Refugio getRefugio() {
        return refugio;
    }

    public Adopcion getAdopcion() {
        return adopcion;
    }

}
