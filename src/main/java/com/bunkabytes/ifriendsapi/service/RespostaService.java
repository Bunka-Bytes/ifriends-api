package com.bunkabytes.ifriendsapi.service;

import java.util.List;
import java.util.Optional;

import com.bunkabytes.ifriendsapi.model.entity.CurteResp;
import com.bunkabytes.ifriendsapi.model.entity.Resposta;

public interface RespostaService {
Resposta salvar(Resposta resposta);
	
	Resposta atualizar(Resposta resposta);
	
	void deletar(Resposta Resposta);
	
	List<Resposta> buscar(Resposta respostaFiltro);
	
	void atualizarStatus(Resposta resposta, boolean aceita);
	
	void validar(Resposta resposta);
	
	Optional<Resposta> obterPorId(Long id);
	
	boolean curtir(CurteResp curteResp);
	
	void totalCurtidas(List<Resposta> respostas);
}
