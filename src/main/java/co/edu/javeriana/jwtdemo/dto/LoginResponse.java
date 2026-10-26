package co.edu.javeriana.jwtdemo.dto;

/**
 * token: access token corto que va en el header Authorization.
 * refreshToken: token largo que solo se envía a /auth/refresh para pedir un token nuevo.
 */
public record LoginResponse(String token, String refreshToken, String tipo, long expiraEnSegundos) {
}
