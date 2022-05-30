package com.bunkabytes.ifriendsapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.CurteResp;
import com.bunkabytes.ifriendsapi.model.entity.Resposta;

public interface CurteRespRepository extends JpaRepository<CurteResp, Long>{

	Long countByResposta(Resposta resposta);
}
