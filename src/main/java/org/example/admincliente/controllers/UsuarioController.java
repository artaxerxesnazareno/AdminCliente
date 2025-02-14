package org.example.admincliente.controllers;

import jakarta.validation.Valid;
import org.example.admincliente.dtos.UsuarioDTO;
import org.example.admincliente.dtos.UsuarioRegistroDTO;
import org.example.admincliente.dtos.UsuarioAtualizacaoDTO;
import org.example.admincliente.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<UsuarioDTO> criarAdmin(@Valid @RequestBody UsuarioRegistroDTO registroDTO) {
        return ResponseEntity.ok(usuarioService.criarAdmin(registroDTO));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<UsuarioDTO> atualizarAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioAtualizacaoDTO atualizacaoDTO) {
        return ResponseEntity.ok(usuarioService.atualizarAdmin(id, atualizacaoDTO));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deletarAdmin(@PathVariable Long id) {
        usuarioService.deletarAdmin(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin")
    public ResponseEntity<List<UsuarioDTO>> listarAdmins() {
        return ResponseEntity.ok(usuarioService.listarTodosAdmins());
    }

    // Endpoints para ADMIN e SUPERADMIN
    @PostMapping("/clientes")
    public ResponseEntity<UsuarioDTO> criarCliente(@Valid @RequestBody UsuarioRegistroDTO registroDTO) {
        return ResponseEntity.ok(usuarioService.criarCliente(registroDTO));
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<UsuarioDTO> atualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioAtualizacaoDTO atualizacaoDTO) {
        return ResponseEntity.ok(usuarioService.atualizarCliente(id, atualizacaoDTO));
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        usuarioService.deletarCliente(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<UsuarioDTO>> listarClientes() {
        return ResponseEntity.ok(usuarioService.listarTodosClientes());
    }

    @GetMapping("/clientes/paginado")
    public ResponseEntity<Page<UsuarioDTO>> listarClientesPaginado(Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarClientesPaginado(pageable));
    }

    @GetMapping("/clientes/busca")
    public ResponseEntity<List<UsuarioDTO>> buscarClientesPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(usuarioService.buscarPorNome(nome));
    }

    @GetMapping("/clientes/email/{email}")
    public ResponseEntity<UsuarioDTO> buscarClientePorEmail(@PathVariable String email) {
        return usuarioService.buscarPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoints públicos e para clientes
    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrarCliente(@Valid @RequestBody UsuarioRegistroDTO registroDTO) {
        return ResponseEntity.ok(usuarioService.registrarCliente(registroDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarProprioPerfil(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioAtualizacaoDTO atualizacaoDTO) {
        return ResponseEntity.ok(usuarioService.atualizarProprioPerfil(id, atualizacaoDTO));
    }
} 