package com.bunkabytes.ifriendsapi.api.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.service.impl.CategoriaServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.var;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categorias")
@Slf4j
@CrossOrigin(origins = "*")
public class CategoriaResource {
	
	private final CategoriaServiceImpl service;
	
	@GetMapping("{id}")
	public ResponseEntity<?> exibir(@PathVariable("id") Long id) {
		log.info("exibindo categoria por id");

		try {
			var categorias = service.obterPorId(id);
			return ResponseEntity.ok(categorias);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
	
	@GetMapping()
	public ResponseEntity<?> exibirTodasCategorias() {
		log.info("exibindo todas as categorias");

		try {
			var categorias = service.obterCategorias();
			return ResponseEntity.ok(categorias);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
}
