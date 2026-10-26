package co.edu.javeriana.jwtdemo.dto;

import java.math.BigDecimal;

import co.edu.javeriana.jwtdemo.entity.Producto;

public record ProductoDTO(Long id, String nombre, BigDecimal precio) {

	public static ProductoDTO desde(Producto producto) {
		return new ProductoDTO(producto.getId(), producto.getNombre(), producto.getPrecio());
	}
}
