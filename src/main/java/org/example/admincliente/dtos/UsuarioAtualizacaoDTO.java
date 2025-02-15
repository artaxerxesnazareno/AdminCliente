package org.example.admincliente.dtos;

import org.example.admincliente.enums.TipoUsuario;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAtualizacaoDTO {
    
    private Long id;
    
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;
    
    @NotBlank(message = "O telefone é obrigatório")
    private String telefone;
    
    @Email(message = "Email inválido")
    private String email;
    
    private String senha;
    
    private String confirmacaoSenha;
    
    private String imagemBase64;
    
    @JsonIgnore
    private MultipartFile imagemFile;
    
    private TipoUsuario tipo;
} 