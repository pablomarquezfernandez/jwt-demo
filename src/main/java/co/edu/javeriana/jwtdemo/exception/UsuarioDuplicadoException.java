package co.edu.javeriana.jwtdemo.exception;

public class UsuarioDuplicadoException extends RuntimeException {

	public UsuarioDuplicadoException(String username) {
		super("El usuario '" + username + "' ya existe");
	}
}
