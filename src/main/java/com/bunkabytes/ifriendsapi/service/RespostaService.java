package com.bunkabytes.ifriendsapi.service;

import java.util.List;
import java.util.Optional;

import com.bunkabytes.ifriendsapi.model.entity.CurteResp;
import com.bunkabytes.ifriendsapi.model.entity.ImagemResp;
import com.bunkabytes.ifriendsapi.model.entity.ReportaResposta;
import com.bunkabytes.ifriendsapi.model.entity.Resposta;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;

public interface RespostaService {
	
	Resposta salvar(Resposta resposta, List<ImagemResp> imagens);
	
	Resposta atualizar(Resposta resposta);
	
	void reportar(ReportaResposta report);
	
	void deletar(Resposta Resposta);
	
	List<Resposta> buscar(Resposta respostaFiltro);
	
	void validar(Resposta resposta);
	
	Optional<Resposta> obterPorId(Long id);
	
	boolean curtir(CurteResp curteResp);
	
	void totalCurtidas(List<Resposta> respostas);
	
	void verificarUsuario(Resposta resposta, Usuario usuarioRequisitando);
	
	List<Resposta> obterPorUsuario(Usuario usuario);
	
	boolean aceitarResposta(Resposta resposta);
}
