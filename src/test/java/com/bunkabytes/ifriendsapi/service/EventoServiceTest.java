package com.bunkabytes.ifriendsapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.Evento;
import com.bunkabytes.ifriendsapi.model.entity.FavoritaEvento;
import com.bunkabytes.ifriendsapi.model.entity.ImagemEvento;
import com.bunkabytes.ifriendsapi.model.entity.TagEvento;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.repository.EventoRepository;
import com.bunkabytes.ifriendsapi.model.repository.FavoritaEventoRepository;
import com.bunkabytes.ifriendsapi.model.repository.ImagemPergRepository;
import com.bunkabytes.ifriendsapi.model.repository.TagEventoRepository;
import com.bunkabytes.ifriendsapi.model.repository.TagRepository;
import com.bunkabytes.ifriendsapi.service.impl.EventoServiceImpl;

import lombok.var;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Profile("teste1")
public class EventoServiceTest {
	
	@SpyBean
	EventoServiceImpl service;
	
	@MockBean
	EventoRepository repository;
	@MockBean
	TagRepository tagRepository;
	@MockBean
	TagEventoRepository tagEventoRepository;
	@MockBean
	ImagemPergRepository imagemPergRepository;
	@MockBean
	FavoritaEventoRepository favoritaEventoRepository;
	
	
	@Test
	public void deveSalvarUmEvento() {
		Assertions.assertDoesNotThrow(() ->{
			
			//cenário
			var eventoASalvar = criarEvento();
			var imagens = new ArrayList<ImagemEvento>();
			
			Mockito.doNothing().when(service).validar(eventoASalvar);
			Mockito.doNothing().when(service).salvarTag(eventoASalvar);
			
			var eventoSalvo= eventoASalvar;
			eventoSalvo.setId(1l);
			Mockito.when(repository.save(eventoASalvar)).thenReturn(eventoSalvo);
			
			//ação
			var evento = service.salvar(eventoASalvar, imagens);
			
			// verificação
			Assertions.assertEquals(evento.getId(), eventoASalvar.getId());
		});
	}
	
	@Test
	public void deveAtualizarUmEventoComSucesso() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var categoria = CategoriaServiceTest.criarCategoria();
			var dataEvento = LocalDateTime.now();
			var eventoAAtualizar = criarEvento();
			eventoAAtualizar.setId(1l);
			var modificacoes = new Evento();
			modificacoes.setId(1l);
			modificacoes.setNome("evento");
			modificacoes.setDataEvento(dataEvento);
			modificacoes.setPresencial(false);
			modificacoes.setLink("https://xurume");
			modificacoes.setDescricao("descricao");
			modificacoes.setLocal("local");
			modificacoes.setCategoria(categoria);

			Mockito.doNothing().when(service).validar(modificacoes);
			Mockito.when(repository.findById(eventoAAtualizar.getId())).thenReturn(Optional.of(eventoAAtualizar));
			Mockito.when(repository.save(eventoAAtualizar)).thenReturn(eventoAAtualizar);

			// ação
			eventoAAtualizar = service.atualizar(modificacoes);

