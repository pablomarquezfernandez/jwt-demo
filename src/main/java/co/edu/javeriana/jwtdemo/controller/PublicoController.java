package co.edu.javeriana.jwtdemo.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PÚBLICO: cualquiera puede entrar, con o sin token.
 */
@RestController
@RequestMapping("/api/publico")
public class PublicoController {

	@GetMapping("/info")
	public Map<String, Object> info() {
		return Map.of(
				"mensaje", "Este endpoint es público: no necesita token",
				"fecha", Instant.now().toString());
	}
}
