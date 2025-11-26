package br.com.fabreum.authservice.controller.dto;

import br.com.fabreum.authservice.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroRequest {

    private Long id;
    private String nome;
    private String email;
    private String password;
    private Role role;
}
