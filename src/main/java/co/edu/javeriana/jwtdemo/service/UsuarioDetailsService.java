package co.edu.javeriana.jwtdemo.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.edu.javeriana.jwtdemo.entity.Usuario;
import co.edu.javeriana.jwtdemo.repository.UsuarioRepository;

/**
 * Le dice a Spring Security cómo buscar un usuario en la base de datos durante el login.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		Usuario usuario = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

		return User.withUsername(usuario.getUsername())
				.password(usuario.getPassword())
				.roles(usuario.getRol().name())
				.build();
	}
}
