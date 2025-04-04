package com.bunkabytes.ifriendsapi.model.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>{
	
	Optional<Categoria> findByNome(String nome);
}
