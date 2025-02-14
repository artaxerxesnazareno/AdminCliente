package org.example.admincliente.config;

import java.time.LocalDateTime;

import org.example.admincliente.entities.Usuario;
import org.example.admincliente.enums.TipoUsuario;
import org.example.admincliente.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SuperAdminInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Verifica se já existe um superadmin root
        if (!usuarioRepository.existsByEmail("root@admin.com")) {
            // Cria o superadmin root
            Usuario superAdmin = new Usuario();
            superAdmin.setNome("Root Administrator");
            superAdmin.setEmail("root@admin.com");
            superAdmin.setSenha(passwordEncoder.encode("root123")); // Senha: root123
            superAdmin.setTelefone("000000000");
            superAdmin.setTipo(TipoUsuario.SUPERADMIN);
            superAdmin.setDataCriacao(LocalDateTime.now());
            
            // Salva o superadmin
            usuarioRepository.save(superAdmin);
            
            System.out.println("SuperAdmin root criado com sucesso!");
            System.out.println("Email: root@admin.com");
            System.out.println("Senha: root123");
        }
    }
} 