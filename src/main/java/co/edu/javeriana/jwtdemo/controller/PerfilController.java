package co.edu.javeriana.jwtdemo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.jwtdemo.dto.UsuarioDTO;
import co.edu.javeriana.jwtdemo.service.UsuarioService;

/**
 * CON TOKEN: devuelve los datos del usuario dueño del token.
 */
@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

	private final UsuarioService usuarioService;

	public PerfilController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	/**
	 * Spring inyecta la Authentication que dejó JwtAuthenticationFilter;
	 * getName() es el "sub" del token.
	 */
	@GetMapping
	public UsuarioDTO miPerfil(Authentication autenticacion) {
		return usuarioService.buscarPorUsername(autenticacion.getName());
	}
}