			// verificação
			Mockito.verify(repository, Mockito.times(1)).save(eventoAAtualizar);
			Assertions.assertEquals(eventoAAtualizar.getNome(), "evento");
			Assertions.assertEquals(eventoAAtualizar.getDataEvento(), dataEvento);
			Assertions.assertEquals(eventoAAtualizar.getPresencial(), false);
			Assertions.assertEquals(eventoAAtualizar.getLink(), "https://xurume");
			Assertions.assertEquals(eventoAAtualizar.getDescricao(), "descricao");
			Assertions.assertEquals(eventoAAtualizar.getLocal(), "local");
			Assertions.assertEquals(eventoAAtualizar.getCategoria(), categoria);
		});
	}
	
	@Test
	public void nãoDeveSalvarUmEventoQuandoHouverErroDeValidacao() {
		Assertions.assertThrows(RegraNegocioException.class, () ->{
			
			//cenário
			var eventoASalvar = criarEvento();
			var imagens = new ArrayList<ImagemEvento>();
			imagens.add(criarImagemEvento());
			
			Mockito.doThrow(RegraNegocioException.class).when(service).validar(eventoASalvar);
			Mockito.doNothing().when(service).salvarTag(eventoASalvar);
			
			var perguntaSalva = eventoASalvar;
			perguntaSalva.setId(1l);
			Mockito.when(repository.save(eventoASalvar)).thenReturn(perguntaSalva);
			
			//ação
			service.salvar(eventoASalvar, imagens);
			
			// verificação
			Mockito.verify(repository, Mockito.never()).save(eventoASalvar);
		});
	}
	
	@Test
	public void deveSalvarTagDaPerguntaCasoNaoExista() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var listaTag = new ArrayList<String>();
			listaTag.add("AW2");

			var evento = criarEvento();
			evento.setTags(listaTag);

			var tag = PerguntaServiceTest.criarTag();
			tag.setId(1l);

			Mockito.when(tagRepository.findByNome("AW2")).thenReturn(Optional.of(tag));
			Mockito.when(tagRepository.save(tag)).thenReturn(tag);

			// ação
			service.salvarTag(evento);

			// verificação
			Mockito.verify(tagRepository, Mockito.never()).save(tag);
			Mockito.verify(tagEventoRepository).save(Mockito.any());
		});
	}

	@Test
	public void naoDeveSalvarTagDaPerguntaCasoExista() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var listaTag = new ArrayList<String>();
			listaTag.add("AW2");

			var evento = criarEvento();
			evento.setTags(listaTag);

			var tag = PerguntaServiceTest.criarTag();

			Mockito.when(tagRepository.findByNome("AW2")).thenReturn(Optional.empty());
			Mockito.when(tagRepository.save(tag)).thenReturn(tag);

			// ação
			service.salvarTag(evento);

			// verificação
			Mockito.verify(tagRepository).save(tag);
			Mockito.verify(tagEventoRepository).save(Mockito.any());
		});
	}
	
	@Test
	public void deveObterEventoPorId() {

		// cenário
		Long id = 1l;
		var evento = criarEvento();
		evento.setId(1l);

		var lista = new ArrayList<Evento>();

		Mockito.when(repository.findById(1l)).thenReturn(Optional.of(evento));

		Mockito.doNothing().when(service).populaTags(lista);

		// ação
		var resultado = service.obterPorId(id);

		// verificação
		Assertions.assertTrue(resultado.isPresent());
	}
	
	@Test
	public void deveLancarErroAoObterEventoSemId() {

		// cenário
		Long id = 1l;
		var lista = new ArrayList<Evento>();

		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		Mockito.doNothing().when(service).populaTags(lista);

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> service.obterPorId(id));
	}
	
	@Test
	public void devePreencherAsTagsDasEventos() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var evento = criarEvento();
			evento.setId(1l);
			var listaEvento = new ArrayList<Evento>();
			listaEvento.add(evento);

			var tagPerg = criarTagEvento();
			tagPerg.setEvento(evento);
			var tags = new ArrayList<TagEvento>();
			tags.add(tagPerg);

			var tagEncontrada = PerguntaServiceTest.criarTag();

			Mockito.when(tagEventoRepository.findByEvento(evento)).thenReturn(tags);
			Mockito.when(tagRepository.findById(1l)).thenReturn(Optional.of(tagEncontrada));

			// ação
			service.populaTags(listaEvento);

			// verificação
			Mockito.verify(tagEventoRepository).findByEvento(evento);
			Mockito.verify(tagRepository).findById(1l);
		});
	}
	
	@Test
	public void deveTrataCorretamenteAoValidarEvento() {

		// cenário
		var evento = new Evento();

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(evento);
		});

		evento.setNome("");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(evento);
		});
		
	    char[] nome = new char[101];
	    Arrays.fill(nome, 'a');
		evento.setNome(new String(nome));
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(evento);
		});

		evento.setLocal("");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(evento);
		});
		
	    char[] local = new char[151];
	    Arrays.fill(local, 'a');
		evento.setLocal(new String(local));
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(evento);
		});
		
		evento.setDataEvento(LocalDateTime.now());
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(evento);
		});
		
		evento.setDescricao("");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(evento);
		});
		
	    char[] descricao = new char[1001];
	    Arrays.fill(descricao, 'a');
		evento.setDescricao(new String(descricao));
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(evento);
		});
		
		evento.setNome("evento");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(evento);
		});
		
		evento.setLocal("Local");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(evento);
		});
		
		evento.setDescricao("Descricao");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(evento);
		});
		
		evento.setDataEvento(LocalDateTime.now().plusDays(1));
		Assertions.assertDoesNotThrow(() -> {
			service.validar(evento);
		});

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveFiltrarEvento() {

		// cenário
		var evento = criarEvento();
		evento.setId(1l);
		var listaTag = new ArrayList<String>();
		listaTag.add("AW2");
		evento.setTags(listaTag);
		
		var evento2 = criarEvento();
		evento2.setId(1l);
		
		var lista = new ArrayList<Evento>();
		lista.add(evento);
		lista.add(evento2);
		
		Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(Sort.class))).thenReturn(lista);

		Mockito.doNothing().when(service).populaTags(lista);

		// ação
		var resultado = service.buscar(evento, "AW2");

		// verificação
		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertTrue(resultado.size() == 1);
		Assertions.assertTrue(resultado.contains(evento));
	}
	
	@Test
	public void deveRetonarErroCasoEventoNaoPertenceAoUsuario() {

		// cenário
		var evento = criarEvento();
		evento.setId(1l);
		evento.getUsuario().setEmail("erro@gmail.com");

		var usuario = UsuarioServiceTest.criarUsuario();
		usuario.setId(1l);
		usuario.setEmail("teste@gmail.com");

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.verificarUsuario(evento, usuario);
		});

	}

	@Test
	public void deveVerificarSeEventoPertenceAoUsuarioComSucesso() {

		// cenário
		var evento = criarEvento();
		evento.setId(1l);
		evento.getUsuario().setEmail("teste@gmail.com");

		var usuario = UsuarioServiceTest.criarUsuario();
		usuario.setId(1l);
		usuario.setEmail("teste@gmail.com");

		// ação e verificação
		Assertions.assertDoesNotThrow(() -> {
			service.verificarUsuario(evento, usuario);
		});

	}
	
	@Test
	public void deveFavoritarUmEvento() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var favoritaEvento = criarFavoritaEvento();
			Mockito.when(favoritaEventoRepository.findByUsuarioAndEvento(Mockito.any(Usuario.class),
					Mockito.any(Evento.class))).thenReturn(Optional.empty());

			// ação e verificação
			var resultado = service.favoritar(favoritaEvento);

			Mockito.verify(favoritaEventoRepository).save(Mockito.any(FavoritaEvento.class));
			Mockito.verify(favoritaEventoRepository, Mockito.never()).deleteById(1l);
			Assertions.assertEquals(resultado, true);
		});

	}

	@Test
	public void deveDesFavoritarUmEvento() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var favoritaEvento = criarFavoritaEvento();
			favoritaEvento.setId(1l);
			Mockito.when(favoritaEventoRepository.findByUsuarioAndEvento(Mockito.any(Usuario.class),
					Mockito.any(Evento.class))).thenReturn(Optional.of(favoritaEvento));

			// ação e verificação
			var resultado = service.favoritar(favoritaEvento);

			Mockito.verify(favoritaEventoRepository, Mockito.never()).save(favoritaEvento);
			Mockito.verify(favoritaEventoRepository).deleteById(1l);
			Assertions.assertEquals(resultado, false);
		});

	}
	
	public static Evento criarEvento(){
		return Evento.builder()
				.nome("Evento")
				.local("IFSP")
				.dataEvento(LocalDateTime.now())
				.descricao("Diversão")
				.presencial(true)
				.usuario(UsuarioServiceTest.criarUsuario())
				.tags(new ArrayList<String>()).build();
	}
	
	public static TagEvento criarTagEvento() {
		var evento = criarEvento();
		evento.setId(1l);
		var tag = PerguntaServiceTest.criarTag();
		tag.setId(1l);
		return TagEvento.builder().id(1l).evento(evento).tag(tag).build();
	}
	
	public static FavoritaEvento criarFavoritaEvento() {
		var evento = criarEvento();
		evento.setId(1l);
		var usuario = UsuarioServiceTest.criarUsuario();
		usuario.setId(1l);
		return FavoritaEvento.builder().evento(evento).usuario(usuario).build();
	}
	
	public static ImagemEvento criarImagemEvento() {
		var evento = criarEvento();
		evento.setId(1l);
		return ImagemEvento.builder().id(1l).evento(evento).link("IMG1").build();
	}

}
