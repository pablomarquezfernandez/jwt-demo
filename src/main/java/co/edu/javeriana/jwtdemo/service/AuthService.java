package co.edu.javeriana.jwtdemo.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.javeriana.jwtdemo.dto.LoginRequest;
import co.edu.javeriana.jwtdemo.dto.LoginResponse;
import co.edu.javeriana.jwtdemo.dto.RegistroRequest;
import co.edu.javeriana.jwtdemo.dto.UsuarioDTO;
import co.edu.javeriana.jwtdemo.entity.Rol;
import co.edu.javeriana.jwtdemo.entity.Usuario;
import co.edu.javeriana.jwtdemo.exception.UsuarioDuplicadoException;
import co.edu.javeriana.jwtdemo.repository.UsuarioRepository;
import co.edu.javeriana.jwtdemo.security.JwtService;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthService(AuthenticationManager authenticationManager, JwtService jwtService,
			UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Pasos 1 y 2 del flujo: valida usuario y contraseña contra la base de datos y entrega el JWT.
	 */
	public LoginResponse login(LoginRequest request) {
		// 1. Spring Security busca al usuario (UsuarioDetailsService) y compara la contraseña con el hash BCrypt.
		//    Si no coincide lanza AuthenticationException y GlobalExceptionHandler responde 401.
		Authentication autenticacion = authenticationManager.authenticate(
				UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));

		// 2. >>> AQUÍ SE GENERA EL TOKEN <<< (ver JwtService.generarToken)
		String token = jwtService.generarToken((UserDetails) autenticacion.getPrincipal());

		return new LoginResponse(token, "Bearer", jwtService.getExpiracionEnSegundos());
	}

	/**
	 * Crea un usuario nuevo con rol USER. Después debe hacer login para obtener su token.
	 */
	@Transactional
	public UsuarioDTO registrar(RegistroRequest request) {
		if (usuarioRepository.existsByUsername(request.username())) {
			throw new UsuarioDuplicadoException(request.username());
		}
		Usuario usuario = new Usuario(request.username(), passwordEncoder.encode(request.password()), Rol.USER);
		return UsuarioDTO.desde(usuarioRepository.save(usuario));
	}
}
