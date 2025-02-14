package org.example.admincliente.exceptions;

public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String resource) {
        super(String.format("%s não encontrado", resource));
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s não encontrado com id: %d", resource, id));
    }

    public ResourceNotFoundException(String resource, String identifier) {
        super(String.format("%s não encontrado com identificador: %s", resource, identifier));
    }
} 