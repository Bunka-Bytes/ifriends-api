package com.bunkabytes.ifriendsapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bunkabytes.ifriendsapi.model.entity.Dominio;

@Repository
public interface DominioRepository extends JpaRepository<Dominio, Long>{

}
