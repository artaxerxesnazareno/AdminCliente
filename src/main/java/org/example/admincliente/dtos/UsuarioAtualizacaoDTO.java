package org.example.admincliente.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.admincliente.entities.Usuario;

@Data
public class UsuarioAtualizacaoDTO {
    
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @Email(message = "Email deve ser válido")
    private String email;

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    private String telefone;
    private String imagem;

    public void atualizarEntity(Usuario usuario) {
        if (this.nome != null) {
            usuario.setNome(this.nome);
        }
        if (this.email != null) {
            usuario.setEmail(this.email);
        }
        if (this.senha != null) {
            usuario.setSenha(this.senha);
        }
        if (this.telefone != null) {
            usuario.setTelefone(this.telefone);
        }
        if (this.imagem != null) {
            usuario.setImagem(this.imagem);
        }
    }
} 