package co.edu.javeriana.jwtdemo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.javeriana.jwtdemo.dto.ProductoDTO;
import co.edu.javeriana.jwtdemo.dto.ProductoRequest;
import co.edu.javeriana.jwtdemo.entity.Producto;
import co.edu.javeriana.jwtdemo.exception.RecursoNoEncontradoException;
import co.edu.javeriana.jwtdemo.repository.ProductoRepository;

@Service
@Transactional(readOnly = true)
public class ProductoService {

	private final ProductoRepository productoRepository;

	public ProductoService(ProductoRepository productoRepository) {
		this.productoRepository = productoRepository;
	}

	public List<ProductoDTO> listar() {
		return productoRepository.findAll().stream().map(ProductoDTO::desde).toList();
	}

	public ProductoDTO obtener(Long id) {
		return productoRepository.findById(id)
				.map(ProductoDTO::desde)
				.orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
	}

	@Transactional
	public ProductoDTO crear(ProductoRequest request) {
		Producto producto = productoRepository.save(new Producto(request.nombre(), request.precio()));
		return ProductoDTO.desde(producto);
	}

	@Transactional
	public void eliminar(Long id) {
		if (!productoRepository.existsById(id)) {
			throw new RecursoNoEncontradoException("Producto no encontrado: " + id);
		}
		productoRepository.deleteById(id);
	}
}
