package org.example.bibliotecaservlets.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.bibliotecaservlets.Modelo.DAOGenerico;
import org.example.bibliotecaservlets.Modelo.Ejemplar;
import org.example.bibliotecaservlets.Modelo.Libro;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "controladorEjemplar", value = "/controladorEjemplar")
public class ControladorEjemplar extends HttpServlet {

    DAOGenerico<Ejemplar, Integer> daoejemplar;
    DAOGenerico<Libro, String> daolibro;

    public void init() {
        daoejemplar = new DAOGenerico<>(Ejemplar.class, Integer.class);
        daolibro = new DAOGenerico<>(Libro.class, String.class);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String operacion = request.getParameter("operacion");
        Integer id = null;
        String idParam = request.getParameter("id");
        if(idParam != null && !idParam.isEmpty()){
            id = Integer.valueOf(idParam);
        }
        String isbn = request.getParameter("isbn");
        String estado = request.getParameter("estado");

        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();
        conversorJson.registerModule(new JavaTimeModule());
        String jsonResponse = null;

        switch (operacion) {
            case "Crear":
                Libro libroCrear = daolibro.getById(isbn);
                if(libroCrear != null) {
                    Ejemplar ejemplarCrear = new Ejemplar(libroCrear, estado);
                    daoejemplar.insert(ejemplarCrear);
                    jsonResponse = conversorJson.writeValueAsString(ejemplarCrear);
                } else {
                    jsonResponse = "{\"message\": \"Libro no encontrado\"}";
                }
                break;

            case "Buscar":
                Ejemplar ejemplarBuscar = daoejemplar.getById(id);
                if(ejemplarBuscar != null) {
                    jsonResponse = conversorJson.writeValueAsString(ejemplarBuscar);
                } else {
                    jsonResponse = "{\"message\": \"Ejemplar no encontrado\"}";
                }
                break;

            case "Modificar":
                Ejemplar ejemplarModificar = daoejemplar.getById(id);
                if (ejemplarModificar != null) {
                    ejemplarModificar.setEstado(estado);
                    daoejemplar.update(ejemplarModificar);
                    jsonResponse = conversorJson.writeValueAsString(ejemplarModificar);
                } else {
                    jsonResponse = "{\"message\": \"Ejemplar no encontrado\"}";
                }
                break;

            case "Eliminar":
                Ejemplar ejemplarEliminar = daoejemplar.getById(id);
                if (ejemplarEliminar != null) {
                    daoejemplar.delete(ejemplarEliminar);
                    jsonResponse = "{\"message\": \"Ejemplar eliminado\"}";
                } else {
                    jsonResponse = "{\"message\": \"Ejemplar no encontrado\"}";
                }
                break;

            case "Todos":
                List<Ejemplar> listaEjemplar = daoejemplar.getAll();
                jsonResponse = conversorJson.writeValueAsString(listaEjemplar);
                break;
        }

        impresora.println(jsonResponse);
    }

    public void destroy() {
    }
}
