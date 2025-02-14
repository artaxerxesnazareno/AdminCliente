package org.example.admincliente.controllers;

import jakarta.validation.Valid;
import org.example.admincliente.dtos.LoginDTO;
import org.example.admincliente.dtos.TokenDTO;
import org.example.admincliente.dtos.UsuarioRegistroDTO;
import org.example.admincliente.dtos.UsuarioDTO;
import org.example.admincliente.services.AuthService;
import org.example.admincliente.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDTO loginDTO, 
                       BindingResult result,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "login";
        }

        try {
            TokenDTO token = authService.autenticar(loginDTO);
            // Aqui você pode adicionar o token na sessão se necessário
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Email ou senha inválidos");
            return "redirect:/login";
        }
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("registroDTO", new UsuarioRegistroDTO());
        return "inscricao";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute UsuarioRegistroDTO registroDTO,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "inscricao";
        }

        try {
            usuarioService.registrarCliente(registroDTO);
            redirectAttributes.addFlashAttribute("mensagem", "Registro realizado com sucesso!");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro";
        }
    }

    @PostMapping("/logout")
    public String logout() {
        // Implementar lógica de logout se necessário
        return "redirect:/login?logout";
    }
} 