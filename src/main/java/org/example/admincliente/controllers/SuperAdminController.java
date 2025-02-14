package org.example.admincliente.controllers;

import org.example.admincliente.dtos.UsuarioDTO;
import org.example.admincliente.dtos.UsuarioRegistroDTO;
import org.example.admincliente.dtos.UsuarioAtualizacaoDTO;
import org.example.admincliente.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/superadmin")
@PreAuthorize("hasRole('SUPERADMIN')")
public class SuperAdminController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("admins", usuarioService.listarTodosAdmins());
        model.addAttribute("clientes", usuarioService.listarTodosClientes());
        return "superadmin/dashboard";
    }

    @GetMapping("/admins")
    public String listarAdmins(Model model) {
        model.addAttribute("admins", usuarioService.listarTodosAdmins());
        return "superadmin/admins";
    }

    @GetMapping("/admins/novo")
    public String novoAdminForm() {
        return "superadmin/admin-form";
    }

    @GetMapping("/admins/{id}")
    public String editarAdminForm(@PathVariable Long id, Model model) {
        model.addAttribute("admin", usuarioService.buscarPorId(id).orElseThrow());
        return "superadmin/admin-form";
    }

    // API Endpoints
    @PostMapping("/api/admins")
    @ResponseBody
    public ResponseEntity<UsuarioDTO> criarAdmin(@RequestBody UsuarioRegistroDTO registroDTO) {
        return ResponseEntity.ok(usuarioService.criarAdmin(registroDTO));
    }

    @PutMapping("/api/admins/{id}")
    @ResponseBody
    public ResponseEntity<UsuarioDTO> atualizarAdmin(
            @PathVariable Long id,
            @RequestBody UsuarioAtualizacaoDTO atualizacaoDTO) {
        return ResponseEntity.ok(usuarioService.atualizarAdmin(id, atualizacaoDTO));
    }

    @DeleteMapping("/api/admins/{id}")
    @ResponseBody
    public ResponseEntity<Void> deletarAdmin(@PathVariable Long id) {
        usuarioService.deletarAdmin(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/relatorios")
    public String relatorios(Model model) {
        model.addAttribute("totalAdmins", usuarioService.listarTodosAdmins().size());
        model.addAttribute("totalClientes", usuarioService.listarTodosClientes().size());
        return "superadmin/relatorios";
    }
} 