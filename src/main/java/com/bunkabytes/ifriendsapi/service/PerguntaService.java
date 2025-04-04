package com.bunkabytes.ifriendsapi.service;

import java.util.List;
import java.util.Optional;

import com.bunkabytes.ifriendsapi.model.entity.CurtePerg;
import com.bunkabytes.ifriendsapi.model.entity.ImagemPerg;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.ReportaPergunta;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.entity.Visualizacao;

public interface PerguntaService {
	
	Pergunta salvar(Pergunta pergunta, List<ImagemPerg> imagens);
	
	Pergunta atualizar(Pergunta pergunta);
	
	void reportar(ReportaPergunta report);
	
	void deletar(Pergunta pergunta);
	
	boolean somarVisualizacao(Visualizacao visualizacao);
	
	List<ReportaPergunta> obterPerguntasReportadas();
	
	List<Pergunta> buscar(Pergunta perguntaFiltro, Boolean semResposta, String tag, String ordenar, boolean crescente);
	
	String atualizarStatus(Pergunta pergunta);
	
	void validar(Pergunta pergunta);
	
	Optional<Pergunta> obterPorId(Long id);
	
	boolean curtir(CurtePerg curtePerg);
	
	void totalCurtidas(List<Pergunta> perguntas);
	
	void totalResposta(List<Pergunta> perguntas);
	
	void totalVisualizacao(List<Pergunta> perguntas);
	
	void verificarUsuario(Pergunta pergunta, Usuario usuarioRequisitando);
	
	void salvarTag(Pergunta pergunta);
	
	void populaTags(List<Pergunta> perguntas);
	
	List<Pergunta> obterPorUsuario(Usuario usuario);
	
}
