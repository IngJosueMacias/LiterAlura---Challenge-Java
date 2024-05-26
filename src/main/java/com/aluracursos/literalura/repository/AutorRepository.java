package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {



    @Query("SELECT a.nombre FROM Autor a WHERE :fecha >= a.fechaNacimiento AND :fecha <= a.fechaFallecimiento")
    List<String> autoresPorEpoca(int fecha);
/*
    @Query("SELECT a.nombre FROM Autor a WHERE a.nombre LIKE %:nombre%")
    List<String> autorNombre(String nombre);

    Optional<Autor> findByNombreContainingIgnoreCase(String nombre);

 */
}
