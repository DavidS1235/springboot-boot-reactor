package com.ds.springboot.reactor.app.models;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class UsuarioComentario {

    private Usuario usuario;
    private Comentarios comentarios;

}
