package com.bunkabytes.ifriendsapi.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.Categoria;
import com.bunkabytes.ifriendsapi.model.repository.CategoriaRepository;
import com.bunkabytes.ifriendsapi.service.CategoriaService;

import lombok.var;

@Service
public class CategoriaServiceImpl implements CategoriaService{

	private CategoriaRepository repository;
	
	public CategoriaServiceImpl(CategoriaRepository repository) {
		this.repository = repository;
	}
	
	@Override
	public Optional<Categoria> obterPorId(Long id) {
		
		var categoria = repository.findById(id);
		if (!categoria.isPresent()) {
			throw new RegraNegocioException("Categoria não existe na base de dados");
		}
		return categoria;
	}
	
	@Override
	public Optional<Categoria> obterPorNome(String nome) {
		
		return repository.findByNome(nome);
	}
	
	@Override
	public List<Categoria> obterCategorias() {
		return repository.findAll();
	}

}
