package co.edu.javeriana.jwtdemo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.jwtdemo.dto.LoginRequest;
import co.edu.javeriana.jwtdemo.dto.LoginResponse;
import co.edu.javeriana.jwtdemo.dto.RefreshRequest;
import co.edu.javeriana.jwtdemo.dto.RegistroRequest;
import co.edu.javeriana.jwtdemo.dto.UsuarioDTO;
import co.edu.javeriana.jwtdemo.service.AuthService;
import jakarta.validation.Valid;

/**
 * PÚBLICO: registro, login y refresh no piden access token (es justo donde se obtiene).
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/registro")
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioDTO registrar(@Valid @RequestBody RegistroRequest request) {
		return authService.registrar(request);
	}

	/** Devuelve el JWT que el cliente enviará luego en el header Authorization. */
	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	/** Recibe el refresh token y devuelve tokens nuevos sin pedir otra vez la contraseña. */
	@PostMapping("/refresh")
	public LoginResponse refrescar(@Valid @RequestBody RefreshRequest request) {
		return authService.refrescar(request.refreshToken());
	}
}
