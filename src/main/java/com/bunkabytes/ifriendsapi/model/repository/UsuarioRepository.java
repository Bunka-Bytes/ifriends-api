package com.bunkabytes.ifriendsapi.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bunkabytes.ifriendsapi.model.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
	boolean existsByEmail(String email);
	
	Optional<Usuario> findByEmail(String email);
	
	@Query(value = 
			"SELECT "
		+		" id "
		+ 	"FROM "
		+ 		" Usuario "
		+ 	"WHERE "
		+ 		" nome LIKE '%:nome%' "
		)
	Long findByNome(@Param("nome") String nome);

}
