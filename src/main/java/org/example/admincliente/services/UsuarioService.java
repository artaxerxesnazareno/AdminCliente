package org.example.admincliente.services;

import org.example.admincliente.dtos.UsuarioDTO;
import org.example.admincliente.dtos.UsuarioRegistroDTO;
import org.example.admincliente.dtos.UsuarioAtualizacaoDTO;
import org.example.admincliente.entities.Usuario;
import org.example.admincliente.enums.TipoUsuario;
import org.example.admincliente.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Métodos para SUPERADMIN
    @PreAuthorize("hasRole('SUPERADMIN')")
    public UsuarioDTO criarAdmin(UsuarioRegistroDTO registroDTO) {
        Usuario usuario = registroDTO.toEntity();
        usuario.setTipo(TipoUsuario.ADMIN);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    public UsuarioDTO atualizarAdmin(Long id, UsuarioAtualizacaoDTO atualizacaoDTO) {
        Usuario admin = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin não encontrado"));
        
        if (admin.getTipo() != TipoUsuario.ADMIN) {
            throw new RuntimeException("Usuário não é um admin");
        }

        atualizacaoDTO.atualizarEntity(admin);
        if (atualizacaoDTO.getSenha() != null) {
            admin.setSenha(passwordEncoder.encode(atualizacaoDTO.getSenha()));
        }
        
        return UsuarioDTO.fromEntity(usuarioRepository.save(admin));
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    public void deletarAdmin(Long id) {
        Usuario admin = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin não encontrado"));
        if (admin.getTipo() != TipoUsuario.ADMIN) {
            throw new RuntimeException("Usuário não é um admin");
        }
        usuarioRepository.delete(admin);
    }

    // Métodos para ADMIN e SUPERADMIN
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public UsuarioDTO criarCliente(UsuarioRegistroDTO registroDTO) {
        Usuario usuario = registroDTO.toEntity();
        usuario.setTipo(TipoUsuario.CLIENTE);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public UsuarioDTO atualizarCliente(Long id, UsuarioAtualizacaoDTO atualizacaoDTO) {
        Usuario cliente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        
        if (cliente.getTipo() != TipoUsuario.CLIENTE) {
            throw new RuntimeException("Usuário não é um cliente");
        }

        atualizacaoDTO.atualizarEntity(cliente);
        if (atualizacaoDTO.getSenha() != null) {
            cliente.setSenha(passwordEncoder.encode(atualizacaoDTO.getSenha()));
        }

        return UsuarioDTO.fromEntity(usuarioRepository.save(cliente));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public void deletarCliente(Long id) {
        Usuario cliente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        if (cliente.getTipo() != TipoUsuario.CLIENTE) {
            throw new RuntimeException("Usuário não é um cliente");
        }
        usuarioRepository.delete(cliente);
    }

    // Métodos para CLIENTE (auto-gerenciamento)
    @Transactional
    public UsuarioDTO registrarCliente(UsuarioRegistroDTO registroDTO) {
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        Usuario usuario = registroDTO.toEntity();
        usuario.setTipo(TipoUsuario.CLIENTE);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @PreAuthorize("#id == authentication.principal.id")
    public UsuarioDTO atualizarProprioPerfil(Long id, UsuarioAtualizacaoDTO atualizacaoDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if (atualizacaoDTO.getEmail() != null && 
            !atualizacaoDTO.getEmail().equals(usuarioExistente.getEmail()) && 
            usuarioRepository.existsByEmail(atualizacaoDTO.getEmail())) {
            throw new RuntimeException("Email já está em uso");
        }

        atualizacaoDTO.atualizarEntity(usuarioExistente);
        if (atualizacaoDTO.getSenha() != null) {
            usuarioExistente.setSenha(passwordEncoder.encode(atualizacaoDTO.getSenha()));
        }

        return UsuarioDTO.fromEntity(usuarioRepository.save(usuarioExistente));
    }

    // Métodos de consulta
    @PreAuthorize("hasRole('SUPERADMIN')")
    public List<UsuarioDTO> listarTodosAdmins() {
        return usuarioRepository.findByTipo(TipoUsuario.ADMIN)
                .stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public List<UsuarioDTO> listarTodosClientes() {
        return usuarioRepository.findByTipo(TipoUsuario.CLIENTE)
                .stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public Page<UsuarioDTO> listarClientesPaginado(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(UsuarioDTO::fromEntity);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public List<UsuarioDTO> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public Optional<UsuarioDTO> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).map(UsuarioDTO::fromEntity);
    }
} 