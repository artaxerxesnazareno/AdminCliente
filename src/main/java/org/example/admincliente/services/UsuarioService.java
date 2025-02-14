package org.example.admincliente.services;

import org.example.admincliente.dtos.UsuarioDTO;
import org.example.admincliente.dtos.UsuarioRegistroDTO;
import org.example.admincliente.dtos.UsuarioAtualizacaoDTO;
import org.example.admincliente.entities.Usuario;
import org.example.admincliente.enums.TipoUsuario;
import org.example.admincliente.exceptions.*;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    private static final Pattern SENHA_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Métodos de validação
    private void validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email é obrigatório");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Email inválido");
        }
    }

    private void validarEmailUnico(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException(email);
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.trim().isEmpty()) {
            throw new ValidationException("Senha é obrigatória");
        }
        if (!SENHA_PATTERN.matcher(senha).matches()) {
            throw new SenhaInvalidaException(
                "A senha deve conter pelo menos 8 caracteres, incluindo: " +
                "letra maiúscula, letra minúscula, número e caractere especial"
            );
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidationException("Nome é obrigatório");
        }
        if (nome.trim().length() < 3) {
            throw new ValidationException("Nome deve ter pelo menos 3 caracteres");
        }
        if (nome.trim().length() > 100) {
            throw new ValidationException("Nome deve ter no máximo 100 caracteres");
        }
    }

    private void validarTipoUsuario(Usuario usuario, TipoUsuario tipoEsperado) {
        if (usuario.getTipo() != tipoEsperado) {
            throw new TipoUsuarioInvalidoException(tipoEsperado.name(), usuario.getTipo().name());
        }
    }

    // Métodos de busca
    public Optional<UsuarioDTO> buscarPorId(Long id) {
        return usuarioRepository.findById(id).map(UsuarioDTO::fromEntity);
    }

    public Optional<UsuarioDTO> buscarPorEmail(String email) {
        validarEmail(email);
        return usuarioRepository.findByEmail(email).map(UsuarioDTO::fromEntity);
    }

    // Métodos para SUPERADMIN
    @PreAuthorize("hasRole('SUPERADMIN')")
    public UsuarioDTO criarAdmin(UsuarioRegistroDTO registroDTO) {
        validarNome(registroDTO.getNome());
        validarEmail(registroDTO.getEmail());
        validarEmailUnico(registroDTO.getEmail());
        validarSenha(registroDTO.getSenha());

        Usuario usuario = registroDTO.toEntity();
        usuario.setTipo(TipoUsuario.ADMIN);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setSenha(passwordEncoder.encode(registroDTO.getSenha()));
        
        return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    public UsuarioDTO atualizarAdmin(Long id, UsuarioAtualizacaoDTO atualizacaoDTO) {
        Usuario admin = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador", id));
        
        validarTipoUsuario(admin, TipoUsuario.ADMIN);

        if (atualizacaoDTO.getNome() != null) {
            validarNome(atualizacaoDTO.getNome());
        }
        
        if (atualizacaoDTO.getEmail() != null) {
            validarEmail(atualizacaoDTO.getEmail());
            if (!atualizacaoDTO.getEmail().equals(admin.getEmail())) {
                validarEmailUnico(atualizacaoDTO.getEmail());
            }
        }

        if (atualizacaoDTO.getSenha() != null) {
            validarSenha(atualizacaoDTO.getSenha());
            admin.setSenha(passwordEncoder.encode(atualizacaoDTO.getSenha()));
        }

        atualizacaoDTO.atualizarEntity(admin);
        return UsuarioDTO.fromEntity(usuarioRepository.save(admin));
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    public void deletarAdmin(Long id) {
        Usuario admin = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador", id));
        validarTipoUsuario(admin, TipoUsuario.ADMIN);
        usuarioRepository.delete(admin);
    }

    // Métodos para ADMIN e SUPERADMIN
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public UsuarioDTO criarCliente(UsuarioRegistroDTO registroDTO) {
        validarNome(registroDTO.getNome());
        validarEmail(registroDTO.getEmail());
        validarEmailUnico(registroDTO.getEmail());
        validarSenha(registroDTO.getSenha());

        Usuario usuario = registroDTO.toEntity();
        usuario.setTipo(TipoUsuario.CLIENTE);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setSenha(passwordEncoder.encode(registroDTO.getSenha()));
        
        return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public UsuarioDTO atualizarCliente(Long id, UsuarioAtualizacaoDTO atualizacaoDTO) {
        Usuario cliente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        
        validarTipoUsuario(cliente, TipoUsuario.CLIENTE);

        if (atualizacaoDTO.getNome() != null) {
            validarNome(atualizacaoDTO.getNome());
        }
        
        if (atualizacaoDTO.getEmail() != null) {
            validarEmail(atualizacaoDTO.getEmail());
            if (!atualizacaoDTO.getEmail().equals(cliente.getEmail())) {
                validarEmailUnico(atualizacaoDTO.getEmail());
            }
        }

        if (atualizacaoDTO.getSenha() != null) {
            validarSenha(atualizacaoDTO.getSenha());
            cliente.setSenha(passwordEncoder.encode(atualizacaoDTO.getSenha()));
        }

        atualizacaoDTO.atualizarEntity(cliente);
        return UsuarioDTO.fromEntity(usuarioRepository.save(cliente));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public void deletarCliente(Long id) {
        Usuario cliente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        validarTipoUsuario(cliente, TipoUsuario.CLIENTE);
        usuarioRepository.delete(cliente);
    }

    // Métodos para CLIENTE (auto-gerenciamento)
    @Transactional
    public UsuarioDTO registrarCliente(UsuarioRegistroDTO registroDTO) {
        validarNome(registroDTO.getNome());
        validarEmail(registroDTO.getEmail());
        validarEmailUnico(registroDTO.getEmail());
        validarSenha(registroDTO.getSenha());

        Usuario usuario = registroDTO.toEntity();
        usuario.setTipo(TipoUsuario.CLIENTE);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setSenha(passwordEncoder.encode(registroDTO.getSenha()));
        
        return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @PreAuthorize("#id == authentication.principal.id")
    public UsuarioDTO atualizarProprioPerfil(Long id, UsuarioAtualizacaoDTO atualizacaoDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));

        if (atualizacaoDTO.getNome() != null) {
            validarNome(atualizacaoDTO.getNome());
        }
        
        if (atualizacaoDTO.getEmail() != null) {
            validarEmail(atualizacaoDTO.getEmail());
            if (!atualizacaoDTO.getEmail().equals(usuarioExistente.getEmail())) {
                validarEmailUnico(atualizacaoDTO.getEmail());
            }
        }

        if (atualizacaoDTO.getSenha() != null) {
            validarSenha(atualizacaoDTO.getSenha());
            usuarioExistente.setSenha(passwordEncoder.encode(atualizacaoDTO.getSenha()));
        }

        atualizacaoDTO.atualizarEntity(usuarioExistente);
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
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidationException("Nome de busca não pode ser vazio");
        }
        return usuarioRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
    }
} 