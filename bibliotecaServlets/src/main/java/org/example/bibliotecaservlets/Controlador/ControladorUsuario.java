package org.example.bibliotecaservlets.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.bibliotecaservlets.Modelo.DAOGenerico;
import org.example.bibliotecaservlets.Modelo.Usuario;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "controladorUsuario", value = "/controladorUsuario")
public class ControladorUsuario extends HttpServlet {
    DAOGenerico<Usuario, Integer> daousuario;

    public void init() {
        daousuario = new DAOGenerico<>(Usuario.class, Integer.class);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String operacion = request.getParameter("operacion");

        Integer id = null;
        String idParam = request.getParameter("id");
        if(idParam != null && !idParam.isEmpty()){
            id = Integer.valueOf(idParam);
        }
        String dni = request.getParameter("dni");
        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String tipo = request.getParameter("tipo");

        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();
        conversorJson.registerModule(new JavaTimeModule());
        String jsonResponse = null;

        switch (operacion) {
            case "Crear":
                Usuario usuarioCrear = new Usuario(dni, nombre, email, password, tipo);
                daousuario.insert(usuarioCrear);
                jsonResponse = conversorJson.writeValueAsString(usuarioCrear);
                break;

            case "Buscar":
                Usuario usuarioBuscar = daousuario.getById(id);
                if (usuarioBuscar != null) {
                    jsonResponse = conversorJson.writeValueAsString(usuarioBuscar);
                } else {
                    jsonResponse = "{\"message\": \"Usuario no encontrado\"}";
                }
                break;

            case "Modificar":
                Usuario usuarioModificar = daousuario.getById(id);
                if (usuarioModificar != null) {
                    usuarioModificar.setDni(dni);
                    usuarioModificar.setNombre(nombre);
                    usuarioModificar.setEmail(email);
                    usuarioModificar.setPassword(password);
                    usuarioModificar.setTipo(tipo);
                    daousuario.update(usuarioModificar);
                    jsonResponse = conversorJson.writeValueAsString(usuarioModificar);
                } else {
                    jsonResponse = "{\"message\": \"Usuario no encontrado\"}";
                }
                break;

            case "Eliminar":
                Usuario usuarioEliminar = daousuario.getById(id);
                if (usuarioEliminar != null) {
                    daousuario.delete(usuarioEliminar);
                    jsonResponse = "{\"message\": \"Usuario eliminado\"}";
                } else {
                    jsonResponse = "{\"message\": \"usuario no encontrado\"}";
                }
                break;

            case "Todos":
                List<Usuario> listaUsuarios = daousuario.getAll();
                jsonResponse = conversorJson.writeValueAsString(listaUsuarios);
                break;
        }

        impresora.println(jsonResponse);
    }

    public void penalizarUsuario(int id, int dias) {
        Usuario usuario = daousuario.getById(id);
        if (usuario != null) {
            usuario.setPenalizacionHasta(LocalDate.now().plusDays(dias));
            daousuario.update(usuario);
        } else {
            System.out.println("Usuario no encontrado.");
        }
    }

    public void destroy() {
    }
}
