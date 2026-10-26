package co.edu.javeriana.jwtdemo.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Se ejecuta en cada petición: si trae "Authorization: Bearer &lt;token&gt;" y el token es válido,
 * deja al usuario autenticado para esa petición. No hay sesión: el token se revisa siempre.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String PREFIJO = "Bearer ";

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header == null || !header.startsWith(PREFIJO)) {
			chain.doFilter(request, response);
			return;
		}

		try {
			Claims claims = jwtService.validarToken(header.substring(PREFIJO.length()));
			// Un refresh token no autentica peticiones: solo se canjea en /auth/refresh (y no trae roles)
			if (jwtService.esRefreshToken(claims)) {
				throw new JwtException("Se envió un refresh token como access token");
			}
			List<SimpleGrantedAuthority> roles = ((List<?>) claims.get("roles", List.class)).stream()
					.map(rol -> new SimpleGrantedAuthority(rol.toString()))
					.toList();

			var autenticacion = UsernamePasswordAuthenticationToken.authenticated(claims.getSubject(), null, roles);
			SecurityContextHolder.getContext().setAuthentication(autenticacion);
		} catch (JwtException | IllegalArgumentException e) {
			// Token alterado, expirado o mal formado: la petición sigue sin usuario y Spring Security responde 401
			SecurityContextHolder.clearContext();
		}

		chain.doFilter(request, response);
	}
}
