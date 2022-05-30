package com.bunkabytes.ifriendsapi.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.Resposta;

public interface RespostaRepository extends JpaRepository<Resposta, Long>{
	
	Long countRespostaByPergunta(Pergunta pergunta);
	
	List<Resposta> findByPergunta(Pergunta pergunta);

}
