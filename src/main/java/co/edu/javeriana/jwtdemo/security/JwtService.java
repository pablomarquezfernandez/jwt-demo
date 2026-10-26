package co.edu.javeriana.jwtdemo.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Crea y valida los JWT. Es el único lugar que conoce la clave de firma.
 */
@Service
public class JwtService {

	private static final String EMISOR = "jwt-demo";
	private static final String CLAIM_TIPO = "tipo";
	private static final String TIPO_REFRESH = "refresh";

	private final SecretKey clave;
	private final Duration expiracion;
	private final Duration expiracionRefresh;

	public JwtService(@Value("${jwt.secret}") String secreto,
			@Value("${jwt.expiration-minutes}") long minutos,
			@Value("${jwt.refresh-expiration-days}") long diasRefresh) {
		this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto));
		this.expiracion = Duration.ofMinutes(minutos);
		this.expiracionRefresh = Duration.ofDays(diasRefresh);
	}

	/**
	 * >>> AQUÍ SE GENERA EL TOKEN <<< (lo llama AuthService.login después de validar la contraseña)
	 * Header: {"alg":"HS256"}; payload: sub, roles, iss, iat y exp; firma: HMAC-SHA256 con la clave.
	 */
	public String generarToken(UserDetails usuario) {
		Instant ahora = Instant.now();
		List<String> roles = usuario.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.toList();

		return Jwts.builder()
				.subject(usuario.getUsername())
				.claim("roles", roles)
				.issuer(EMISOR)
				.issuedAt(Date.from(ahora))
				.expiration(Date.from(ahora.plus(expiracion)))
				.signWith(clave, Jwts.SIG.HS256)
				.compact();
	}

	/**
	 * >>> AQUÍ SE GENERA EL REFRESH TOKEN <<< Misma clave y emisor, pero dura días, no lleva roles
	 * y se marca con tipo=refresh: solo sirve para pedir un access token nuevo en /auth/refresh.
	 */
	public String generarRefreshToken(UserDetails usuario) {
		Instant ahora = Instant.now();

		return Jwts.builder()
				.subject(usuario.getUsername())
				.claim(CLAIM_TIPO, TIPO_REFRESH)
				.issuer(EMISOR)
				.issuedAt(Date.from(ahora))
				.expiration(Date.from(ahora.plus(expiracionRefresh)))
				.signWith(clave, Jwts.SIG.HS256)
				.compact();
	}

	/**
	 * Verifica la firma, la expiración y el emisor, y devuelve el payload.
	 *
	 * @throws JwtException si el token fue alterado, expiró o no es nuestro
	 */
	public Claims validarToken(String token) {
		return Jwts.parser()
				.verifyWith(clave)
				.requireIssuer(EMISOR)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public boolean esRefreshToken(Claims claims) {
		return TIPO_REFRESH.equals(claims.get(CLAIM_TIPO, String.class));
	}

	public long getExpiracionEnSegundos() {
		return expiracion.toSeconds();
	}
}
