package org.example.admincliente.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDTO {
    private String token;
    private String tipo;
    private UsuarioDTO usuario;
} 