package org.example.admincliente.controllers;

import org.example.admincliente.dtos.UsuarioDTO;
import org.example.admincliente.dtos.UsuarioRegistroDTO;
import org.example.admincliente.dtos.UsuarioAtualizacaoDTO;
import org.example.admincliente.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("clientes", usuarioService.listarTodosClientes());
        return "admin/dashboard";
    }

    @GetMapping("/clientes")
    public String listarClientes(Model model, Pageable pageable) {
        model.addAttribute("clientesPage", usuarioService.listarClientesPaginado(pageable));
        return "admin/clientes";
    }

    @GetMapping("/clientes/novo")
    public String novoClienteForm() {
        return "admin/cliente-form";
    }

    @GetMapping("/clientes/{id}")
    public String editarClienteForm(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", usuarioService.buscarPorId(id).orElseThrow());
        return "admin/cliente-form";
    }

    // API Endpoints
    @PostMapping("/api/clientes")
    @ResponseBody
    public ResponseEntity<UsuarioDTO> criarCliente(@RequestBody UsuarioRegistroDTO registroDTO) {
        return ResponseEntity.ok(usuarioService.criarCliente(registroDTO));
    }

    @PutMapping("/api/clientes/{id}")
    @ResponseBody
    public ResponseEntity<UsuarioDTO> atualizarCliente(
            @PathVariable Long id,
            @RequestBody UsuarioAtualizacaoDTO atualizacaoDTO) {
        return ResponseEntity.ok(usuarioService.atualizarCliente(id, atualizacaoDTO));
    }

    @DeleteMapping("/api/clientes/{id}")
    @ResponseBody
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        usuarioService.deletarCliente(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/clientes/busca")
    @ResponseBody
    public ResponseEntity<Page<UsuarioDTO>> buscarClientes(
            @RequestParam(required = false) String nome,
            Pageable pageable) {
        if (nome != null && !nome.trim().isEmpty()) {
            return ResponseEntity.ok(Page.empty()); // TODO: Implementar busca por nome paginada
        }
        return ResponseEntity.ok(usuarioService.listarClientesPaginado(pageable));
    }

    @GetMapping("/perfil")
    public String perfilAdmin(Model model, Authentication authentication) {
        model.addAttribute("admin", usuarioService.buscarPorEmail(authentication.getName()).orElseThrow());
        return "admin/perfil";
    }
} 