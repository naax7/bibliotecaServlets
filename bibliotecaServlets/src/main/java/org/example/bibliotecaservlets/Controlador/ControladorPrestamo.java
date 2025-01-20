package org.example.bibliotecaservlets.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.bibliotecaservlets.Modelo.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@WebServlet(name = "controladorPrestamo", value = "/controladorPrestamo")
public class ControladorPrestamo extends HttpServlet {

    DAOGenerico<Ejemplar, Integer> daoejemplar;
    DAOGenerico<Usuario, Integer> daousuario;
    DAOGenerico<Prestamo, Integer> daoprestamo;

    public void init() {
        daoejemplar = new DAOGenerico<>(Ejemplar.class, Integer.class);
        daousuario = new DAOGenerico<>(Usuario.class, Integer.class);
        daoprestamo = new DAOGenerico<>(Prestamo.class, Integer.class);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String operacion = request.getParameter("operacion");
        Integer id = null;
        String idParam = request.getParameter("id");
        if(idParam != null && !idParam.isEmpty()){
            id = Integer.valueOf(idParam);
        }
        Integer usuarioId = Integer.parseInt(request.getParameter("usuarioId"));
        Integer ejemplaiId = Integer.parseInt(request.getParameter("ejemplaiId"));
        LocalDate fechaInicio = LocalDate.parse(request.getParameter("fechaInicio"));
        LocalDate fechaDevolucion = LocalDate.now();

        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();
        conversorJson.registerModule(new JavaTimeModule());
        String jsonResponse = null;

        switch (operacion) {
            case "Crear":
                Usuario usuario = daousuario.getById(usuarioId);
                Ejemplar ejemplar = daoejemplar.getById(ejemplaiId);
                if (usuario == null || ejemplar == null) {
                    jsonResponse = "{\"message\": \"Usuario o ejemplar no encontrado\"}";
                }
                long prestamosActivos = daoprestamo.findAllWhere("usuarioId", usuarioId)
                        .stream()
                        .filter(p -> p.getFechaDevolucion() == null)  // Filtramos los préstamos activos
                        .count();
                if (prestamosActivos >= 3) {
                    jsonResponse = "{\"message\": \"El usuario no puede tener más de 3 préstamos activos.\"}";
                }
                if (!"Disponible".equals(ejemplar.getEstado())) {
                    jsonResponse = "{\"message\": \"El ejemplar no está disponible para préstamo.\"}";
                }
                if (usuario.getPenalizacionHasta() != null && LocalDate.now().isBefore(usuario.getPenalizacionHasta())){
                    jsonResponse = "{\"message\": \"El usuario tiene una penalización activa.\"}";
                }
                Prestamo prestamo = new Prestamo();
                prestamo.setUsuario(usuario);
                prestamo.setEjemplar(ejemplar);
                prestamo.setFechaInicio(LocalDate.now());
                prestamo.setFechaDevolucion(null);
                daoprestamo.insert(prestamo);

                ejemplar.setEstado("Prestado");
                daoejemplar.update(ejemplar);
                break;

            case "Buscar":
                prestamo = daoprestamo.getById(id);
                if (prestamo == null) {
                    jsonResponse = "{\"message\": \"Préstamo no encontrado\"}";
                }
                ejemplar = prestamo.getEjemplar();
                ejemplar.setEstado("Disponible");
                prestamo.setFechaDevolucion(fechaDevolucion);
                usuario = prestamo.getUsuario();
                if (fechaDevolucion.isAfter(prestamo.getFechaDevolucion())) {
                    long diasRetraso = ChronoUnit.DAYS.between(prestamo.getFechaDevolucion(), fechaDevolucion);
                    new ControladorUsuario().penalizarUsuario(usuario.getId(), (int) diasRetraso);
                    jsonResponse = "{\"message\": \"Devolución fuera de plazo. Penalización aplicada.\"}";
                }
                daousuario.update(usuario);
                daoprestamo.update(prestamo);
                break;

            case "Todos":
                List<Prestamo> listaPrestamo = daoprestamo.getAll();
                jsonResponse = conversorJson.writeValueAsString(listaPrestamo);
                break;
        }

        impresora.println(jsonResponse);
    }

    public void destroy() {
    }
}
