package org.example.admincliente.controllers;

import org.example.admincliente.dtos.LoginDTO;
import org.example.admincliente.dtos.UsuarioDTO;
import org.example.admincliente.dtos.UsuarioRegistroDTO;
import org.example.admincliente.enums.TipoUsuario;
import org.example.admincliente.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        logger.info("Acessando página de login");
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDTO loginDTO, 
                       BindingResult result,
                       RedirectAttributes redirectAttributes) {
        logger.info("Tentativa de login para o email: {}", loginDTO.getEmail());
        
        if (result.hasErrors()) {
            logger.error("Erros de validação no formulário: {}", result.getAllErrors());
            return "login";
        }

        try {
            logger.info("Iniciando autenticação com AuthenticationManager");
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getSenha())
            );
            logger.info("Autenticação bem sucedida para o usuário: {}", authentication.getName());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("SecurityContext atualizado com a autenticação");

            // Busca o usuário para verificar o tipo
            UsuarioDTO usuario = usuarioService.buscarPorEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Log para debug
            logger.info("Tipo do usuário: {}", usuario.getTipo());
            logger.info("Autoridades do usuário: {}", authentication.getAuthorities());

            // Redireciona com base no tipo do usuário
            if (TipoUsuario.SUPERADMIN.equals(usuario.getTipo()) || 
                TipoUsuario.ADMIN.equals(usuario.getTipo())) {
                logger.info("Redirecionando admin/superadmin para lista de utilizadores");
                return "redirect:/cazio/utilizadores/todos-utilizadores";
            } else {
                logger.info("Redirecionando cliente para perfil");
                return "redirect:/cazio/utilizadores/perfil-do-utilizador";
            }
        } catch (Exception e) {
            logger.error("Erro durante o login", e);
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
                
                // Cliente normal sempre vai para o perfil
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