package com.aluracursos.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosAutor(
        @JsonAlias("name") String name,
        @JsonAlias("birth_year") int birthYear,
        @JsonAlias("death_year") int deathYear
) {
    @Override
    public String toString() {
        return
                "Nombre: "+this.name+
                " ,Nacimiento: "+this.birthYear+
                " ,Fallecimiento: "+this.deathYear+

                "\n";
    }
}