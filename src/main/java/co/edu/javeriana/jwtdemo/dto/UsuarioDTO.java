package co.edu.javeriana.jwtdemo.dto;

import co.edu.javeriana.jwtdemo.entity.Rol;
import co.edu.javeriana.jwtdemo.entity.Usuario;

/** Lo que se expone de un usuario: nunca la contraseña. */
public record UsuarioDTO(Long id, String username, Rol rol) {

	public static UsuarioDTO desde(Usuario usuario) {
		return new UsuarioDTO(usuario.getId(), usuario.getUsername(), usuario.getRol());
	}
}
