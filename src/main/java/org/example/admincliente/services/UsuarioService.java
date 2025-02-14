package org.example.admincliente.services;

import org.example.admincliente.entities.Usuario;
import org.example.admincliente.enums.TipoUsuario;
import org.example.admincliente.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Métodos para SUPERADMIN
    @PreAuthorize("hasRole('SUPERADMIN')")
    public Usuario criarAdmin(Usuario usuario) {
        usuario.setTipo(TipoUsuario.ADMIN);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    public void deletarAdmin(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin não encontrado"));
        if (usuario.getTipo() != TipoUsuario.ADMIN) {
            throw new RuntimeException("Usuário não é um admin");
        }
        usuarioRepository.delete(usuario);
    }

    // Métodos para ADMIN e SUPERADMIN
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public Usuario criarCliente(Usuario usuario) {
        usuario.setTipo(TipoUsuario.CLIENTE);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public void deletarCliente(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        if (usuario.getTipo() != TipoUsuario.CLIENTE) {
            throw new RuntimeException("Usuário não é um cliente");
        }
        usuarioRepository.delete(usuario);
    }

    // Métodos para CLIENTE (auto-gerenciamento)
    public Usuario registrarCliente(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        usuario.setTipo(TipoUsuario.CLIENTE);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    @PreAuthorize("#id == authentication.principal.id")
    public Usuario atualizarProprioPerfil(Long id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if (usuarioAtualizado.getNome() != null) {
            usuarioExistente.setNome(usuarioAtualizado.getNome());
        }
        if (usuarioAtualizado.getTelefone() != null) {
            usuarioExistente.setTelefone(usuarioAtualizado.getTelefone());
        }
        if (usuarioAtualizado.getImagem() != null) {
            usuarioExistente.setImagem(usuarioAtualizado.getImagem());
        }
        if (usuarioAtualizado.getSenha() != null) {
            usuarioExistente.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
        }

        return usuarioRepository.save(usuarioExistente);
    }

    // Métodos de consulta com restrições de acesso
    @PreAuthorize("hasRole('SUPERADMIN')")
    public List<Usuario> listarTodosAdmins() {
        return usuarioRepository.findByTipo(TipoUsuario.ADMIN);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public List<Usuario> listarTodosClientes() {
        return usuarioRepository.findByTipo(TipoUsuario.CLIENTE);
    }
} 