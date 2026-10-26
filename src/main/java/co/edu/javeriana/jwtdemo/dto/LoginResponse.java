package co.edu.javeriana.jwtdemo.dto;

public record LoginResponse(String token, String tipo, long expiraEnSegundos) {
}
