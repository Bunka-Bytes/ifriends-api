package com.bunkabytes.ifriendsapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.ImagemResp;

public interface ImagemRespRepository extends JpaRepository<ImagemResp, Long>{

	Boolean existsByLink(String link);
}
