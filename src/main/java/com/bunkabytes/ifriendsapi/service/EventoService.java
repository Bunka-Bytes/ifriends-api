package com.bunkabytes.ifriendsapi.service;

import java.util.List;
import java.util.Optional;

import com.bunkabytes.ifriendsapi.model.entity.Evento;
import com.bunkabytes.ifriendsapi.model.entity.FavoritaEvento;
import com.bunkabytes.ifriendsapi.model.entity.ImagemEvento;
import com.bunkabytes.ifriendsapi.model.entity.ReportaEvento;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;

public interface EventoService {
	
	Evento salvar(Evento evento, List<ImagemEvento> imagens);
	
	Evento atualizar(Evento evento);
	
	void reportar(ReportaEvento report);
	
	void salvarTag(Evento evento);
	
	List<Evento> buscar(Evento eventoFiltro, String tag);
	
	void populaTags(List<Evento> evento);

	Optional<Evento> obterPorId(Long id);
	
	void totalFavorito(List<Evento> eventos);
	
	void verificarUsuario(Evento evento, Usuario usuarioRequisitando);
	
	boolean favoritar(FavoritaEvento favoritaEvento);
	
	List<Evento> obterPorUsuario(Usuario usuario);
}
