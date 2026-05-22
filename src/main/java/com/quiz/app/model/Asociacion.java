package com.quiz.app.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "asociaciones")
public class Asociacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "pais", length = 50)
    private String pais;
    
    @Column(name = "presidente", length = 100)
    private String presidente;
    
    @OneToMany(mappedBy = "asociacion", fetch = FetchType.LAZY)
    private List<Club> clubes;
    
    public Asociacion() {}
    
    public Asociacion(String nombre, String pais, String presidente) {
        this.nombre = nombre;
        this.pais = pais;
        this.presidente = presidente;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public String getPresidente() { return presidente; }
    public void setPresidente(String presidente) { this.presidente = presidente; }
    public List<Club> getClubes() { return clubes; }
    public void setClubes(List<Club> clubes) { this.clubes = clubes; }
}