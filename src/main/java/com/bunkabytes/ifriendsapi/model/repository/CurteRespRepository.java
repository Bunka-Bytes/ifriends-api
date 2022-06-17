package com.bunkabytes.ifriendsapi.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bunkabytes.ifriendsapi.model.entity.CurteResp;
import com.bunkabytes.ifriendsapi.model.entity.Resposta;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;

public interface CurteRespRepository extends JpaRepository<CurteResp, Long>{

	Long countByResposta(Resposta resposta);
	
	@Query(value = 
		 	"SELECT "
		+ 		" cr "
		+ 	" FROM "
		+ 		" CurteResp cr "
		+ 	" WHERE "
		+ 		" cr.usuario = :usuario "
		+ 		" AND cr.resposta = :resposta"
		)
	Optional<CurteResp> findByUsuarioAndResposta(@Param("usuario") Usuario usuario, @Param("resposta") Resposta resposta);
}
