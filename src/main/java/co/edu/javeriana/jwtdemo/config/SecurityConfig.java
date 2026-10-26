package co.edu.javeriana.jwtdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import co.edu.javeriana.jwtdemo.security.JwtAuthenticationFilter;
import co.edu.javeriana.jwtdemo.security.JwtService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService) throws Exception {
		http
				// Sin cookies de sesión no hay CSRF que proteger
				.csrf(AbstractHttpConfigurer::disable)
				// Sin estado: el servidor no guarda sesiones, cada petición trae su token
				.sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						// PÚBLICOS: no necesitan token
						.requestMatchers(HttpMethod.POST, "/auth/login", "/auth/registro").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/publico/**", "/api/productos/**").permitAll()
						.requestMatchers("/error").permitAll()
						// SOLO ADMIN: token con rol ADMIN
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
						// CON TOKEN: todo lo demás
						.anyRequest().authenticated())
				// Sin token válido: 401 (con token pero sin el rol: 403)
				.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				// >>> AQUÍ SE REGISTRA EL FILTRO JWT <<< corre antes del filtro de usuario y contraseña
				.addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}


	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Usado solo en el login: busca al usuario con UsuarioDetailsService y compara la contraseña con BCrypt.
	 */
	@Bean
	AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider proveedor = new DaoAuthenticationProvider(userDetailsService);
		proveedor.setPasswordEncoder(passwordEncoder);
		return new ProviderManager(proveedor);
	}
}
