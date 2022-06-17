package com.bunkabytes.ifriendsapi.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bunkabytes.ifriendsapi.model.entity.CurtePerg;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;

public interface CurtePergRepository extends JpaRepository<CurtePerg, Long>{
	
	Long countByPergunta(Pergunta pergunta);
	
	@Query(value = 
		 	"SELECT "
		+ 		" cp "
		+ 	" FROM "
		+ 		" CurtePerg cp "
		+ 	" WHERE "
		+ 		" cp.usuario = :usuario "
		+ 		" AND cp.pergunta = :pergunta"
		)
	Optional<CurtePerg> findByUsuarioAndPergunta(@Param("usuario") Usuario usuario, @Param("pergunta") Pergunta pergunta);
	
}
