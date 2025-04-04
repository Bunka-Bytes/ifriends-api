package com.bunkabytes.ifriendsapi.api.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.service.RecursosService;

import lombok.RequiredArgsConstructor;
import lombok.var;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
@CrossOrigin(origins = "*")
public class RecursoResource {

	private final RecursosService service;
	
	@GetMapping("/categorias/{id}")
	public ResponseEntity<?> exibir(@PathVariable("id") Long id) {
		log.info("exibindo categoria por id");

		try {
			var categorias = service.obterPorId(id);
			return ResponseEntity.ok(categorias);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@GetMapping("/categorias")
	public ResponseEntity<?> exibirTodasCategorias() {
		log.info("exibindo todas as categorias");

		try {
			var categorias = service.obterCategorias();
			return ResponseEntity.ok(categorias);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
	
	@GetMapping("/dominios")
	public ResponseEntity<?> exibirDominios() {
		log.info("exibindo todos dominios");

		try {
			var dominios = service.obterDominios();
			return ResponseEntity.ok(dominios);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
	
	@GetMapping("/cursos")
	public ResponseEntity<?> exibirTodosCursos() {
		log.info("exibindo todos os cursos");

		try {
			var cursos = service.obterCursos();
			return ResponseEntity.ok(cursos);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
	
	@GetMapping("/motivosReport")
	public ResponseEntity<?> exibirTodosMotivos() {
		log.info("exibindo todos motivos de report");

		try {
			var motivos = service.obterMotivosReport();
			return ResponseEntity.ok(motivos);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
}
