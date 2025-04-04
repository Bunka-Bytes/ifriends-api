package com.bunkabytes.ifriendsapi.service;

import java.util.List;
import java.util.Optional;

import com.bunkabytes.ifriendsapi.model.entity.Categoria;
import com.bunkabytes.ifriendsapi.model.entity.Curso;
import com.bunkabytes.ifriendsapi.model.entity.Dominio;
import com.bunkabytes.ifriendsapi.model.entity.MotivoReport;

public interface RecursosService {

	Optional<Categoria> obterPorId(Long id);
	
	Optional<Categoria> obterPorNome(String nome);
	
	List<Categoria> obterCategorias();
	
	List<Curso> obterCursos();
	
	Optional<Curso> obterPorSigla(String sigla);
	
	List<Dominio> obterDominios();
	
	List<MotivoReport> obterMotivosReport();
	
	Optional<MotivoReport> obterMotivosReportPorId(Long id);
}
