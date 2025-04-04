package com.bunkabytes.ifriendsapi.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.CurtePerg;
import com.bunkabytes.ifriendsapi.model.entity.ImagemPerg;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.ReportaPergunta;
import com.bunkabytes.ifriendsapi.model.entity.Tag;
import com.bunkabytes.ifriendsapi.model.entity.TagPerg;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.entity.Visualizacao;
import com.bunkabytes.ifriendsapi.model.repository.CurtePergRepository;
import com.bunkabytes.ifriendsapi.model.repository.ImagemPergRepository;
import com.bunkabytes.ifriendsapi.model.repository.PalavraRuimRepository;
import com.bunkabytes.ifriendsapi.model.repository.PerguntaRepository;
import com.bunkabytes.ifriendsapi.model.repository.ReportaPerguntaRepository;
import com.bunkabytes.ifriendsapi.model.repository.RespostaRepository;
import com.bunkabytes.ifriendsapi.model.repository.TagPergRepository;
import com.bunkabytes.ifriendsapi.model.repository.TagRepository;
import com.bunkabytes.ifriendsapi.model.repository.VisualizacaoRepository;
import com.bunkabytes.ifriendsapi.service.PerguntaService;
import com.bunkabytes.ifriendsapi.service.impl.comparator.ComparatorQtdCurtidaDecrescente;
import com.bunkabytes.ifriendsapi.service.impl.comparator.ComparatorVisualizacaoDecrescente;

import lombok.var;

@Service
public class PerguntaServiceImpl implements PerguntaService {

	@Autowired
	private PerguntaRepository repository;
	@Autowired
	private CurtePergRepository curtePergRepository;
	@Autowired
	private RespostaRepository respostaRepository;
	@Autowired
	private TagRepository tagRepository;
	@Autowired
	private TagPergRepository tagPergRepository;
	@Autowired
	private ImagemPergRepository imagemPergRepository;
	@Autowired
	private ReportaPerguntaRepository reportaPerguntaRepository;
	@Autowired
	private PalavraRuimRepository palavraRuimRepository;
	@Autowired
	private VisualizacaoRepository visualizacaoRepository;

	@Override
	@Transactional
	public Pergunta salvar(Pergunta pergunta, List<ImagemPerg> imagens) {
		validar(pergunta);
		pergunta.setVisualizacao(0);
		var perguntaSalva = repository.save(pergunta);
		salvarTag(perguntaSalva);
		if (imagens != null)
			if(!imagens.isEmpty())
				if(imagens.size() > 5)
					throw new RegraNegocioException("No máximo 5 imagens por pergunta");
				else
					for (var imagem : imagens) {
						if (imagemPergRepository.existsByLink(imagem.getLink())) {
							throw new RegraNegocioException("Link da imagem " + imagem.getLink() + " já está sendo utilizada");
						}
						imagem.setPergunta(perguntaSalva);
						imagemPergRepository.save(imagem);
					}
		return perguntaSalva;
	}

	@Override
	@Transactional
	public void salvarTag(Pergunta pergunta) {

		if(pergunta.getTags().size() > 10)
			throw new RegraNegocioException("No máximo 10 tags por pergunta");
		
		for (String nomeTag : pergunta.getTags()) {
			var tag = new Tag();
			var tagPerg = new TagPerg();
			var tagExistente = tagRepository.findByNome(nomeTag.trim());
			if (!tagExistente.isPresent()) {
				// Caso a tag não exista no banco de dados, é gravado normalmente
				tag.setNome(nomeTag);
				var tagSalvo = tagRepository.save(tag);
				tagPerg.setTag(tagSalvo);
			} else {
				// Caso a tag já exista, ela é atribuida a pergunta sem ocorrer a gravação
				tagPerg.setTag(tagExistente.get());
			}

			tagPerg.setPergunta(pergunta);
			tagPergRepository.save(tagPerg);

		}
	}

	@Override
	@Transactional
	public Pergunta atualizar(Pergunta pergunta) {
		Objects.requireNonNull(pergunta.getId());
		validar(pergunta);
		var perguntaAAtualizar = repository.findById(pergunta.getId()).get();
		perguntaAAtualizar.setTexto(pergunta.getTexto());
		perguntaAAtualizar.setTitulo(pergunta.getTitulo());
		perguntaAAtualizar.setCategoria(pergunta.getCategoria());
		return repository.save(perguntaAAtualizar);
	}

