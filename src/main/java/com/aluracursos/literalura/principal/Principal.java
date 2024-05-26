package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.Autor;
import com.aluracursos.literalura.model.DatosLibro;
import com.aluracursos.literalura.model.DatosAutor;
import com.aluracursos.literalura.model.Idioma;
import com.aluracursos.literalura.model.Libro;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static java.lang.System.exit;

public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosLibro> datosLibros = new ArrayList<>();
    private LibroRepository repositorioLibro;
    private AutorRepository repositorioAutor;
    private List<Libro> libros;
    private Optional<Libro> libroBuscado;

    String nombreAutor = null;

    //@Autowired
    public Principal(LibroRepository repositorioLibro, AutorRepository repositorioAutor) {
        this.repositorioLibro = repositorioLibro;
        this.repositorioAutor = repositorioAutor;
    }

    public void menu() {
        Scanner teclado = new Scanner(System.in);

        int opcion=-1;

        do {
            System.out.println("""
                ********************************
                Elija la opcion del menu deseada:
                1.- Buscar libro por titulo.
                2.- Listar libros registrados.
                3.- Listar autores registrados.
                4.- Listar autores vivos en un determinado año.
                5.- Listar libros por idioma.
                0.- Salir.
                ********************************
                """);
            try{
                opcion = Integer.parseInt(teclado.nextLine());
            }
            catch (NumberFormatException e){

            }

            switch (opcion) {
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    verLibros();
                    break;
                case 3:
                    verAutores();
                    break;
                case 4:
                    buscarAutorFecha();
                    break;
                case 5:
                    verLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("El sistema se cerrara.");
                    break;
                default:
                    System.out.println("Opción no válida");
                    break;
            }
        }
        while (opcion != 0);
    }

    private void buscarLibro() {
        DatosLibro datos, libroSeleccion;
        DatosAutor autorSeleccion;
        Libro libro;
        List<Autor> autorList = new ArrayList<>();
        int count = 0;
        System.out.println("Escribe el nombre del libro que deseas buscar");
        String nombreLibro = teclado.nextLine();
        String json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ","%20"));

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            //obtenemos cuantos libros arrojo la busqueda
            count = Integer.parseInt(rootNode.get("count").toString());

            if(count>0){
                if (count>32){
                    count = 32;
                }
                System.out.println("Se encontraron "+count+" libros que conciden con tu busqueda.");
                /*si encontra libros, imprime los datos de todos*/
                for (int i = 0; i < count; i++) {
                    String json2 = rootNode.get("results").get(i).toString();
                    datos = conversor.obtenerDatos(json2, DatosLibro.class);
                    System.out.println((i+1)+"\n"+datos.toString());
                }
                /*elegimos cual de los libros encontrados vamos a registrar*/
                System.out.println("De los anteriores resultados, elige el numero de libro que deseas registrar.");
                int eleccion = teclado.nextInt();
                /*obtenemos el libro seleccionado*/
                String jsonLibroElegido = rootNode.get("results").get(eleccion-1).toString();
                libroSeleccion = conversor.obtenerDatos(jsonLibroElegido, DatosLibro.class);
                /*imprimimos los datos al usuario*/
                System.out.println("El libro seleccionado es:\n"+libroSeleccion.toString());
                Optional<Libro> libroOptional=repositorioLibro.findById(libroSeleccion.id());
                if (libroOptional.isPresent()){
                    System.out.println("El libro ya esta registrado");
                } else {
                    /*Guardamos en una lista los datosAutore*/
                    List<DatosAutor> datosAutorList = libroSeleccion.autores();
                    /*Recorremos la lista y guardamos en la base de datos cada autor*/
                    for (int i = 0; i < datosAutorList.size(); i++) {
                        Autor autor = new Autor(datosAutorList.get(i));
                        System.out.println(autor);
                        repositorioAutor.save(autor);
                        autorList.add(autor);
                    }
                    /*creamos el objeto libro apartir de datosLibro*/
                    libro = new Libro(libroSeleccion);
                    /*agregamos al libro sus autores*/
                    libro.setAutores(autorList);
                    /*guardamos el libro en la base de datos*/
                    repositorioLibro.save(libro);
                }
            } else {
                System.out.println("Libro no encontrado.");
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void top10() {
        System.out.println("El top 10 son los siguientes");

        List<Object[]> top10 = repositorioLibro.top10();
        int rank = 1;

        for (Object[] result : top10) {
            String titulo = (String) result[0];
            int numDescargas = (int) result[1];
            System.out.println(rank + ". Título: " + titulo + ", Número de Descargas: " + numDescargas);
            rank++;
        }
    }

    private void buscarAutorFecha() {
        System.out.println("Escribe un año: ");
        int fecha = teclado.nextInt();

        List<String> autoresVivos = repositorioAutor.autoresPorEpoca(fecha);
        if (!autoresVivos.isEmpty()) {
            System.out.println("Los autores vivos en el año "+fecha+" son:");
            autoresVivos.forEach(nombre -> System.out.printf("Nombre: %s\n", nombre));
        } else {
            System.out.println("No hay autores vivos en esa fecha");
        }
    }

    private void verLibrosPorIdioma() {
        Idioma idioma = null;
        int opcion;

        do {
            System.out.println("Elige el idioma:");
            System.out.println("1. Español");
            System.out.println("2. Inglés");
            System.out.println("3. Francés");
            System.out.println("4. Italiano");
            System.out.println("5. Alemán");
            System.out.println("6. Portugués");

            try {
                opcion = teclado.nextInt();
                switch (opcion) {
                    case 1:
                        idioma = Idioma.es;
                        break;
                    case 2:
                        idioma = Idioma.en;
                        break;
                    case 3:
                        idioma = Idioma.fr;
                        break;
                    case 4:
                        idioma = Idioma.it;
                        break;
                    case 5:
                        idioma = Idioma.de;
                        break;
                    case 6:
                        idioma = Idioma.pt;
                        break;
                    default:
                        System.out.println("Opción no válida");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, introduce un número válido.");
                teclado.nextLine();
                opcion = 0;
            }
        } while (opcion < 1 || opcion > 6);

        if (idioma != null) {
            List<Libro> libros = repositorioLibro.findByIdioma(idioma);
            if (libros.isEmpty()) {
                System.out.println("No hay libros en la base de datos con ese idioma.");
            } else {
                for (int i = 0; i < libros.size(); i++) {
                    System.out.println("***** LIBRO "+(i+1)+" *****"+
                            "\nTítulo: "+libros.get(i).getTitulo()+
                            "\nIdioma: "+libros.get(i).getIdioma()+
                            "\nNúmero de descargas: "+libros.get(i).getNumDescargas()+
                            "\n****************\n");
                }
            }
        }
    }

    private void verAutores() {
        System.out.println("Los autores encontrados en la base de datos son los siguientes:");
        List<Autor> autores = repositorioAutor.findAll();
        for (int i = 0; i < autores.size(); i++) {
            System.out.println("***** Autor "+(i+1)+" *****"+
                    "\nNombre: "+autores.get(i).getNombre()+
                    "\nNacimiento: "+autores.get(i).getFechaNacimiento()+
                    "\nFallecimiento: "+autores.get(i).getFechaFallecimiento()+
                    "\n****************\n");
        }
    }

    private void verLibros() {
        System.out.println("Los libros encontrados en la base de datos son los siguientes:");
        List<Libro> libros = repositorioLibro.findAll();
        for (int i = 0; i < libros.size(); i++) {
            System.out.println("***** LIBRO "+(i+1)+" *****"+
                    "\nTítulo: "+libros.get(i).getTitulo()+
                    "\nIdioma: "+libros.get(i).getIdioma()+
                    "\nNúmero de descargas: "+libros.get(i).getNumDescargas()+
                    "\n****************\n");
        }
    }
}