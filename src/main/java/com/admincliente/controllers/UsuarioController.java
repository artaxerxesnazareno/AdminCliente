package com.admincliente.controllers;

import com.admincliente.entities.Usuario;
import com.admincliente.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Endpoints para SUPERADMIN
    @PostMapping("/admin")
    public ResponseEntity<Usuario> criarAdmin(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.criarAdmin(usuario));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deletarAdmin(@PathVariable Long id) {
        usuarioService.deletarAdmin(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Usuario>> listarAdmins() {
        return ResponseEntity.ok(usuarioService.listarTodosAdmins());
    }

    // Endpoints para ADMIN e SUPERADMIN
    @PostMapping("/clientes")
    public ResponseEntity<Usuario> criarCliente(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.criarCliente(usuario));
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        usuarioService.deletarCliente(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<Usuario>> listarClientes() {
        return ResponseEntity.ok(usuarioService.listarTodosClientes());
    }

    // Endpoints públicos e para clientes
    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrarCliente(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.registrarCliente(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarProprioPerfil(
            @PathVariable Long id,
            @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.atualizarProprioPerfil(id, usuario));
    }
} 