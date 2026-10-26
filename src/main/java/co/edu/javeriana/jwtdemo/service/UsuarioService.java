package co.edu.javeriana.jwtdemo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.javeriana.jwtdemo.dto.UsuarioDTO;
import co.edu.javeriana.jwtdemo.exception.RecursoNoEncontradoException;
import co.edu.javeriana.jwtdemo.repository.UsuarioRepository;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public UsuarioDTO buscarPorUsername(String username) {
		return usuarioRepository.findByUsername(username)
				.map(UsuarioDTO::desde)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + username));
	}

	public List<UsuarioDTO> listar() {
		return usuarioRepository.findAll().stream().map(UsuarioDTO::desde).toList();
	}
}
