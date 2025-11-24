package br.com.fabreum.authservice.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroRequest {
    private String username;
    private String password;
}
