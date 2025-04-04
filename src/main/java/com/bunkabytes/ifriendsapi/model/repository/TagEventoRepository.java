package com.bunkabytes.ifriendsapi.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.Evento;
import com.bunkabytes.ifriendsapi.model.entity.TagEvento;

public interface TagEventoRepository extends JpaRepository<TagEvento, Long>{

	List<TagEvento> findByEvento(Evento evento);
}
