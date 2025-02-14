package org.example.admincliente.controllers;

import jakarta.validation.Valid;
import org.example.admincliente.dtos.LoginDTO;
import org.example.admincliente.dtos.TokenDTO;
import org.example.admincliente.dtos.UsuarioRegistroDTO;
import org.example.admincliente.dtos.UsuarioDTO;
import org.example.admincliente.services.AuthService;
import org.example.admincliente.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> autenticar(@RequestBody @Valid LoginDTO loginDTO) {
        TokenDTO token = authService.autenticar(loginDTO);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrar(@RequestBody @Valid UsuarioRegistroDTO registroDTO) {
        return ResponseEntity.ok(usuarioService.registrarCliente(registroDTO));
    }
} 