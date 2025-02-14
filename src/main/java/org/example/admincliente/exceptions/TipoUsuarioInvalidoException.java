package org.example.admincliente.exceptions;

public class TipoUsuarioInvalidoException extends BusinessException {
    
    public TipoUsuarioInvalidoException(String message) {
        super(message);
    }

    public TipoUsuarioInvalidoException(String tipoEsperado, String tipoAtual) {
        super(String.format("Usuário deveria ser do tipo %s, mas é do tipo %s", tipoEsperado, tipoAtual));
    }
} 