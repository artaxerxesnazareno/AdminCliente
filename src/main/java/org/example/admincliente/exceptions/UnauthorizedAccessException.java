package org.example.admincliente.exceptions;

public class UnauthorizedAccessException extends BusinessException {
    
    public UnauthorizedAccessException() {
        super("Acesso não autorizado");
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccessException(String resource, String action) {
        super(String.format("Não autorizado a %s este %s", action, resource));
    }
} 