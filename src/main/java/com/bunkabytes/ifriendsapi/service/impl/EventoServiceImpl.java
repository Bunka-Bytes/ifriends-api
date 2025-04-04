package com.bunkabytes.ifriendsapi.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.Evento;
import com.bunkabytes.ifriendsapi.model.entity.FavoritaEvento;
import com.bunkabytes.ifriendsapi.model.entity.ImagemEvento;
import com.bunkabytes.ifriendsapi.model.entity.ReportaEvento;
import com.bunkabytes.ifriendsapi.model.entity.Tag;
import com.bunkabytes.ifriendsapi.model.entity.TagEvento;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.repository.EventoRepository;
import com.bunkabytes.ifriendsapi.model.repository.FavoritaEventoRepository;
import com.bunkabytes.ifriendsapi.model.repository.ImagemEventoRepository;
import com.bunkabytes.ifriendsapi.model.repository.PalavraRuimRepository;
import com.bunkabytes.ifriendsapi.model.repository.ReportaEventoRepository;
import com.bunkabytes.ifriendsapi.model.repository.TagEventoRepository;
import com.bunkabytes.ifriendsapi.model.repository.TagRepository;
import com.bunkabytes.ifriendsapi.service.EventoService;

import lombok.var;

@Service
public class EventoServiceImpl implements EventoService {

	@Autowired
	private EventoRepository repository;
	@Autowired
	private TagEventoRepository tagEventoRepository;
	@Autowired
	private TagRepository tagRepository;
	@Autowired
	private ImagemEventoRepository imagemEventoRepository;
	@Autowired
	private FavoritaEventoRepository favoritaEventoRepository;
	@Autowired
	private ReportaEventoRepository reportaEventoRepository;
	@Autowired
	private PalavraRuimRepository palavraRuimRepository;

	@Override
	public Evento salvar(Evento evento, List<ImagemEvento> imagens) {
		validar(evento);
		var eventoSalvo = repository.save(evento);

		salvarTag(eventoSalvo);
		if (imagens != null)
			if(!imagens.isEmpty())
				if(imagens.size() > 5)
					throw new RegraNegocioException("No máximo 5 imagens por evento");
				else
					for (var imagem : imagens) {
						if (imagemEventoRepository.existsByLink(imagem.getLink())) {
							throw new RegraNegocioException("Link da imagem " + imagem.getLink() + " já está sendo utilizada");
						}
						imagem.setEvento(eventoSalvo);
						imagemEventoRepository.save(imagem);
					}
		return eventoSalvo;
	}

	@Override
	@Transactional
	public Evento atualizar(Evento evento) {
		Objects.requireNonNull(evento.getId());
		validar(evento);
		var eventoAAtualizar = repository.findById(evento.getId()).get();
		eventoAAtualizar.setNome(evento.getNome());
		eventoAAtualizar.setDataEvento(evento.getDataEvento());
		eventoAAtualizar.setPresencial(evento.getPresencial());
		eventoAAtualizar.setLink(evento.getLink());
		eventoAAtualizar.setDescricao(evento.getDescricao());
		eventoAAtualizar.setLocal(evento.getLocal());
		eventoAAtualizar.setCategoria(evento.getCategoria());
		return repository.save(eventoAAtualizar);
	}

	public void validar(Evento evento) {

		// Validando campos vazios
		if (evento.getNome() == null || evento.getNome().trim().equals("")) {
			throw new RegraNegocioException("Informe uma título válido.");
		}

		if (evento.getLocal() == null || evento.getLocal().trim().equals("")) {
			throw new RegraNegocioException("Informe um local válido.");
		}

		if (evento.getDataEvento() == null) {
			throw new RegraNegocioException("Informe uma data válida.");
		}

		if (evento.getDescricao() == null || evento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma descrição válida.");
		}

		// Validando tamanho dos campos
		if (evento.getNome().length() > 100) {
			throw new RegraNegocioException("Nome deve ter 100 ou menos caracteres.");
		}

		if (evento.getLocal().length() > 150) {
			throw new RegraNegocioException("Nome deve ter 150 ou menos caracteres.");
		}

		if (evento.getDescricao().length() > 1000) {
			throw new RegraNegocioException("Descricao deve ter 1000 ou menos caracteres.");
		}
		
		if (evento.getDataEvento().isBefore(LocalDateTime.now())) {
			throw new RegraNegocioException("A data do evento não pode ser antes da data de hoje.");
		}
		
		for (var palavraRuim : palavraRuimRepository.findAll()) {
			
			if(evento.getNome().toLowerCase().contains(palavraRuim.getPalavra()))
				throw new RegraNegocioException("Por favor, não utilize a palavra '"+palavraRuim.getPalavra()+"'.");	
			
			if(evento.getDescricao().toLowerCase().contains(palavraRuim.getPalavra()))
				throw new RegraNegocioException("Por favor, não utilize a palavra '"+palavraRuim.getPalavra()+"'.");			
		}
	}

