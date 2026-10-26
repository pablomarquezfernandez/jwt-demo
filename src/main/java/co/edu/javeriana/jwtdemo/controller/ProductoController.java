package co.edu.javeriana.jwtdemo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.jwtdemo.dto.ProductoDTO;
import co.edu.javeriana.jwtdemo.dto.ProductoRequest;
import co.edu.javeriana.jwtdemo.service.ProductoService;
import jakarta.validation.Valid;

/**
 * Un mismo recurso con distintos niveles de acceso (las reglas están en SecurityConfig):
 * leer es público, crear pide token y eliminar pide token con rol ADMIN.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

	private final ProductoService productoService;

	public ProductoController(ProductoService productoService) {
		this.productoService = productoService;
	}

	/** PÚBLICO */
	@GetMapping
	public List<ProductoDTO> listar() {
		return productoService.listar();
	}

	/** PÚBLICO */
	@GetMapping("/{id}")
	public ProductoDTO obtener(@PathVariable Long id) {
		return productoService.obtener(id);
	}

	/** CON TOKEN: cualquier usuario autenticado */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductoDTO crear(@Valid @RequestBody ProductoRequest request) {
		return productoService.crear(request);
	}

	/** CON TOKEN Y ROL ADMIN */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable Long id) {
		productoService.eliminar(id);
	}
}
