package org.example.admincliente.exceptions;

public class SenhaInvalidaException extends BusinessException {
    
    public SenhaInvalidaException() {
        super("A senha não atende aos requisitos mínimos de segurança");
    }

    public SenhaInvalidaException(String message) {
        super(message);
    }
} 