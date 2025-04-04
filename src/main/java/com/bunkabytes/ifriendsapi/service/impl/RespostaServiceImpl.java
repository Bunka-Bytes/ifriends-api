package com.bunkabytes.ifriendsapi.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.CurteResp;
import com.bunkabytes.ifriendsapi.model.entity.ImagemResp;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.ReportaResposta;
import com.bunkabytes.ifriendsapi.model.entity.Resposta;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.repository.CurteRespRepository;
import com.bunkabytes.ifriendsapi.model.repository.ImagemRespRepository;
import com.bunkabytes.ifriendsapi.model.repository.PalavraRuimRepository;
import com.bunkabytes.ifriendsapi.model.repository.ReportaRespostaRepository;
import com.bunkabytes.ifriendsapi.model.repository.RespostaRepository;
import com.bunkabytes.ifriendsapi.service.RespostaService;

import lombok.var;

@Service
public class RespostaServiceImpl implements RespostaService {

	@Autowired
	private RespostaRepository repository;
	@Autowired
	private CurteRespRepository curteRespRepository;
	@Autowired
	private ImagemRespRepository imagemRespRepository;
	@Autowired
	private ReportaRespostaRepository reportaRespostaRepository;
	@Autowired
	private PalavraRuimRepository palavraRuimRepository;

	@Override
	@Transactional
	public Resposta salvar(Resposta resposta, List<ImagemResp> imagens) {
		validar(resposta);
		resposta.setAceita(false);
		resposta.setDeletado(false);
		var respostaSalva = repository.save(resposta);
		if (imagens != null)
			if(!imagens.isEmpty())
				if(imagens.size() > 5)
					throw new RegraNegocioException("No máximo 5 imagens por resposta");
				else
					for (var imagem : imagens) {
						if (imagemRespRepository.existsByLink(imagem.getLink())) {
							throw new RegraNegocioException("Link da imagem " + imagem.getLink() + " já está sendo utilizada");
						}
						imagem.setResposta(respostaSalva);
						imagemRespRepository.save(imagem);
					}
		return respostaSalva;
	}

	@Override
	@Transactional
	public Resposta atualizar(Resposta modificacoes) {
		Objects.requireNonNull(modificacoes.getId());
		var respostaAAtualizar = repository.findById(modificacoes.getId());
		validar(respostaAAtualizar.get());
		respostaAAtualizar.get().setTexto(modificacoes.getTexto());
		return repository.save(respostaAAtualizar.get());
	}

	@Override
	@Transactional
	public void deletar(Resposta resposta) {
		Objects.requireNonNull(resposta.getId());
		resposta.setDeletado(true);
		repository.save(resposta);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Resposta> buscar(Resposta respostas) {
		Pergunta pergunta = respostas.getPergunta();
		List<Resposta> respostasEncontradas = repository.findByPergunta(pergunta);
		totalCurtidas(respostasEncontradas);
		return respostasEncontradas;
	}

	@Override
	@Transactional
	public void validar(Resposta resposta) {

		// Validando campos vazios
		if (resposta.getTexto() == null || resposta.getTexto().trim().equals("")) {
			throw new RegraNegocioException("Informe uma resposta válida.");
		}

		if (resposta.getPergunta() == null || resposta.getPergunta().getId() == null) {
			throw new RegraNegocioException("Pergunta não está cadastrada.");
		}

		// Validando tamanho dos campos
		if (resposta.getTexto().length() > 1000) {
			throw new RegraNegocioException("Texto muito longo.");
		}
		
		for (var palavraRuim : palavraRuimRepository.findAll()) {
			
			if(resposta.getTexto().toLowerCase().contains(palavraRuim.getPalavra()))
				throw new RegraNegocioException("Por favor, não utilize a palavra '"+palavraRuim.getPalavra()+"'.");			
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
		// totalCurtidas(respostaConvertida);
		return resposta;
	}

	@Override
	@Transactional
	public boolean curtir(CurteResp curteResp) {
		var curteRespEncontrado = curteRespRepository.findByUsuarioAndResposta(curteResp.getUsuario(),
				curteResp.getResposta());
		if (!curteRespEncontrado.isPresent()) {
			curteRespRepository.save(curteResp);
			return true;
		} else {
			curteRespRepository.deleteById(curteRespEncontrado.get().getId());
			return false;
		}
	}

	@Override
	@Transactional
	public void totalCurtidas(List<Resposta> respostas) {
		for (Resposta resposta : respostas) {
			var total = curteRespRepository.countByResposta(resposta);
			resposta.setQtdCurtida(Long.valueOf(total));
		}
	}

	@Override
	public void verificarUsuario(Resposta resposta, Usuario usuarioRequisitando) {
		if (!resposta.getUsuario().getEmail().equals(usuarioRequisitando.getEmail())
				&& !usuarioRequisitando.isAdmin()) {
			throw new RegraNegocioException("Não é possivel manter respostas que não sejam suas.");
		}
	}

	@Override
	public List<Resposta> obterPorUsuario(Usuario usuario) {
		var respostas = repository.findByUsuario(usuario);
		totalCurtidas(respostas);

		return respostas;
	}

	@Override
	@Transactional
	public boolean aceitarResposta(Resposta resposta) {

		var respostaAceita = repository.findByPerguntaAndAceita(resposta.getPergunta(), true);
		if (respostaAceita.isPresent()) {
			if(respostaAceita.get().getId().equals(resposta.getId())) {
				respostaAceita.get().setAceita(false);
				repository.save(respostaAceita.get());
				return false;
			}
			else
				throw new RegraNegocioException("Pergunta já possui uma resposta aceita.");
				
		} else {
			resposta.setAceita(true);
			repository.save(resposta);
			return true;
		}

	}

	@Override
	public void reportar(ReportaResposta report) {
		reportaRespostaRepository.save(report);
		
	}

}