	@Override
	@Transactional
	public void salvarTag(Evento evento) {
		
		if(evento.getTags().size() > 10)
			throw new RegraNegocioException("No máximo 10 tags por evento");

		for (String nomeTag : evento.getTags()) {
			var tag = new Tag();
			var tagEvento = new TagEvento();
			var tagExistente = tagRepository.findByNome(nomeTag);
			if (!tagExistente.isPresent()) {
				// Caso a tag não exista no banco de dados, é gravado normalmente
				tag.setNome(nomeTag);
				var tagSalvo = tagRepository.save(tag);
				tagEvento.setTag(tagSalvo);
			} else {
				// Caso a tag já exista, ela é atribuida ao evento sem ocorrer a gravação
				tagEvento.setTag(tagExistente.get());
			}

			tagEvento.setEvento(evento);
			tagEventoRepository.save(tagEvento);

		}
	}

	@Override
	public List<Evento> buscar(Evento eventoFiltro, String tag) {

		var example = Example.of(eventoFiltro, ExampleMatcher.matching().withIgnoreCase().withIgnoreNullValues()
				.withStringMatcher(StringMatcher.CONTAINING));
		var eventos = repository.findAll(example, Sort.by("dataEvento").ascending());
		populaTags(eventos);
		totalFavorito(eventos);

		if (tag != null) {
			for (int i = 0; i < eventos.size(); i++)
				if (!eventos.get(i).getTags().stream().anyMatch(tag::equalsIgnoreCase)) {
					eventos.remove(i);
					i = i - 1;
				}
		}

		return eventos;
	}

	@Override
	@Transactional
	public void populaTags(List<Evento> eventos) {

		for (Evento evento : eventos) {

			var tags = tagEventoRepository.findByEvento(evento);
			var nomeTags = new ArrayList<String>();

			for (TagEvento tag : tags) {

				var tagEncontrada = tagRepository.findById(tag.getTag().getId());
				nomeTags.add(tagEncontrada.get().getNome());
			}
			evento.setTags(nomeTags);

		}

	}
	
	@Override
	@Transactional
	public void totalFavorito(List<Evento> eventos) {
		for (var evento : eventos) {
			var total = favoritaEventoRepository.countByEvento(evento);
			evento.setQtdFavorito(total);
		}
	}

	@Override
	public Optional<Evento> obterPorId(Long id) {
		var evento = repository.findById(id);
		if (!evento.isPresent()) {
			throw new RegraNegocioException("Evento não existe na base de dados");
		}
		List<Evento> perguntaConvertida = new ArrayList<Evento>();
		perguntaConvertida.add(evento.get());
		populaTags(perguntaConvertida);
		totalFavorito(perguntaConvertida);

		return evento;
	}

	@Override
	public void verificarUsuario(Evento evento, Usuario usuarioRequisitando) {
		if (!evento.getUsuario().getEmail().equals(usuarioRequisitando.getEmail()) && !usuarioRequisitando.isAdmin()) {
			throw new RegraNegocioException("Não é possivel manter eventos que não sejam seus.");
		}
	}

	@Override
	public boolean favoritar(FavoritaEvento favoritaEvento) {
		var favoritaEventoEncontrado = favoritaEventoRepository.findByUsuarioAndEvento(favoritaEvento.getUsuario(),
				favoritaEvento.getEvento());
		if (!favoritaEventoEncontrado.isPresent()) {
			favoritaEventoRepository.save(favoritaEvento);
			return true;
		} else {
			favoritaEventoRepository.deleteById(favoritaEventoEncontrado.get().getId());
			return false;
		}
	}

	@Override
	public List<Evento> obterPorUsuario(Usuario usuario) {
		var eventos = repository.findByUsuario(usuario);
		populaTags(eventos);

		return eventos;
	}

	@Override
	public void reportar(ReportaEvento report) {
		reportaEventoRepository.save(report);
		
	}

}
