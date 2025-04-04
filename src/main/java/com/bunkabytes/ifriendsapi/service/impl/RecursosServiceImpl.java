package com.bunkabytes.ifriendsapi.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.Categoria;
import com.bunkabytes.ifriendsapi.model.entity.Curso;
import com.bunkabytes.ifriendsapi.model.entity.Dominio;
import com.bunkabytes.ifriendsapi.model.entity.MotivoReport;
import com.bunkabytes.ifriendsapi.model.repository.CategoriaRepository;
import com.bunkabytes.ifriendsapi.model.repository.CursoRepository;
import com.bunkabytes.ifriendsapi.model.repository.DominioRepository;
import com.bunkabytes.ifriendsapi.model.repository.MotivoReportRepository;
import com.bunkabytes.ifriendsapi.service.RecursosService;

import lombok.var;

@Service
public class RecursosServiceImpl implements RecursosService{

	@Autowired
	private CategoriaRepository categoriaRepository;
	@Autowired
	private DominioRepository dominioRepository;
	@Autowired
	private CursoRepository cursoRepository;
	@Autowired
	private MotivoReportRepository motivoReportRepository;
	
	@Override
	public Optional<Categoria> obterPorId(Long id) {
		if(id == null)
			throw new RegraNegocioException("É obrigatório o uso da categoria");
		
		var categoria = categoriaRepository.findById(id);
		if (!categoria.isPresent()) {
			throw new RegraNegocioException("Categoria não existe na base de dados");
		}
		return categoria;
	}
	
	@Override
	public Optional<Categoria> obterPorNome(String nome) {
		
		return categoriaRepository.findByNome(nome);
	}
	
	@Override
	public List<Categoria> obterCategorias() {
		return categoriaRepository.findAll();
	}
	
	@Override
	public List<Dominio> obterDominios() {
		return dominioRepository.findAll();
	}
	
	@Override
	public List<Curso> obterCursos() {
		
		var curso = cursoRepository.findAll(Sort.by("nome").ascending());
		return curso;
	}
	
	@Override
	public Optional<Curso> obterPorSigla(String sigla) {
		
		var curso = cursoRepository.findById(sigla);
		if (!curso.isPresent()) {
			throw new RegraNegocioException("curso não existe na base de dados");
		}
		return curso;
	}

	@Override
	public List<MotivoReport> obterMotivosReport() {
		var motivos = motivoReportRepository.findAll();
		if (motivos.size() == 0) {
			throw new RegraNegocioException("não foram encontrados cursos");
		}
		return motivos;
	}

	@Override
	public Optional<MotivoReport> obterMotivosReportPorId(Long id) {
		if(id == null)
			throw new RegraNegocioException("É obrigatório o uso do motivo");
		
		var motivo = motivoReportRepository.findById(id);
		if (!motivo.isPresent()) {
			throw new RegraNegocioException("Motivo não existe na base de dados");
		}
		return motivo;
	}
}
