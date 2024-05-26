package com.aluracursos.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibro(
        @JsonAlias("id") Long id,
        @JsonAlias("title") String titulo,
        @JsonAlias("download_count") int numdescargas,
        @JsonAlias("languages") Idioma[] idiomas,
        @JsonAlias("authors") List<DatosAutor> autores) {

    public Idioma[] idiomas() {
        return idiomas;
    }

    @Override
    public String toString() {
        return "***** DatosLIBRO *****"+
                "\nTítulo: "+this.titulo+
                "\nAutor: "+this.autores+
                "\nIdioma: "+this.idiomas+
                "\nNúmero de descargas: "+this.numdescargas+
                "\n****************\n";
    }





}
