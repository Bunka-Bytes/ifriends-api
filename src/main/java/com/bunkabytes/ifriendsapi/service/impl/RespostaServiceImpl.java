package com.bunkabytes.ifriendsapi.service.impl;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.CurteResp;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.Resposta;
import com.bunkabytes.ifriendsapi.model.repository.CurteRespRepository;
import com.bunkabytes.ifriendsapi.model.repository.RespostaRepository;
import com.bunkabytes.ifriendsapi.service.RespostaService;

import lombok.var;

@Service
public class RespostaServiceImpl implements RespostaService {
	
	private RespostaRepository repository;
	private CurteRespRepository curteRespRepository;

	public RespostaServiceImpl(RespostaRepository repository, CurteRespRepository curteRespRepository) {
		this.repository = repository;
		this.curteRespRepository = curteRespRepository;
	}
	
	@Override
	@Transactional
	public Resposta salvar(Resposta resposta) {
		validar(resposta);
		resposta.setAceita(false);
		resposta.setDeletado(false);
		resposta.setDataEmissao(LocalDateTime.now());
		return repository.save(resposta);
	}

	@Override
	@Transactional
	public Resposta atualizar(Resposta resposta) {
		Objects.requireNonNull(resposta.getId());
		return repository.save(resposta);
	}

	@Override
	@Transactional
	public void deletar(Resposta resposta) {
		Objects.requireNonNull(resposta.getId());
		resposta.setDeletado(true);
		repository.save(resposta);
	}

	@Override
	@Transactional
	public List<Resposta> buscar(Resposta respostas) {
		Pergunta pergunta = respostas.getPergunta();
		List<Resposta> respostasEncontradas = repository.findByPergunta(pergunta);
		totalCurtidas(respostasEncontradas);
		return respostasEncontradas;
	}

	@Override
	@Transactional
	public void atualizarStatus(Resposta resposta, boolean aceita) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Transactional
	public void validar(Resposta resposta) {
		
		if(resposta.getTexto() == null || resposta.getTexto().trim().equals("")) {
			throw new RegraNegocioException("Informe uma resposta válida.");
		}
		
		if(resposta.getPergunta() == null|| resposta.getPergunta().getId() == null) {
			throw new RegraNegocioException("Pergunta não está cadastrada.");
		}
		
	}

	@Override
	@Transactional
	public Optional<Resposta> obterPorId(Long id) {
		var resposta = repository.findById(id);
		if (!resposta.isPresent()) {
			throw new RegraNegocioException("Resposta não existe na base de dados");
		}
		List<Resposta> respostaConvertida = new ArrayList<Resposta>();
		respostaConvertida.add(resposta.get());
		//totalCurtidas(respostaConvertida);
		return resposta;
	}

	@Override
	@Transactional
	public boolean curtir(CurteResp curteResp) {
		Example example = Example.of(curteResp, 
				ExampleMatcher.matching()
				.withIgnoreCase()
				.withStringMatcher(StringMatcher.CONTAINING));
		List<CurteResp> curteRespEncontrados = curteRespRepository.findAll(example);
		if(curteRespEncontrados.isEmpty()) {
			curteRespRepository.save(curteResp);
			return true;
		}else {
			curteRespRepository.deleteById(curteRespEncontrados.get(0).getId());
			return false;
		}
	}
	
	@Override
	@Transactional
	public void totalCurtidas(List<Resposta> respostas) {
		for (Resposta resposta : respostas) {
			Long total = curteRespRepository.countByResposta(resposta);
			resposta.setQtdCurtida(Long.valueOf(total));
		}
	}

}
