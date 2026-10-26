package co.edu.javeriana.jwtdemo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductoRequest(@NotBlank String nombre, @NotNull @Positive BigDecimal precio) {
}
