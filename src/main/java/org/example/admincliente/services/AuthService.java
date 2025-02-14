package org.example.admincliente.services;

import org.example.admincliente.dtos.LoginDTO;
import org.example.admincliente.dtos.TokenDTO;
import org.example.admincliente.dtos.UsuarioDTO;
import org.example.admincliente.entities.Usuario;
import org.example.admincliente.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public TokenDTO autenticar(LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken dadosLogin = new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(),
                loginDTO.getSenha()
        );

        Authentication authentication = authenticationManager.authenticate(dadosLogin);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = tokenService.gerarToken(authentication);
        
        return new TokenDTO(
                token,
                "Bearer",
                UsuarioDTO.fromEntity(usuario)
        );
    }
} 