package com.bunkabytes.ifriendsapi.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.entity.Visualizacao;

public interface VisualizacaoRepository extends JpaRepository<Visualizacao, Long>{

	@Query(value = 
		 	"SELECT "
		+ 		" v "
		+ 	" FROM "
		+ 		" Visualizacao v "
		+ 	" WHERE "
		+ 		" v.usuario = :usuario "
		+ 		" AND v.pergunta = :pergunta"
		)
	Optional<Visualizacao> findByUsuarioAndPergunta(@Param("usuario") Usuario usuario, @Param("pergunta") Pergunta pergunta);
	
	Integer countByPergunta(Pergunta pergunta);
}
