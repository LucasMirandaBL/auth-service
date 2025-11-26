package br.com.fabreum.authservice.service;

import br.com.fabreum.authservice.controller.dto.RegistroRequest;
import br.com.fabreum.authservice.model.Role;
import br.com.fabreum.authservice.model.Usuario;
import br.com.fabreum.authservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(RegistroRequest registroRequest) {
        if (usuarioRepository.findByEmail(registroRequest.getEmail()).isPresent()) {
            throw new IllegalStateException("Erro: Email já está em uso!");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(registroRequest.getNome());
        usuario.setEmail(registroRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroRequest.getPassword()));
        usuario.setRoles(Set.of(registroRequest.getRole()));

        return usuarioRepository.save(usuario);
    }

    public RegistroRequest findByEmail(String email) {

        Usuario user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Erro: Usuário não encontrado"));

        RegistroRequest request = new RegistroRequest();
        request.setId(user.getId());
        request.setNome(user.getNome());
        request.setEmail(user.getEmail());

        return request;
    }

}
