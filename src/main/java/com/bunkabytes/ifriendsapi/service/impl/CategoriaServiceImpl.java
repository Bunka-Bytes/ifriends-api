package com.bunkabytes.ifriendsapi.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bunkabytes.ifriendsapi.model.entity.Categoria;
import com.bunkabytes.ifriendsapi.model.repository.CategoriaRepository;
import com.bunkabytes.ifriendsapi.service.CategoriaService;

@Service
public class CategoriaServiceImpl implements CategoriaService{

	private CategoriaRepository repository;
	
	public CategoriaServiceImpl(CategoriaRepository repository) {
		super();
		this.repository = repository;
	}
	
	@Override
	public Optional<Categoria> obterCategoriaPorId(Long id) {
		return repository.findById(id);
	}

}
