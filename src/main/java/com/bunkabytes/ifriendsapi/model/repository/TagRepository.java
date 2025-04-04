package com.bunkabytes.ifriendsapi.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>{
	
	Optional<Tag> findByNome(String nomeTag);
	
}
