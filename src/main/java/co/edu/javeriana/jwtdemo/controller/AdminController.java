package co.edu.javeriana.jwtdemo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.jwtdemo.dto.UsuarioDTO;
import co.edu.javeriana.jwtdemo.service.UsuarioService;

/**
 * CON TOKEN Y ROL ADMIN: todo lo que está bajo /api/admin.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

	private final UsuarioService usuarioService;

	public AdminController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping("/usuarios")
	public List<UsuarioDTO> listarUsuarios() {
		return usuarioService.listar();
	}
}