	@Override
	@Transactional
	public void deletar(Pergunta pergunta) {
		Objects.requireNonNull(pergunta.getId());
		// Perguntas não são deletadas na base de dados, somente marcadas com a flag.
		pergunta.setDeletado(true);
		repository.save(pergunta);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Pergunta> buscar(Pergunta perguntaFiltro, Boolean semResposta, String tag, String ordenar, boolean crescente) {
		var example = Example.of(perguntaFiltro, ExampleMatcher.matching().withIgnoreNullValues().withIgnoreCase()
				.withStringMatcher(StringMatcher.CONTAINING));
		
		List<Pergunta> perguntas = new ArrayList<Pergunta>();
		
		if(ordenar == null || ordenar == "") {
			ordenar = "dataEmissao";
		}
		
		if(ordenar.equalsIgnoreCase("visualizacao") || ordenar.equalsIgnoreCase("qtdCurtida")) {
			perguntas = repository.findAll(example);
		}
		else {
			if(crescente)
				perguntas = repository.findAll(example, Sort.by(ordenar).ascending());
			else
				perguntas = repository.findAll(example, Sort.by(ordenar).descending());
		}

		totalCurtidas(perguntas);
		totalVisualizacao(perguntas);
		totalResposta(perguntas);
		populaTags(perguntas);
		
		if(ordenar.equalsIgnoreCase("visualizacao")) {
			Collections.sort(perguntas, new ComparatorVisualizacaoDecrescente());
		}
		if(ordenar.equalsIgnoreCase("qtdCurtida")) {
			Collections.sort(perguntas, new ComparatorQtdCurtidaDecrescente());
		}
		
		if (semResposta != null && semResposta == true) {
			for (int i = 0; i < perguntas.size(); i++) {
				if (!perguntas.get(i).getQtdResposta().equals(0l)) {
					perguntas.remove(i);
					i = i - 1;
				}
			}
		}

		if (tag != null) {
			for (int i = 0; i < perguntas.size(); i++)
				if (!perguntas.get(i).getTags().stream().anyMatch(tag::equalsIgnoreCase)) {
					perguntas.remove(i);
					i = i - 1;
				}
		}

		return perguntas;
	}

	@Override
	public String atualizarStatus(Pergunta pergunta) {
		if (pergunta.getRespondida() != null && pergunta.getRespondida() == true) {
			pergunta.setRespondida(false);
			repository.save(pergunta);
			return "Pergunta fechada com sucesso!";
		} else {
			pergunta.setRespondida(true);
			repository.save(pergunta);
			return "Pergunta aberta com sucesso!";
		}

	}

	@Override
	public void validar(Pergunta pergunta) {

		// Validando campos vazios
		if (pergunta.getTexto() == null || pergunta.getTexto().trim().equals("")) {
			throw new RegraNegocioException("Informe uma texto válido.");
		}

		if (pergunta.getTitulo() == null || pergunta.getTitulo().trim().equals("")) {
			throw new RegraNegocioException("Informe um título válido.");
		}

		// Validando tamanho dos campos
		if (pergunta.getTexto().length() > 1000) {
			throw new RegraNegocioException("Texto muito longo.");
		}

		if (pergunta.getTitulo().length() > 50) {
			throw new RegraNegocioException("Título deve ter menos que 50 caracteres.");
		}
		
		for (var palavraRuim : palavraRuimRepository.findAll()) {
			
			if(pergunta.getTitulo().toLowerCase().contains(palavraRuim.getPalavra()))
				throw new RegraNegocioException("Por favor, não utilize palavrões.");
			
			if(pergunta.getTexto().toLowerCase().contains(palavraRuim.getPalavra()))
				throw new RegraNegocioException("Por favor, não utilize palavrões.");
				
		}	
	}

	@Override
	public Optional<Pergunta> obterPorId(Long id) {
		var pergunta = repository.findById(id);
		if (!pergunta.isPresent()) {
			throw new RegraNegocioException("Pergunta não existe na base de dados");
		}
		var perguntaConvertida = new ArrayList<Pergunta>();
		perguntaConvertida.add(pergunta.get());

		totalCurtidas(perguntaConvertida);
		totalResposta(perguntaConvertida);
		totalVisualizacao(perguntaConvertida);
		populaTags(perguntaConvertida);

		return pergunta;
	}

	@Override
	@Transactional
	public boolean curtir(CurtePerg curtePerg) {
		var curtePergEncontrado = curtePergRepository.findByUsuarioAndPergunta(curtePerg.getUsuario(),
				curtePerg.getPergunta());
		if (!curtePergEncontrado.isPresent()) {
			curtePergRepository.save(curtePerg);
			return true;
		} else {
			curtePergRepository.deleteById(curtePergEncontrado.get().getId());
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
	public void totalVisualizacao(List<Pergunta> perguntas) {
		for (Pergunta pergunta : perguntas) {
			var total = visualizacaoRepository.countByPergunta(pergunta);
			pergunta.setVisualizacao(total);
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

			var tags = tagPergRepository.findByPergunta(pergunta);
			var nomeTags = new ArrayList<String>();

			for (TagPerg tag : tags) {

				var tagEncontrada = tagRepository.findById(tag.getTag().getId());
				nomeTags.add(tagEncontrada.get().getNome());
			}
			pergunta.setTags(nomeTags);

		}

	}

	@Override
	public void verificarUsuario(Pergunta pergunta, Usuario usuarioRequisitando) {
		if (!pergunta.getUsuario().getEmail().equals(usuarioRequisitando.getEmail())
				&& !usuarioRequisitando.isAdmin()) {
			throw new RegraNegocioException("Não é possivel manter perguntas que não sejam suas.");
		}
	}

	@Override
	public List<Pergunta> obterPorUsuario(Usuario usuario) {

		var perguntas = repository.findByUsuario(usuario);
		totalCurtidas(perguntas);
		totalResposta(perguntas);
		populaTags(perguntas);

		return perguntas;
	}

	@Override
	public void reportar(ReportaPergunta report) {
		reportaPerguntaRepository.save(report);
	}

	@Override
	public List<ReportaPergunta> obterPerguntasReportadas() {
		var perguntasReportadas = reportaPerguntaRepository.findAll();
		return perguntasReportadas;
	}

	@Override
	public boolean somarVisualizacao(Visualizacao visualizacao) {
		var visualizacaoEncontrada = visualizacaoRepository.findByUsuarioAndPergunta(visualizacao.getUsuario(), visualizacao.getPergunta());
		if (!visualizacaoEncontrada.isPresent()) {
			visualizacaoRepository.save(visualizacao);
			return true;
		} else {
			return false;
		}
	}

}
