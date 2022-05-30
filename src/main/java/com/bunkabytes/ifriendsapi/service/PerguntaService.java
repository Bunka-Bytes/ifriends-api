package com.bunkabytes.ifriendsapi.service;

import java.util.List;
import java.util.Optional;

import com.bunkabytes.ifriendsapi.model.entity.CurtePerg;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;

public interface PerguntaService {
	
	Pergunta salvar(Pergunta pergunta);
	
	Pergunta atualizar(Pergunta pergunta);
	
	void deletar(Pergunta pergunta);
	
	List<Pergunta> buscar(Pergunta perguntaFiltro);
	
	void atualizarStatus(Pergunta pergunta, boolean respondida);
	
	void validar(Pergunta pergunta);
	
	Optional<Pergunta> obterPorId(Long id);
	
	Integer gravarVisualizacao(Long id);
	
	boolean curtir(CurtePerg curtePerg);
	
	void totalCurtidas(List<Pergunta> perguntas);
	
	void totalResposta(List<Pergunta> perguntas);
	
	void verificarUsuario(Pergunta pergunta, String usuario);
	
	void salvarTag(Pergunta pergunta);
	
	void populaTags(List<Pergunta> perguntas);

}
