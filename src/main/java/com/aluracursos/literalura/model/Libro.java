package com.aluracursos.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @Column(name = "id_libro")
    private Long id;

    @Column(name = "nombre")
    private String titulo;

    @Column(unique = false)
    private int numDescargas;

    @Enumerated(EnumType.STRING)
    private Idioma idioma;

    @ManyToMany
    @JoinTable(
            name = "rel_libros_autores",
            joinColumns = @JoinColumn(name = "id_libro", nullable = false),
            inverseJoinColumns = @JoinColumn(name="id_autor", nullable = false)
    )
    private List<Autor> autores;

    public Libro(){}

    public Libro(DatosLibro datosLibros) {
        this.id = datosLibros.id();
        this.titulo = datosLibros.titulo();
        this.numDescargas = datosLibros.numdescargas();
        this.idioma = Idioma.fromString(datosLibros.idiomas()[0].toString());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getNumDescargas() {
        return numDescargas;
    }

    public void setNumDescargas(int numDescargas) {
        this.numDescargas = numDescargas;
    }

    public Idioma getIdioma() {
        return idioma;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    @Override
    public String toString() {
        return "***** LIBRO *****"+
                "\nTítulo: "+this.titulo+
                "\nAutor: "+this.autores+
                "\nIdioma: "+this.idioma+
                "\nNúmero de descargas: "+this.numDescargas+
                "\n****************\n";
    }


}