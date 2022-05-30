package com.bunkabytes.ifriendsapi.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;

import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.CurtePerg;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.Tag;
import com.bunkabytes.ifriendsapi.model.entity.TagPerg;
import com.bunkabytes.ifriendsapi.model.repository.CurtePergRepository;
import com.bunkabytes.ifriendsapi.model.repository.PerguntaRepository;
import com.bunkabytes.ifriendsapi.model.repository.RespostaRepository;
import com.bunkabytes.ifriendsapi.model.repository.TagPergRepository;
import com.bunkabytes.ifriendsapi.model.repository.TagRepository;
import com.bunkabytes.ifriendsapi.service.PerguntaService;

import lombok.var;

@Service
public class PerguntaServiceImpl implements PerguntaService {

	private PerguntaRepository repository;
	private CurtePergRepository curtePergRepository;
	private RespostaRepository respostaRepository;
	private TagRepository tagRepository;
	private TagPergRepository tagPergRepository;

	public PerguntaServiceImpl(PerguntaRepository repository, CurtePergRepository curtePergRepository,
			RespostaRepository respostaRepository, TagRepository tagRepository, TagPergRepository tagPergRepository) {
		this.repository = repository;
		this.curtePergRepository = curtePergRepository;
		this.respostaRepository = respostaRepository;
		this.tagRepository = tagRepository;
		this.tagPergRepository = tagPergRepository;
	}

	@Override
	@Transactional
	public Pergunta salvar(Pergunta pergunta) {
		validar(pergunta);
		pergunta.setRespondida(false);
		pergunta.setDeletado(false);
		pergunta.setDataEmissao(LocalDateTime.now());
		Pergunta perguntaSalva = repository.save(pergunta);
		salvarTag(perguntaSalva);
		return perguntaSalva;
	}

	@Override
	@Transactional
	public void salvarTag(Pergunta pergunta) {

		List<String> tags = pergunta.getTag();

		for (String nomeTag : tags) {

			Tag tag = new Tag();
			TagPerg tagPerg = new TagPerg();

			var tagNoBanco = tagRepository.findByNome(nomeTag);

			if (tagNoBanco.isEmpty()) {

				tag.setNome(nomeTag);
				var tagSalvo = tagRepository.save(tag);
				tagPerg.setTag(tagSalvo);

			} else {
				tagPerg.setTag(tagNoBanco.get());
			}

			tagPerg.setPergunta(pergunta);
			tagPergRepository.save(tagPerg);

		}
	}

	@Override
	@Transactional
	public Pergunta atualizar(Pergunta pergunta) {
		Objects.requireNonNull(pergunta.getId());
		return repository.save(pergunta);
	}

	@Override
	@Transactional
	public void deletar(Pergunta pergunta) {
		Objects.requireNonNull(pergunta.getId());
		pergunta.setDeletado(true);
		repository.save(pergunta);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Pergunta> buscar(Pergunta perguntaFiltro) {
		Example example = Example.of(perguntaFiltro,
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
		List<Pergunta> perguntas = repository.findAll(example);
		totalCurtidas(perguntas);
		totalResposta(perguntas);
		populaTags(perguntas);
		return perguntas;
	}

	@Override
	public void atualizarStatus(Pergunta pergunta, boolean respondida) {
		pergunta.setRespondida(respondida);
		atualizar(pergunta);
	}

	@Override
	public void validar(Pergunta pergunta) {

		if (pergunta.getTexto() == null || pergunta.getTexto().trim().equals("")) {
			throw new RegraNegocioException("Informe uma texto válido.");
		}

		if (pergunta.getTitulo() == null || pergunta.getTitulo().trim().equals("")) {
			throw new RegraNegocioException("Informe um título válido.");
		}

	}

	@Override
	public Optional<Pergunta> obterPorId(Long id) {
		var pergunta = repository.findById(id);
		if (!pergunta.isPresent()) {
			throw new RegraNegocioException("Pergunta não existe na base de dados");
		}
		List<Pergunta> perguntaConvertida = new ArrayList<Pergunta>();
		perguntaConvertida.add(pergunta.get());
		totalCurtidas(perguntaConvertida);
		totalResposta(perguntaConvertida);
		populaTags(perguntaConvertida);
		return pergunta;
	}

	@Override
	// EM CONSTRUÇÃO
	public Integer gravarVisualizacao(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public boolean curtir(CurtePerg curtePerg) {
		Example example = Example.of(curtePerg,
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
		List<CurtePerg> curtePergEncontrados = curtePergRepository.findAll(example);
		if (curtePergEncontrados.isEmpty()) {
			curtePergRepository.save(curtePerg);
			return true;
		} else {
			curtePergRepository.deleteById(curtePergEncontrados.get(0).getId());
			return false;
		}
	}

	@Override
	@Transactional
	public void totalCurtidas(List<Pergunta> perguntas) {
		for (Pergunta pergunta : perguntas) {
			var total = curtePergRepository.countByPergunta(pergunta);
			pergunta.setQtdCurtida(Long.valueOf(total));
		}
	}

	@Override
	@Transactional
	public void totalResposta(List<Pergunta> perguntas) {

		for (Pergunta pergunta : perguntas) {
			var total = respostaRepository.countRespostaByPergunta(pergunta);
			pergunta.setQtdResposta(Long.valueOf(total));
		}
	}

	@Override
	@Transactional
	public void populaTags(List<Pergunta> perguntas) {

		for (Pergunta pergunta : perguntas) {
			List<TagPerg> tags = tagPergRepository.findByPergunta(pergunta);

			List<String> nomeTags = new ArrayList<String>();
			for (TagPerg tag : tags) {

				Optional<Tag> nomeTag = tagRepository.findById(tag.getTag().getId());
				nomeTags.add(nomeTag.get().getNome());
			}
			pergunta.setTag(nomeTags);

		}

	}

	@Override
	public void verificarUsuario(Pergunta pergunta, String usuario) {
		if (!pergunta.getUsuario().getEmail().equals(usuario)) {
			throw new RegraNegocioException("Pergunta não pertence ao usuário.");
		}
	}

}
