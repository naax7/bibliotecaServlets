package org.example.bibliotecaservlets.Modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "ejemplar")
public class Ejemplar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "isbn", nullable = false)
    @JsonBackReference
    private org.example.bibliotecaservlets.Modelo.Libro isbn;

    @ColumnDefault("'Disponible'")
    @Lob
    @Column(name = "estado")
    private String estado;

    @OneToMany(mappedBy = "ejemplar")
    @JsonManagedReference
    private Set<org.example.bibliotecaservlets.Modelo.Prestamo> prestamos = new LinkedHashSet<>();

    public Ejemplar(Integer id, Libro isbn, String estado) {
        this.id = id;
        this.isbn = isbn;
        this.estado = estado;
    }

    public Ejemplar(Libro isbn, String estado) {
        this.isbn = isbn;
        this.estado = estado;
    }

    public Ejemplar() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public org.example.bibliotecaservlets.Modelo.Libro getIsbn() {
        return isbn;
    }

    public void setIsbn(org.example.bibliotecaservlets.Modelo.Libro isbn) {
        this.isbn = isbn;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Set<org.example.bibliotecaservlets.Modelo.Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(Set<org.example.bibliotecaservlets.Modelo.Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

    @Override
    public String toString() {
        return "Ejemplar{" +
                "id=" + id +
                ", isbn=" + isbn +
                ", estado='" + estado + '\'' +
                '}';
    }
}