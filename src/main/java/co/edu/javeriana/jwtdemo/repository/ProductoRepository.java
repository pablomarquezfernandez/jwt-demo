package co.edu.javeriana.jwtdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.javeriana.jwtdemo.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
