package org.example.admincliente.dtos;

import java.time.LocalDateTime;

import org.example.admincliente.entities.Usuario;
import org.example.admincliente.enums.TipoUsuario;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String imagem;
    private LocalDateTime dataCriacao;
    private TipoUsuario tipo;
    private String senha;

    public static UsuarioDTO fromEntity(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setTelefone(usuario.getTelefone());
        dto.setImagem(usuario.getImagem());
        dto.setDataCriacao(usuario.getDataCriacao());
        dto.setTipo(usuario.getTipo());
        // Não enviamos a senha por questões de segurança
        return dto;
    }
} 