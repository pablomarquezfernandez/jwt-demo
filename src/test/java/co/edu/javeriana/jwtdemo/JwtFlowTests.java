package co.edu.javeriana.jwtdemo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class JwtFlowTests {

	private static final String PRODUCTO = "{\"nombre\":\"Borrador\",\"precio\":900}";

	@Autowired
	private MockMvc mockMvc;

	private String login(String usuario, String clave) throws Exception {
		String respuesta = mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"" + usuario + "\",\"password\":\"" + clave + "\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tipo").value("Bearer"))
				.andReturn().getResponse().getContentAsString();
		return JsonPath.read(respuesta, "$.token");
	}

	private static String bearer(String token) {
		return "Bearer " + token;
	}

	// --- Públicos ---

	@Test
	void endpointsPublicosNoPidenToken() throws Exception {
		mockMvc.perform(get("/api/publico/info")).andExpect(status().isOk());
		mockMvc.perform(get("/api/productos")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].nombre").exists());
	}

	@Test
	void registroYLoginDeUnUsuarioNuevo() throws Exception {
		mockMvc.perform(post("/auth/registro")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"carlos\",\"password\":\"carlos123\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.rol").value("USER"))
				.andExpect(jsonPath("$.password").doesNotExist());

		String token = login("carlos", "carlos123");
		mockMvc.perform(get("/api/perfil").header(HttpHeaders.AUTHORIZATION, bearer(token)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("carlos"));
	}

	@Test
	void registroDuplicadoResponde409() throws Exception {
		mockMvc.perform(post("/auth/registro")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"ana\",\"password\":\"otra123\"}"))
				.andExpect(status().isConflict());
	}

	@Test
	void credencialesIncorrectasResponde401() throws Exception {
		mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"ana\",\"password\":\"mala\"}"))
				.andExpect(status().isUnauthorized());
	}

	// --- Con token ---

	@Test
	void sinTokenResponde401() throws Exception {
		mockMvc.perform(get("/api/perfil")).andExpect(status().isUnauthorized());
		mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(PRODUCTO))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void conTokenValidoDevuelveElPerfil() throws Exception {
		String token = login("ana", "ana123");

		mockMvc.perform(get("/api/perfil").header(HttpHeaders.AUTHORIZATION, bearer(token)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("ana"))
				.andExpect(jsonPath("$.rol").value("USER"));
	}

	@Test
	void conTokenSePuedeCrearUnProducto() throws Exception {
		String token = login("ana", "ana123");

		mockMvc.perform(post("/api/productos").header(HttpHeaders.AUTHORIZATION, bearer(token))
				.contentType(MediaType.APPLICATION_JSON).content(PRODUCTO))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists());
	}

	@Test
	void tokenAlteradoResponde401() throws Exception {
		String token = login("ana", "ana123");
		// Cambiar un carácter de la firma basta para que el token se rechace. Se cambia el primero:
		// los últimos bits del último carácter Base64URL se descartan y el cambio podría no notarse.
		int inicioFirma = token.lastIndexOf('.') + 1;
		char primero = token.charAt(inicioFirma);
		String alterado = token.substring(0, inicioFirma) + (primero == 'A' ? 'B' : 'A')
				+ token.substring(inicioFirma + 1);

		mockMvc.perform(get("/api/perfil").header(HttpHeaders.AUTHORIZATION, bearer(alterado)))
				.andExpect(status().isUnauthorized());
	}

	// --- Con token y rol ADMIN ---

	@Test
	void usuarioSinRolAdminResponde403() throws Exception {
		String token = login("ana", "ana123");

		mockMvc.perform(get("/api/admin/usuarios").header(HttpHeaders.AUTHORIZATION, bearer(token)))
				.andExpect(status().isForbidden());
		mockMvc.perform(delete("/api/productos/1").header(HttpHeaders.AUTHORIZATION, bearer(token)))
				.andExpect(status().isForbidden());
	}

	@Test
	void adminPuedeListarUsuariosYEliminarProductos() throws Exception {
		String token = login("admin", "admin123");

		mockMvc.perform(get("/api/admin/usuarios").header(HttpHeaders.AUTHORIZATION, bearer(token)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].password").doesNotExist());

		String creado = mockMvc.perform(post("/api/productos").header(HttpHeaders.AUTHORIZATION, bearer(token))
				.contentType(MediaType.APPLICATION_JSON).content(PRODUCTO))
				.andReturn().getResponse().getContentAsString();
		Integer id = JsonPath.read(creado, "$.id");

		mockMvc.perform(delete("/api/productos/" + id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
				.andExpect(status().isNoContent());
	}
}
