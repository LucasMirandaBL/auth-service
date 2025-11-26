package br.com.fabreum.authservice.controller;

import br.com.fabreum.authservice.controller.dto.JwtAuthenticationResponse;
import br.com.fabreum.authservice.controller.dto.LoginRequest;
import br.com.fabreum.authservice.controller.dto.RefreshTokenRequest;
import br.com.fabreum.authservice.controller.dto.RegistroRequest;
import br.com.fabreum.authservice.model.Usuario;
import br.com.fabreum.authservice.security.JwtTokenProvider;
import br.com.fabreum.authservice.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioService usuarioService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistroRequest registroRequest) {
        try {
            Usuario usuario = usuarioService.registrarUsuario(registroRequest);
            usuario.setPassword(null);
            return ResponseEntity.ok(usuario);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (tokenProvider.validateToken(refreshToken)) {
            String username = tokenProvider.getUsernameFromJWT(refreshToken);
            String newAccessToken = tokenProvider.generateTokenFromUsername(username);
            return ResponseEntity.ok(new JwtAuthenticationResponse(newAccessToken, refreshToken));
        } else {
            return ResponseEntity.badRequest().body("Invalid Refresh Token");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
            Usuario currentUser = (Usuario) authentication.getPrincipal();
            currentUser.setPassword(null);
            return ResponseEntity.ok(currentUser);
        }
        return ResponseEntity.status(401).body("Usuário não autenticado.");
    }

    @GetMapping("/users/by-username/{username}")
    public ResponseEntity<RegistroRequest> getUserByEmail(@PathVariable String username) {
        RegistroRequest user = usuarioService.findByEmail(username);

      return ResponseEntity.ok(user);
    }



}
