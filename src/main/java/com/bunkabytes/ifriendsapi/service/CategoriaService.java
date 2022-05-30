package com.bunkabytes.ifriendsapi.service;

import java.util.Optional;

import com.bunkabytes.ifriendsapi.model.entity.Categoria;

public interface CategoriaService {

	Optional<Categoria> obterCategoriaPorId(Long id);
}
