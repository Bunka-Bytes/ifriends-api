package com.bunkabytes.ifriendsapi.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bunkabytes.ifriendsapi.model.entity.Pergunta;


public interface PerguntaRepository extends JpaRepository<Pergunta, Long>{
	
	@Query(value = 
			" SELECT "
		+		" p "
		+ 	" FROM "
		+ 		" Pergunta p "
		+ 		" INNER JOIN p.usuario u "
		+ 	" WHERE "
		+ 		" UPPER(p.texto) LIKE CONCAT('%',UPPER(:pesquisa),'%') "
		+ 		" OR UPPER(p.titulo) LIKE CONCAT('%',UPPER(:pesquisa),'%') "
		+ 		" OR UPPER(u.nome) LIKE CONCAT('%',UPPER(:pesquisa),'%')"
		)
	List<Pergunta> findAllPesquisa(@Param("pesquisa") String pesquisa);
}
