package org.example.admincliente.controllers;

import org.example.admincliente.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("usuarioEmail", authentication.getName());
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("admins", usuarioService.listarTodosAdmins());
        model.addAttribute("clientes", usuarioService.listarTodosClientes());
        return "admin/dashboard";
    }

    @GetMapping("/cliente/perfil")
    public String clientePerfil(Model model, Authentication authentication) {
        model.addAttribute("usuario", usuarioService.buscarPorEmail(authentication.getName()).orElseThrow());
        return "cliente/perfil";
    }
} 