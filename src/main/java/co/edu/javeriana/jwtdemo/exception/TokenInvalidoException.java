package co.edu.javeriana.jwtdemo.exception;

/** El refresh token fue alterado, expiró o en realidad es un access token. */
public class TokenInvalidoException extends RuntimeException {

	public TokenInvalidoException(String mensaje) {
		super(mensaje);
	}
}
