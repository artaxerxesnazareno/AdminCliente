package org.example.admincliente.services;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

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
        try {
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

            // Atualiza os campos permitidos
            usuario.setNome(atualizacaoDTO.getNome());
            usuario.setEmail(atualizacaoDTO.getEmail());
            usuario.setTelefone(atualizacaoDTO.getTelefone());
            
            // Processa e atualiza a imagem se fornecida como arquivo
            if (atualizacaoDTO.getImagemFile() != null && !atualizacaoDTO.getImagemFile().isEmpty()) {
                String caminhoImagem = salvarImagem(atualizacaoDTO.getImagemFile(), usuario.getEmail());
                usuario.setImagem(caminhoImagem);
            }
            // Processa e atualiza a imagem se fornecida como base64
            else if (atualizacaoDTO.getImagemBase64() != null && !atualizacaoDTO.getImagemBase64().isEmpty()) {
                String caminhoImagem = salvarImagemBase64(atualizacaoDTO.getImagemBase64(), usuario.getEmail());
                usuario.setImagem(caminhoImagem);
            }
            
            if (atualizacaoDTO.getSenha() != null) {
                usuario.setSenha(passwordEncoder.encode(atualizacaoDTO.getSenha()));
            }

            return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
        } catch (IOException e) {
            logger.error("Erro ao processar imagem", e);
            throw new BusinessException("Erro ao processar imagem do usuário");
        }
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
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
            
            if (usuario.getTipo() != TipoUsuario.CLIENTE) {
                throw new RuntimeException("Usuário não é um cliente");
            }

            // Atualiza os campos permitidos
            usuario.setNome(atualizacaoDTO.getNome());
            usuario.setEmail(atualizacaoDTO.getEmail());
            usuario.setTelefone(atualizacaoDTO.getTelefone());
            
            // Processa e atualiza a imagem se fornecida como arquivo
            if (atualizacaoDTO.getImagemFile() != null && !atualizacaoDTO.getImagemFile().isEmpty()) {
                String caminhoImagem = salvarImagem(atualizacaoDTO.getImagemFile(), usuario.getEmail());
                usuario.setImagem(caminhoImagem);
            }
            // Processa e atualiza a imagem se fornecida como base64
            else if (atualizacaoDTO.getImagemBase64() != null && !atualizacaoDTO.getImagemBase64().isEmpty()) {
                String caminhoImagem = salvarImagemBase64(atualizacaoDTO.getImagemBase64(), usuario.getEmail());
                usuario.setImagem(caminhoImagem);
            }

            if (atualizacaoDTO.getSenha() != null) {
                usuario.setSenha(passwordEncoder.encode(atualizacaoDTO.getSenha()));
            }

            return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
        } catch (IOException e) {
            logger.error("Erro ao processar imagem", e);
            throw new BusinessException("Erro ao processar imagem do usuário");
        }
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

    public UsuarioDTO atualizarProprioPerfil(Long id, UsuarioAtualizacaoDTO atualizacaoDTO) {
        try {
            // Obtém o usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String emailAutenticado = auth.getName();
            
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
            
            // Verifica se o usuário está tentando atualizar seu próprio perfil
            if (!usuario.getEmail().equals(emailAutenticado)) {
                throw new BusinessException("Você só pode atualizar seu próprio perfil");
            }
            
            // Atualiza apenas os campos permitidos para o próprio usuário
            usuario.setNome(atualizacaoDTO.getNome());
            usuario.setTelefone(atualizacaoDTO.getTelefone());
            
            // Processa e atualiza a imagem se fornecida como arquivo
            if (atualizacaoDTO.getImagemFile() != null && !atualizacaoDTO.getImagemFile().isEmpty()) {
                String caminhoImagem = salvarImagem(atualizacaoDTO.getImagemFile(), usuario.getEmail());
                usuario.setImagem(caminhoImagem);
            }
            // Processa e atualiza a imagem se fornecida como base64
            else if (atualizacaoDTO.getImagemBase64() != null && !atualizacaoDTO.getImagemBase64().isEmpty()) {
                String caminhoImagem = salvarImagemBase64(atualizacaoDTO.getImagemBase64(), usuario.getEmail());
                usuario.setImagem(caminhoImagem);
            }
            
            // Atualiza a senha se fornecida
            if (atualizacaoDTO.getSenha() != null && !atualizacaoDTO.getSenha().isEmpty()) {
                usuario.setSenha(passwordEncoder.encode(atualizacaoDTO.getSenha()));
            }
            
            return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
        } catch (IOException e) {
            logger.error("Erro ao processar imagem", e);
            throw new BusinessException("Erro ao processar imagem do usuário");
        }
    }

    private String salvarImagemBase64(String base64Image, String email) throws IOException {
        if (base64Image == null || base64Image.isEmpty()) {
            return null;
        }

        // Remove o prefixo do base64 se existir (ex: "data:image/jpeg;base64,")
        String[] parts = base64Image.split(",");
        String imageData = parts.length > 1 ? parts[1] : parts[0];

        // Decodifica o base64
        byte[] imageBytes = java.util.Base64.getDecoder().decode(imageData);

        // Cria o diretório de uploads se não existir
        String uploadDir = "uploads/usuarios";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Gera um nome único para o arquivo
        String fileName = email.replace("@", "_at_") + "_" + 
                        LocalDateTime.now().toString().replace(":", "-") + ".jpg";
        String filePath = uploadDir + "/" + fileName;

        // Salva o arquivo
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath)) {
            fos.write(imageBytes);
        }

        return "/" + filePath; // Retorna o caminho relativo para salvar no banco
    }

    private String salvarImagem(MultipartFile arquivo, String email) throws IOException {
        if (arquivo == null || arquivo.isEmpty()) {
            return null;
        }

        // Cria o diretório de uploads se não existir
        String uploadDir = "uploads/usuarios";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Gera um nome único para o arquivo
        String fileName = email.replace("@", "_at_") + "_" + 
                        LocalDateTime.now().toString().replace(":", "-") + ".jpg";
        String filePath = uploadDir + "/" + fileName;

        // Salva o arquivo
        File destFile = new File(filePath);
        arquivo.transferTo(destFile);

        return "/" + filePath; // Retorna o caminho relativo para salvar no banco
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

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public UsuarioDTO criarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        // Verifica se é um ADMIN tentando criar um ADMIN
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isSuperAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));

        if (!isSuperAdmin && TipoUsuario.ADMIN.equals(usuarioDTO.getTipo())) {
            throw new BusinessException("Apenas SUPERADMIN pode criar contas do tipo ADMIN");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(usuarioDTO.getNome());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setTelefone(usuarioDTO.getTelefone());
        usuario.setTipo(usuarioDTO.getTipo());
        usuario.setDataCriacao(LocalDateTime.now());

        // Usa a senha informada
        if (usuarioDTO.getSenha() == null || usuarioDTO.getSenha().isEmpty()) {
            throw new BusinessException("A senha é obrigatória");
        }
        usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));

        return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public UsuarioDTO atualizarUsuario(Long id, UsuarioAtualizacaoDTO atualizacaoDTO) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
            
            // Atualiza os campos
            usuario.setNome(atualizacaoDTO.getNome());
            usuario.setEmail(atualizacaoDTO.getEmail());
            usuario.setTelefone(atualizacaoDTO.getTelefone());
            usuario.setTipo(atualizacaoDTO.getTipo());
            
            // Processa e atualiza a imagem se fornecida como arquivo
            if (atualizacaoDTO.getImagemFile() != null && !atualizacaoDTO.getImagemFile().isEmpty()) {
                String caminhoImagem = salvarImagem(atualizacaoDTO.getImagemFile(), usuario.getEmail());
                usuario.setImagem(caminhoImagem);
            }
            // Processa e atualiza a imagem se fornecida como base64
            else if (atualizacaoDTO.getImagemBase64() != null && !atualizacaoDTO.getImagemBase64().isEmpty()) {
                String caminhoImagem = salvarImagemBase64(atualizacaoDTO.getImagemBase64(), usuario.getEmail());
                usuario.setImagem(caminhoImagem);
            }
            
            // Atualiza a senha se fornecida
            if (atualizacaoDTO.getSenha() != null && !atualizacaoDTO.getSenha().isEmpty()) {
                usuario.setSenha(passwordEncoder.encode(atualizacaoDTO.getSenha()));
            }
            
            return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
        } catch (IOException e) {
            logger.error("Erro ao processar imagem", e);
            throw new BusinessException("Erro ao processar imagem do usuário");
        }
    }
} 