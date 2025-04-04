package com.bunkabytes.ifriendsapi.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.Resposta;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;

public interface RespostaRepository extends JpaRepository<Resposta, Long>{
	
	Long countRespostaByPergunta(Pergunta pergunta);
	
	List<Resposta> findByPergunta(Pergunta pergunta);
	
	List<Resposta> findByUsuario(Usuario usuario);
	
	Optional<Resposta> findByPerguntaAndAceita(Pergunta pergunta, boolean aceita);
}
