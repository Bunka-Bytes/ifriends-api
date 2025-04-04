package com.bunkabytes.ifriendsapi.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bunkabytes.ifriendsapi.model.entity.Evento;
import com.bunkabytes.ifriendsapi.model.entity.FavoritaEvento;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;

public interface FavoritaEventoRepository extends JpaRepository<FavoritaEvento, Long>{

	Integer countByEvento(Evento evento);
	
	Optional<FavoritaEvento> findByUsuarioAndEvento(Usuario usuario, Evento evento);
	
	List<FavoritaEvento> findByUsuario(Usuario usuario);
}
