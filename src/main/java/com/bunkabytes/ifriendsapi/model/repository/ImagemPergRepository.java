package com.bunkabytes.ifriendsapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.ImagemPerg;

public interface ImagemPergRepository extends JpaRepository<ImagemPerg, Long>{
	
	Boolean existsByLink(String link);

}
