package com.bunkabytes.ifriendsapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.CurtePerg;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;

public interface CurtePergRepository extends JpaRepository<CurtePerg, Long>{
	
	Long countByPergunta(Pergunta pergunta);
	
}
