# jwt-demo

Ejemplo de autenticación con **JWT** en **Spring Boot 4.1** (Java 17, Spring Security, Spring Data JPA con H2 y [JJWT](https://github.com/jwtk/jjwt) 0.12.6).
Acompaña la presentación «¿Qué es JWT?».

## Cómo ejecutarlo

```bash
./mvnw spring-boot:run     # levanta la API en http://localhost:8080
./mvnw test                # corre las pruebas del flujo completo
```

La base de datos es H2 en memoria: se crea al arrancar y [DataInitializer](src/main/java/co/edu/javeriana/jwtdemo/config/DataInitializer.java) carga estos usuarios y tres productos.

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| `ana`   | `ana123`   | USER |
| `admin` | `admin123` | ADMIN |

## Endpoints

| Método | Ruta | Acceso |
|--------|------|--------|
| POST | `/auth/registro` | **Público**: crea un usuario con rol USER |
| POST | `/auth/login` | **Público**: valida usuario y contraseña y **devuelve el JWT** |
| GET | `/api/publico/info` | **Público** |
| GET | `/api/productos`, `/api/productos/{id}` | **Público** |
| POST | `/api/productos` | **Con token** |
| GET | `/api/perfil` | **Con token**: datos del dueño del token |
| DELETE | `/api/productos/{id}` | **Con token y rol ADMIN** |
| GET | `/api/admin/usuarios` | **Con token y rol ADMIN** |

Sin token o con un token inválido la respuesta es **401**; con token pero sin el rol necesario, **403**.
Las reglas están todas juntas en [SecurityConfig](src/main/java/co/edu/javeriana/jwtdemo/config/SecurityConfig.java).

## Probarlo con curl

```bash
# 1. Login: obtener el token
TOKEN=$(curl -s -X POST localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"ana","password":"ana123"}' | sed 's/.*"token":"\([^"]*\)".*/\1/')

# 2. Usar el token en cada petición
curl localhost:8080/api/publico/info                                     # 200: público
curl localhost:8080/api/perfil -H "Authorization: Bearer $TOKEN"         # 200: con token
curl localhost:8080/api/perfil                                           # 401: sin token
curl localhost:8080/api/admin/usuarios -H "Authorization: Bearer $TOKEN" # 403: ana no es ADMIN
```

Pega el token en [jwt.io](https://jwt.io) para ver su header y payload. También puedes usar [peticiones.http](peticiones.http) con la extensión REST Client de VS Code.

## Arquitectura

```
co.edu.javeriana.jwtdemo
├── controller   AuthController, PublicoController, ProductoController, PerfilController, AdminController
├── service      AuthService, UsuarioService, ProductoService, UsuarioDetailsService
├── repository   UsuarioRepository, ProductoRepository
├── entity       Usuario, Producto, Rol
├── dto          LoginRequest, LoginResponse, RegistroRequest, UsuarioDTO, ProductoDTO, ProductoRequest
├── security     JwtService, JwtAuthenticationFilter
├── config       SecurityConfig, DataInitializer
└── exception    GlobalExceptionHandler y excepciones propias
```

Petición → **filtro JWT** → controller → service → repository → entity (base de datos). Los controladores reciben y devuelven DTO, nunca entidades: así la contraseña no sale de la API.

## Dónde pasa cada cosa

| Qué | Dónde |
|-----|-------|
| **Se genera el token** | [JwtService.generarToken](src/main/java/co/edu/javeriana/jwtdemo/security/JwtService.java), llamado desde [AuthService.login](src/main/java/co/edu/javeriana/jwtdemo/service/AuthService.java) (busca `AQUÍ SE GENERA EL TOKEN`) |
| **Se valida el token** | [JwtService.validarToken](src/main/java/co/edu/javeriana/jwtdemo/security/JwtService.java) |
| **Se lee el token en cada petición** | [JwtAuthenticationFilter](src/main/java/co/edu/javeriana/jwtdemo/security/JwtAuthenticationFilter.java) |
| **Se registra el filtro y las reglas de acceso** | [SecurityConfig](src/main/java/co/edu/javeriana/jwtdemo/config/SecurityConfig.java) (busca `AQUÍ SE REGISTRA EL FILTRO JWT`) |
| **Se busca al usuario en la base de datos al hacer login** | [UsuarioDetailsService](src/main/java/co/edu/javeriana/jwtdemo/service/UsuarioDetailsService.java) |
| **Pruebas del flujo completo** | [JwtFlowTests](src/test/java/co/edu/javeriana/jwtdemo/JwtFlowTests.java): públicos, registro, login, 401, 403 y token alterado |

## Ojo: es una demo

- La clave de firma por defecto está en `application.properties` solo para que el ejemplo arranque. En un entorno real se pasa por la variable `JWT_SECRET` (genera una con `openssl rand -base64 32`).
- No incluye refresh tokens: cuando el token expira (15 minutos) hay que volver a hacer login.
