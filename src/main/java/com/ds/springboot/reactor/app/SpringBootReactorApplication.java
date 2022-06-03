package com.ds.springboot.reactor.app;

import com.ds.springboot.reactor.app.models.Comentarios;
import com.ds.springboot.reactor.app.models.Usuario;
import com.ds.springboot.reactor.app.models.UsuarioComentario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

	private static Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		//ejemploIterable();
		//ejemploFlatMap();
		//ejemploToString();
		//ejemploCollectList();
		ejemploUsuarioComentariosFlatMap();

	}

	//Combinaremos El usuario con comentario y lo devolveremos en un flujo
	public void ejemploUsuarioComentariosFlatMap(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("John", "Doe"));
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(()-> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola David");
			comentarios.addComentario("Hace frio");
			return comentarios;
		});

		usuarioMono.flatMap(u -> comentariosUsuarioMono.map(c -> new UsuarioComentario(u,c)))
				.subscribe(uc -> log.info(uc.toString()));
	}

	public void ejemploCollectList() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Andres", "Guzman"));
		usuariosList.add(new Usuario("David"," Silva"));
		usuariosList.add(new Usuario("Paula", "Solis"));
		usuariosList.add(new Usuario("Andres", "Sanchez"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		usuariosList.add(new Usuario("Bruce", "Willis"));

		Flux.fromIterable(usuariosList)
				.collectList() // Convierte a MONO
				//.subscribe(lista -> log.info(lista.toString())); // Resultado: [Usuario(nombre=Andres, apellido=Guzman), Usuario(nombre=David, apellido= Silva), etc
				.subscribe(lista ->{
					lista.forEach(item -> log.info(item.toString())); // Uso de API Stream de JAVA (forEach), devuelve la lista por item
				});
	}
	public void ejemploToString() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Andres", "Guzman"));
		usuariosList.add(new Usuario("David"," Silva"));
		usuariosList.add(new Usuario("Paula", "Solis"));
		usuariosList.add(new Usuario("Andres", "Sanchez"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		usuariosList.add(new Usuario("Bruce", "Willis"));

		Flux.fromIterable(usuariosList)
				.map(usuario -> usuario.getNombre().toUpperCase().concat(" "+usuario.getApellido().toUpperCase()))
				.flatMap(nombre -> {
					if(nombre.contains("bruce".toUpperCase())){
						return Mono.just(nombre);
					}else {
						return Mono.empty();
					}
				})
				.map(nombre ->{
					return nombre.toLowerCase();
				})
				.subscribe(u -> log.info(u.toString()));

	}

	public void ejemploFlatMap() throws Exception {

		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Andres Guzman");
		usuariosList.add("David Silva");
		usuariosList.add("Paula Solis");
		usuariosList.add("Andres Sanchez");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Willis");

		Flux.fromIterable(usuariosList)
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(),
						nombre.split(" ")[1].toUpperCase()))
				.flatMap(usuario -> {
					if(usuario.getNombre().equalsIgnoreCase("bruce")){
						return Mono.just(usuario);
					}else {
						return Mono.empty();
					}
				})
				.map(usuario ->{
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				})
				.subscribe(u -> log.info(u.toString()));

	}
	public void ejemploIterable() throws Exception {

		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Andres Guzman");
		usuariosList.add("David Silva");
		usuariosList.add("Paula Solis");
		usuariosList.add("Andres Sanchez");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Willis");

		Flux<String> nombres = Flux.fromIterable(usuariosList); /* Flux.just("Andres Guzman", "David Silva", "Paula Solis", "Andres Sanchez",
						"Bruce Lee", "Bruce Willis"); */

		Flux<Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(),
						nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> usuario.getNombre().equalsIgnoreCase("Bruce")				)
				.doOnNext(usuario -> {
					if (usuario == null){
						throw new RuntimeException("Nombres no pueden estar vacios");
					}
					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
				})
				.map(usuario ->{
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				});
		//.doOnNext(System.out::println); alternativa

		// imprime en consola y en el log
		//nombres.subscribe(log::info);
		usuarios.subscribe(u -> log.info(u.toString()),
				error -> log.error(error.getMessage()),
				new Runnable() {
					@Override
					public void run() {
						log.info("Ha finalizado la ejecucion del observable");
					}
				});
	}
}