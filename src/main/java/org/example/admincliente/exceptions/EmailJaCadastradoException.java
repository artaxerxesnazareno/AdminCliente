package org.example.admincliente.exceptions;

public class EmailJaCadastradoException extends BusinessException {
    
    public EmailJaCadastradoException() {
        super("Email já está cadastrado no sistema");
    }

    public EmailJaCadastradoException(String email) {
        super(String.format("O email %s já está cadastrado no sistema", email));
    }
} 