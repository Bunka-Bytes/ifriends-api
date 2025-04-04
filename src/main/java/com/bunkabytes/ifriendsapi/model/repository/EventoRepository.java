package com.bunkabytes.ifriendsapi.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.Evento;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;

public interface EventoRepository extends JpaRepository<Evento, Long>{
	
	List<Evento> findByUsuario(Usuario usuario);

}
