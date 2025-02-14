package org.example.admincliente.controllers;

import org.example.admincliente.dtos.UsuarioDTO;
import org.example.admincliente.dtos.UsuarioAtualizacaoDTO;
import org.example.admincliente.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cliente")
@PreAuthorize("hasRole('CLIENTE')")
public class ClienteController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        model.addAttribute("usuario", usuarioService.buscarPorEmail(authentication.getName()).orElseThrow());
        return "cliente/dashboard";
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Authentication authentication) {
        model.addAttribute("usuario", usuarioService.buscarPorEmail(authentication.getName()).orElseThrow());
        return "cliente/perfil";
    }

    @GetMapping("/perfil/editar")
    public String editarPerfil(Model model, Authentication authentication) {
        model.addAttribute("usuario", usuarioService.buscarPorEmail(authentication.getName()).orElseThrow());
        return "cliente/perfil-form";
    }

    // API Endpoints
    @PutMapping("/api/perfil")
    @ResponseBody
    public ResponseEntity<UsuarioDTO> atualizarPerfil(
            Authentication authentication,
            @RequestBody UsuarioAtualizacaoDTO atualizacaoDTO) {
        UsuarioDTO usuario = usuarioService.buscarPorEmail(authentication.getName()).orElseThrow();
        return ResponseEntity.ok(usuarioService.atualizarProprioPerfil(usuario.getId(), atualizacaoDTO));
    }

    @GetMapping("/api/dados")
    @ResponseBody
    public ResponseEntity<UsuarioDTO> getDadosUsuario(Authentication authentication) {
        return usuarioService.buscarPorEmail(authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 