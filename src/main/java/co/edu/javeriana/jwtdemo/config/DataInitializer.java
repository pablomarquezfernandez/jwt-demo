package co.edu.javeriana.jwtdemo.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import co.edu.javeriana.jwtdemo.entity.Producto;
import co.edu.javeriana.jwtdemo.entity.Rol;
import co.edu.javeriana.jwtdemo.entity.Usuario;
import co.edu.javeriana.jwtdemo.repository.ProductoRepository;
import co.edu.javeriana.jwtdemo.repository.UsuarioRepository;

/**
 * Carga datos de prueba al arrancar: los usuarios ana (USER) y admin (ADMIN) y algunos productos.
 */
@Component
public class DataInitializer implements CommandLineRunner {

	private final UsuarioRepository usuarioRepository;
	private final ProductoRepository productoRepository;
	private final PasswordEncoder passwordEncoder;

	public DataInitializer(UsuarioRepository usuarioRepository, ProductoRepository productoRepository,
			PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.productoRepository = productoRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		if (usuarioRepository.count() == 0) {
			usuarioRepository.save(new Usuario("ana", passwordEncoder.encode("ana123"), Rol.USER));
			usuarioRepository.save(new Usuario("admin", passwordEncoder.encode("admin123"), Rol.ADMIN));
		}
		if (productoRepository.count() == 0) {
			productoRepository.save(new Producto("Cuaderno", new BigDecimal("8500")));
			productoRepository.save(new Producto("Lápiz", new BigDecimal("1200")));
			productoRepository.save(new Producto("Calculadora", new BigDecimal("45000")));
		}
	}
}
