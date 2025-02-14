package org.example.admincliente.repositories;

import org.example.admincliente.entities.Usuario;
import org.example.admincliente.enums.TipoUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    List<Usuario> findByTipo(TipoUsuario tipo);
    
    boolean existsByEmail(String email);
    
    List<Usuario> findByNomeContainingIgnoreCase(String nome);
    
    Optional<Usuario> findByEmailAndTipo(String email, TipoUsuario tipo);
    
    List<Usuario> findByTipoIn(List<TipoUsuario> tipos);

    Page<Usuario> findByTipo(TipoUsuario tipo, Pageable pageable);
} 