package co.edu.javeriana.jwtdemo.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Map<String, String>> credencialesInvalidas() {
		return error(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
	}

	@ExceptionHandler(UsuarioDuplicadoException.class)
	public ResponseEntity<Map<String, String>> usuarioDuplicado(UsuarioDuplicadoException e) {
		return error(HttpStatus.CONFLICT, e.getMessage());
	}

	@ExceptionHandler(RecursoNoEncontradoException.class)
	public ResponseEntity<Map<String, String>> noEncontrado(RecursoNoEncontradoException e) {
		return error(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> datosInvalidos(MethodArgumentNotValidException e) {
		String detalle = e.getBindingResult().getFieldErrors().stream()
				.map(campo -> campo.getField() + ": " + campo.getDefaultMessage())
				.findFirst()
				.orElse("Datos inválidos");
		return error(HttpStatus.BAD_REQUEST, detalle);
	}

	private ResponseEntity<Map<String, String>> error(HttpStatus estado, String mensaje) {
		return ResponseEntity.status(estado).body(Map.of("error", mensaje));
	}
}
