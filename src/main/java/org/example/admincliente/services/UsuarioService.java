package org.example.admincliente.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.admincliente.dtos.UsuarioAtualizacaoDTO;
import org.example.admincliente.dtos.UsuarioDTO;
import org.example.admincliente.dtos.UsuarioRegistroDTO;
import org.example.admincliente.entities.Usuario;
import org.example.admincliente.enums.TipoUsuario;
import org.example.admincliente.exceptions.BusinessException;
import org.example.admincliente.exceptions.ResourceNotFoundException;
import org.example.admincliente.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<UsuarioDTO> buscarPorId(Long id) {
        return usuarioRepository.findById(id).map(UsuarioDTO::fromEntity);
    }

    public Optional<UsuarioDTO> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).map(UsuarioDTO::fromEntity);
    }

    // Métodos para SUPERADMIN
    @PreAuthorize("hasRole('SUPERADMIN')")
    public UsuarioDTO criarAdmin(UsuarioRegistroDTO registroDTO) {
        Usuario usuario = registroDTO.toEntity();
        usuario.setTipo(TipoUsuario.ADMIN);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    public void deletarAdmin(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", id));
                
        // Verifica se é o superadmin root
        if (usuario.getEmail().equals("root@admin.com")) {
            throw new BusinessException("O superadmin root não pode ser excluído");
        }
        
        if (usuario.getTipo() != TipoUsuario.ADMIN) {
            throw new RuntimeException("Usuário não é um admin");
        }
        usuarioRepository.delete(usuario);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    public UsuarioDTO atualizarAdmin(Long id, UsuarioAtualizacaoDTO atualizacaoDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", id));
        
        // Verifica se é o superadmin root
        if (usuario.getEmail().equals("root@admin.com")) {
            // Permite apenas atualizar a senha do root
            if (atualizacaoDTO.getSenha() != null) {
                usuario.setSenha(passwordEncoder.encode(atualizacaoDTO.getSenha()));
            }
            // Não permite alterar outros dados do root
            return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
        }
        
        if (usuario.getTipo() != TipoUsuario.ADMIN) {
            throw new RuntimeException("Usuário não é um admin");
        }

        atualizacaoDTO.atualizarEntity(usuario);
        if (atualizacaoDTO.getSenha() != null) {
            usuario.setSenha(passwordEncoder.encode(atualizacaoDTO.getSenha()));
        }

        return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
    }

    // Métodos para ADMIN e SUPERADMIN
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public UsuarioDTO criarCliente(UsuarioRegistroDTO registroDTO) {
        Usuario usuario = registroDTO.toEntity();
        usuario.setTipo(TipoUsuario.CLIENTE);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public UsuarioDTO atualizarCliente(Long id, UsuarioAtualizacaoDTO atualizacaoDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        
        if (usuario.getTipo() != TipoUsuario.CLIENTE) {
            throw new RuntimeException("Usuário não é um cliente");
        }

        atualizacaoDTO.atualizarEntity(usuario);
        if (atualizacaoDTO.getSenha() != null) {
            usuario.setSenha(passwordEncoder.encode(atualizacaoDTO.getSenha()));
        }

        return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public void deletarCliente(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        if (usuario.getTipo() != TipoUsuario.CLIENTE) {
            throw new RuntimeException("Usuário não é um cliente");
        }
        usuarioRepository.delete(usuario);
    }

    // Métodos para CLIENTE (auto-gerenciamento)
    public UsuarioDTO registrarCliente(UsuarioRegistroDTO registroDTO) {
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        Usuario usuario = registroDTO.toEntity();
        usuario.setTipo(TipoUsuario.CLIENTE);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @PreAuthorize("#id == authentication.principal.id")
    public UsuarioDTO atualizarProprioPerfil(Long id, UsuarioAtualizacaoDTO atualizacaoDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
        
        atualizacaoDTO.atualizarEntity(usuario);
        if (atualizacaoDTO.getSenha() != null) {
            usuario.setSenha(passwordEncoder.encode(atualizacaoDTO.getSenha()));
        }

        return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
    }

    // Métodos de consulta com restrições de acesso
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
        return usuarioRepository.findByTipo(TipoUsuario.CLIENTE, pageable)
                .map(UsuarioDTO::fromEntity);
    }
} 