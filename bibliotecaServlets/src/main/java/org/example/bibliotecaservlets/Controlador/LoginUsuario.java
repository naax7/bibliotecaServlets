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

@WebServlet(name = "loginUsuario", value = "/loginUsuario")
public class LoginUsuario extends HttpServlet {
    DAOGenerico<Usuario, Integer> daousuario;

    public void init() {
        daousuario = new DAOGenerico<>(Usuario.class, Integer.class);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();
        conversorJson.registerModule(new JavaTimeModule());
        String jsonResponse = null;

        Usuario usuario = null;
        try {
            usuario = daousuario.findUnique("email", email);
        } catch (jakarta.persistence.NoResultException e){
            impresora.println("No existe el usuario");
        }
        if (usuario != null) {
            if (usuario.getPassword().equals(password)) {
                impresora.println("El usuario y contraseña son correctos");
            } else {
                impresora.println("La contraseña es incorrecta");
            }
        }
    }
    /*public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();
        conversorJson.registerModule(new JavaTimeModule());
        String jsonResponse = null;

        Usuario usuario = null;
        try {
            usuario = daousuario.findUnique("email", email);
        } catch (jakarta.persistence.NoResultException e) {
            impresora.println("{\"error\":\"No existe el usuario\"}");
            return;
        }

        if (usuario != null) {
            if (usuario.getPassword().equals(password)) {
                impresora.println("{\"message\":\"El usuario y contraseña son correctos\"}");
                return;
            } else {
                impresora.println("{\"error\":\"La contraseña es incorrecta\"}");
            }
        }
    }*/

    public void destroy() {
    }
}