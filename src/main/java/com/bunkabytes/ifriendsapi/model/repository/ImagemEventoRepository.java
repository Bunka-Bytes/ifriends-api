package com.bunkabytes.ifriendsapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.ImagemEvento;

public interface ImagemEventoRepository extends JpaRepository<ImagemEvento, Long>{

	Boolean existsByLink(String link);
}
