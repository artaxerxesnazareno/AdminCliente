package org.example.admincliente.dtos;

import lombok.Data;
import org.example.admincliente.entities.Usuario;
import org.example.admincliente.enums.TipoUsuario;

import java.time.LocalDateTime;

@Data
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String imagem;
    private LocalDateTime dataCriacao;
    private TipoUsuario tipo;

    public static UsuarioDTO fromEntity(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setTelefone(usuario.getTelefone());
        dto.setImagem(usuario.getImagem());
        dto.setDataCriacao(usuario.getDataCriacao());
        dto.setTipo(usuario.getTipo());
        return dto;
    }
} 