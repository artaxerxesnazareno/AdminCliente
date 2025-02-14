package org.example.admincliente.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends BusinessException {
    
    private final List<String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
        this.errors.add(message);
    }

    public ValidationException(List<String> errors) {
        super("Erro de validação");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
} 