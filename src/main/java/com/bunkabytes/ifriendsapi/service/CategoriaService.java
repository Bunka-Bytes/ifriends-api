package com.bunkabytes.ifriendsapi.service;

import java.util.List;
import java.util.Optional;

import com.bunkabytes.ifriendsapi.model.entity.Categoria;

public interface CategoriaService {

	Optional<Categoria> obterPorId(Long id);
	
	Optional<Categoria> obterPorNome(String nome);
	
	List<Categoria> obterCategorias();
}
