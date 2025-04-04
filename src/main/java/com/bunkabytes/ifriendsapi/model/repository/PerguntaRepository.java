package com.bunkabytes.ifriendsapi.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;

public interface PerguntaRepository extends JpaRepository<Pergunta, Long>{
	
	List<Pergunta> findByUsuario(Usuario usuario);
	
	List<Pergunta> findAllByOrderByDataEmissaoDesc();
}
