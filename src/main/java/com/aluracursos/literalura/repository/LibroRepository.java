package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Idioma;
import com.aluracursos.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByIdioma(Idioma idioma);

    @Query("SELECT l.titulo, l.numDescargas FROM Libro l ORDER BY l.numDescargas DESC")
    List<Object[]> top10();
}
