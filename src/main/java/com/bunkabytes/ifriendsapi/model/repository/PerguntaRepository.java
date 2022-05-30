package com.bunkabytes.ifriendsapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.Pergunta;


public interface PerguntaRepository extends JpaRepository<Pergunta, Long>{
	
}
