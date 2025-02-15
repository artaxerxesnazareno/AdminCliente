package org.example.admincliente.controllers;

import java.util.ArrayList;
import java.util.List;

import org.example.admincliente.dtos.UsuarioAtualizacaoDTO;
import org.example.admincliente.dtos.UsuarioDTO;
import org.example.admincliente.enums.TipoUsuario;
import org.example.admincliente.exceptions.BusinessException;
import org.example.admincliente.services.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/cazio/utilizadores")
public class UtilizadorController {

    private static final Logger logger = LoggerFactory.getLogger(UtilizadorController.class);

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/todos-utilizadores")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public String listarUtilizadores(Model model) {
        try {
            logger.info("Iniciando carregamento da lista de utilizadores");
            
            // Lista todos os tipos de usuários
            List<UsuarioDTO> todosUsuarios = new ArrayList<>();
            
            // Se for SUPERADMIN, inclui admins também
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"))) {
                todosUsuarios.addAll(usuarioService.listarTodosAdmins());
            }
            
            // Adiciona os clientes
            todosUsuarios.addAll(usuarioService.listarTodosClientes());
            
            model.addAttribute("utilizadores", todosUsuarios);
            logger.info("Lista de utilizadores carregada com sucesso. Total: {}", todosUsuarios.size());
            return "cazio/utilizadores/todos-utilizadores";
        } catch (Exception e) {
            logger.error("Erro ao carregar lista de utilizadores", e);
            throw e;
        }
    }

    @GetMapping({"/perfil-do-utilizador", "/perfil-do-utilizador/{id}"})
    public String perfilUtilizador(@PathVariable(required = false) Long id, Model model, Authentication authentication) {
        try {
            logger.info("Iniciando carregamento do perfil do utilizador");
            
            UsuarioDTO usuario;
            if (id != null) {
                // Se um ID foi fornecido, busca o usuário específico
                logger.info("Buscando usuário com ID: {}", id);
                usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            } else {
                // Caso contrário, busca o usuário autenticado
                String email = authentication.getName();
                logger.info("Buscando usuário autenticado com email: {}", email);
                usuario = usuarioService.buscarPorEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            }
            
            logger.info("Usuário encontrado: {}", usuario);
            
            // Adiciona os dados do usuário ao modelo
            model.addAttribute("id", usuario.getId());
            model.addAttribute("nome", usuario.getNome());
            model.addAttribute("email", usuario.getEmail());
            model.addAttribute("telefone", usuario.getTelefone());
            model.addAttribute("tipo", usuario.getTipo().toString());
            model.addAttribute("dataCriacao", usuario.getDataCriacao());
            model.addAttribute("imagem", usuario.getImagem());
            
            logger.info("Dados adicionados ao modelo com sucesso");
            return "cazio/utilizadores/perfil-do-utilizador";
        } catch (Exception e) {
            logger.error("Erro ao carregar perfil do utilizador", e);
            throw e;
        }
    }

    @GetMapping("/criar-utilizador")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public String exibirFormularioCriarUtilizador(Model model) {
        try {
            logger.info("Exibindo formulário de criação de utilizador");
            model.addAttribute("usuarioDTO", new UsuarioDTO());
            return "cazio/utilizadores/criar-utilizador";
        } catch (Exception e) {
            logger.error("Erro ao exibir formulário de criação de utilizador", e);
            throw e;
        }
    }

    @PostMapping("/criar-utilizador")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public String criarUtilizador(@ModelAttribute UsuarioDTO usuarioDTO, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Iniciando criação de novo utilizador: {}", usuarioDTO.getEmail());
            usuarioService.criarUsuario(usuarioDTO);
            logger.info("Utilizador criado com sucesso: {}", usuarioDTO.getEmail());
            redirectAttributes.addFlashAttribute("mensagem", "Utilizador criado com sucesso!");
            return "redirect:/cazio/utilizadores/todos-utilizadores";
        } catch (Exception e) {
            logger.error("Erro ao criar utilizador", e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar utilizador: " + e.getMessage());
            return "redirect:/cazio/utilizadores/criar-utilizador";
        }
    }

    @GetMapping("/editar-utilizador/{id}")
    public String exibirFormularioEditarUtilizador(@PathVariable Long id, Model model, Authentication authentication) {
        try {
            logger.info("Exibindo formulário de edição para o usuário ID: {}", id);
            
            // Busca o usuário logado
            UsuarioDTO usuarioLogado = usuarioService.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Se for o próprio usuário tentando editar seu perfil, redireciona para a página correta
            if (usuarioLogado.getId().equals(id)) {
                return "redirect:/cazio/utilizadores/editar-perfil";
            }
            
            // Se não for ADMIN nem SUPERADMIN, não pode editar outros usuários
            if (!authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                                 a.getAuthority().equals("ROLE_SUPERADMIN"))) {
                throw new BusinessException("Você não tem permissão para editar outros usuários");
            }
            
            UsuarioDTO usuarioParaEditar = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Se for ADMIN tentando editar outro ADMIN ou SUPERADMIN
            if (usuarioLogado.getTipo() == TipoUsuario.ADMIN && 
                (usuarioParaEditar.getTipo() == TipoUsuario.ADMIN || 
                 usuarioParaEditar.getTipo() == TipoUsuario.SUPERADMIN)) {
                throw new BusinessException("Você não tem permissão para editar este usuário");
            }
            
            model.addAttribute("usuarioDTO", usuarioParaEditar);
            return "cazio/utilizadores/editar-utilizador";
        } catch (Exception e) {
            logger.error("Erro ao exibir formulário de edição", e);
            throw e;
        }
    }

    @GetMapping("/editar-perfil")
    public String exibirFormularioEditarPerfil(Model model, Authentication authentication) {
        try {
            logger.info("Exibindo formulário de edição de perfil próprio");
            
            UsuarioDTO usuario = usuarioService.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            model.addAttribute("usuarioDTO", usuario);
            return "cazio/utilizadores/editar-perfil";
        } catch (Exception e) {
            logger.error("Erro ao exibir formulário de edição de perfil", e);
            throw e;
        }
    }

    @PostMapping("/editar-utilizador/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public String atualizarUtilizador(@PathVariable Long id, @Valid @ModelAttribute UsuarioAtualizacaoDTO atualizacaoDTO, 
                                     RedirectAttributes redirectAttributes, Authentication authentication) {
        try {
            logger.info("Atualizando usuário ID: {}", id);
            
            // Verifica permissões
            UsuarioDTO usuarioLogado = usuarioService.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                
            if (usuarioLogado.getTipo() == TipoUsuario.ADMIN && atualizacaoDTO.getTipo() == TipoUsuario.ADMIN) {
                throw new BusinessException("ADMIN não pode editar outros ADMINs");
            }
            
            usuarioService.atualizarUsuario(id, atualizacaoDTO);
            redirectAttributes.addFlashAttribute("mensagem", "Usuário atualizado com sucesso!");
            return "redirect:/cazio/utilizadores/todos-utilizadores";
        } catch (Exception e) {
            logger.error("Erro ao atualizar usuário", e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar usuário: " + e.getMessage());
            return "redirect:/cazio/utilizadores/editar-utilizador/" + id;
        }
    }

    @PostMapping("/editar-perfil")
    public String atualizarPerfil(@Valid @ModelAttribute UsuarioAtualizacaoDTO atualizacaoDTO, 
                                 RedirectAttributes redirectAttributes, 
                                 Authentication authentication) {
        try {
            logger.info("Atualizando perfil próprio");
            
            UsuarioDTO usuarioAtual = usuarioService.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            atualizacaoDTO.setId(usuarioAtual.getId());
            atualizacaoDTO.setTipo(usuarioAtual.getTipo()); // Mantém o tipo original
            
            usuarioService.atualizarProprioPerfil(usuarioAtual.getId(), atualizacaoDTO);
            redirectAttributes.addFlashAttribute("mensagem", "Perfil atualizado com sucesso!");
            return "redirect:/cazio/utilizadores/perfil-do-utilizador";
        } catch (Exception e) {
            logger.error("Erro ao atualizar perfil", e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar perfil: " + e.getMessage());
            return "redirect:/cazio/utilizadores/editar-perfil";
        }
    }

    @GetMapping("/excluir/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public String excluirUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Iniciando exclusão do usuário ID: {}", id);
            
            // Busca o usuário para verificar o tipo
            UsuarioDTO usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Se for ADMIN, apenas SUPERADMIN pode excluir
            if (TipoUsuario.ADMIN.equals(usuario.getTipo())) {
                usuarioService.deletarAdmin(id);
            } else {
                usuarioService.deletarCliente(id);
            }
            
            redirectAttributes.addFlashAttribute("mensagem", "Usuário excluído com sucesso!");
            return "redirect:/cazio/utilizadores/todos-utilizadores";
        } catch (Exception e) {
            logger.error("Erro ao excluir usuário", e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir usuário: " + e.getMessage());
            return "redirect:/cazio/utilizadores/todos-utilizadores";
        }
    }
} 