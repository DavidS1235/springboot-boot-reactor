package com.ds.springboot.reactor.app.models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class Usuario {
    private String nombre;
    private String apellido;
}
