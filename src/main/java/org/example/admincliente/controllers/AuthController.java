package org.example.admincliente.controllers;

import org.example.admincliente.dtos.LoginDTO;
import org.example.admincliente.dtos.UsuarioRegistroDTO;
import org.example.admincliente.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

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
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getSenha())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/cazio/utilizadores/perfil-do-utilizador";
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
            // Registra o usuário
            usuarioService.registrarCliente(registroDTO);
            
            // Faz o login automático
            try {
                Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(registroDTO.getEmail(), registroDTO.getSenha())
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // Redireciona diretamente para o perfil após o login bem-sucedido
                return "redirect:/cazio/utilizadores/perfil-do-utilizador";
            } catch (Exception e) {
                // Se o login automático falhar, redireciona para a página de login
                redirectAttributes.addFlashAttribute("message", "Registro realizado com sucesso. Por favor, faça login.");
                return "redirect:/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro";
        }
    }

    @PostMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout";
    }
} 