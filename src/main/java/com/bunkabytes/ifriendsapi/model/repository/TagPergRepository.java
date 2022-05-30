package com.bunkabytes.ifriendsapi.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.TagPerg;

public interface TagPergRepository extends JpaRepository<TagPerg, Long> {
	
	List<TagPerg> findByPergunta(Pergunta pergunta);
	
}
