package org.example.admincliente.controllers;

import org.example.admincliente.dtos.UsuarioDTO;
import org.example.admincliente.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cazio/utilizadores")
public class UtilizadorController {

    private static final Logger logger = LoggerFactory.getLogger(UtilizadorController.class);

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/perfil-do-utilizador")
    @PostMapping("/perfil-do-utilizador")
    public String perfilUtilizador(Model model, Authentication authentication) {
        try {
            logger.info("Iniciando carregamento do perfil do utilizador");
            
            // Obtém o email do usuário autenticado
            String email = authentication.getName();
            logger.info("Email do usuário autenticado: {}", email);
            
            // Busca o usuário pelo email
            UsuarioDTO usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            logger.info("Usuário encontrado: {}", usuario);
            
            // Adiciona os dados do usuário ao modelo
            model.addAttribute("nome", usuario.getNome());
            model.addAttribute("email", usuario.getEmail());
            model.addAttribute("telefone", usuario.getTelefone());
            model.addAttribute("tipo", usuario.getTipo().toString());
            
            logger.info("Dados adicionados ao modelo com sucesso");
            return "cazio/utilizadores/perfil-do-utilizador";
        } catch (Exception e) {
            logger.error("Erro ao carregar perfil do utilizador", e);
            throw e;
        }
    }
} 