package br.com.fabreum.authservice.service;

import br.com.fabreum.authservice.controller.dto.RegistroRequest;
import br.com.fabreum.authservice.model.Role;
import br.com.fabreum.authservice.model.Usuario;
import br.com.fabreum.authservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(RegistroRequest registroRequest) {
        if (usuarioRepository.findByUsername(registroRequest.getUsername()).isPresent()) {
            throw new IllegalStateException("Erro: Nome de usuário já está em uso!");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(registroRequest.getUsername());
        usuario.setPassword(passwordEncoder.encode(registroRequest.getPassword()));
        usuario.setRoles(Set.of(Role.ROLE_CUSTOMER)); // Default role

        return usuarioRepository.save(usuario);
    }
}
