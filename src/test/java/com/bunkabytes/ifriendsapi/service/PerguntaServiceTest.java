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

import com.bunkabytes.ifriendsapi.model.entity.CurtePerg;
import com.bunkabytes.ifriendsapi.model.entity.ImagemPerg;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.Tag;
import com.bunkabytes.ifriendsapi.model.entity.TagPerg;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.entity.Visualizacao;
import com.bunkabytes.ifriendsapi.model.repository.CurtePergRepository;
import com.bunkabytes.ifriendsapi.model.repository.ImagemPergRepository;
import com.bunkabytes.ifriendsapi.model.repository.PerguntaRepository;
import com.bunkabytes.ifriendsapi.model.repository.RespostaRepository;
import com.bunkabytes.ifriendsapi.model.repository.TagPergRepository;
import com.bunkabytes.ifriendsapi.model.repository.TagRepository;
import com.bunkabytes.ifriendsapi.model.repository.VisualizacaoRepository;
import com.bunkabytes.ifriendsapi.service.impl.PerguntaServiceImpl;

import lombok.var;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Profile("teste1")
public class PerguntaServiceTest {

	@SpyBean
	PerguntaServiceImpl service;

	@MockBean
	PerguntaRepository repository;
	@MockBean
	TagRepository tagRepository;
	@MockBean
	TagPergRepository tagPergRepository;
	@MockBean
	CurtePergRepository curtePergRepository;
	@MockBean
	RespostaRepository respostaRepository;
	@MockBean
	ImagemPergRepository imagemPergRepository;
	@MockBean
	VisualizacaoRepository visualizacaoRepository;

	@Test
	public void deveSalvarUmaPergunta() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var perguntaASalvar = criarPergunta();
			var imagens = new ArrayList<ImagemPerg>();
			imagens.add(criarImagemPerg());
			
			var tags = new ArrayList<String>();
			tags.add("AW2");
			perguntaASalvar.setTags(tags);
			
			Mockito.doNothing().when(service).validar(perguntaASalvar);
			Mockito.doNothing().when(service).salvarTag(perguntaASalvar);

			var perguntaSalva = criarPergunta();
			perguntaSalva.setId(1l);
			Mockito.when(repository.save(perguntaASalvar)).thenReturn(perguntaSalva);
			Mockito.when(imagemPergRepository.existsByLink("IMG1")).thenReturn(false);
			
			// ação
			var pergunta = service.salvar(perguntaASalvar, imagens);

			// verificação
			Assertions.assertEquals(pergunta.getId(), perguntaSalva.getId());
			Assertions.assertEquals(pergunta.getRespondida(), null);
		});
	}

	@Test
	public void naoDeveSalvarUmaPerguntaQuandoHouverErroDeValidacao() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			// cenário
			var imagens = new ArrayList<ImagemPerg>();
			var perguntaASalvar = criarPergunta();
			var tags = new ArrayList<String>();
			tags.add("AW2");
			perguntaASalvar.setTags(tags);
			Mockito.doThrow(RegraNegocioException.class).when(service).validar(perguntaASalvar);
			Mockito.doNothing().when(service).salvarTag(perguntaASalvar);

			var perguntaSalva = criarPergunta();
			perguntaSalva.setId(1l);
			Mockito.when(repository.save(perguntaASalvar)).thenReturn(perguntaSalva);

			// ação
			service.salvar(perguntaASalvar, imagens);

			// verificação
			Mockito.verify(repository, Mockito.never()).save(perguntaASalvar);
		});
	}

	@Test
	public void deveSalvarTagDaPerguntaCasoNaoExista() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var listaTag = new ArrayList<String>();
			listaTag.add("AW2");

			var pergunta = criarPergunta();
			pergunta.setTags(listaTag);

			var tag = criarTag();
			tag.setId(1l);

			Mockito.when(tagRepository.findByNome("AW2")).thenReturn(Optional.of(tag));
			Mockito.when(tagRepository.save(tag)).thenReturn(tag);

			// ação
			service.salvarTag(pergunta);

			// verificação
			Mockito.verify(tagRepository, Mockito.never()).save(tag);
			Mockito.verify(tagPergRepository).save(Mockito.any());
		});
	}

	@Test
	public void naoDeveSalvarTagDaPerguntaCasoExista() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var listaTag = new ArrayList<String>();
			listaTag.add("AW2");

			var pergunta = criarPergunta();
			pergunta.setTags(listaTag);

			var tag = criarTag();

			Mockito.when(tagRepository.findByNome("AW2")).thenReturn(Optional.empty());
			Mockito.when(tagRepository.save(tag)).thenReturn(tag);

			// ação
			service.salvarTag(pergunta);

			// verificação
			Mockito.verify(tagRepository).save(tag);
			Mockito.verify(tagPergRepository).save(Mockito.any());
		});
	}
	
	@Test
	public void deveLancarErroAoUltarapassarLimiteDeTags() {
		
		// cenário
		var listaTag = new ArrayList<String>();
		listaTag.add("1");
		listaTag.add("2");
		listaTag.add("3");
		listaTag.add("4");
		listaTag.add("5");
		listaTag.add("6");
		listaTag.add("7");
		listaTag.add("8");
		listaTag.add("9");
		listaTag.add("10");
		listaTag.add("11");

		var pergunta = criarPergunta();
		pergunta.setTags(listaTag);

		// ação
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.salvarTag(pergunta);
		});

		// verificação
		Mockito.verify(tagRepository, Mockito.never()).save(Mockito.any());
		Mockito.verify(tagPergRepository, Mockito.never()).save(Mockito.any());
		
	}

	@Test
	public void deveAtualizarUmaPerguntaComSucesso() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var perguntaAAtualizar = criarPergunta();
			perguntaAAtualizar.setId(1l);
			var modificacoes = new Pergunta();
			modificacoes.setId(1l);
			modificacoes.setTexto("textoAtt");
			modificacoes.setTitulo("tituloAtt");

			Mockito.doNothing().when(service).validar(perguntaAAtualizar);
			Mockito.when(repository.findById(perguntaAAtualizar.getId())).thenReturn(Optional.of(perguntaAAtualizar));
			Mockito.when(repository.save(perguntaAAtualizar)).thenReturn(perguntaAAtualizar);

			// ação
			perguntaAAtualizar = service.atualizar(modificacoes);

			// verificação
			Mockito.verify(repository, Mockito.times(1)).save(perguntaAAtualizar);
			Assertions.assertEquals(perguntaAAtualizar.getTexto(), "textoAtt");
			Assertions.assertEquals(perguntaAAtualizar.getTitulo(), "tituloAtt");
		});
	}

	@Test
	public void naoDeveAtualizarUmaPerguntaSemId() {
		Assertions.assertThrows(NullPointerException.class, () -> {
			// cenário
			Pergunta perguntaAAtualizar = criarPergunta();
			Mockito.doNothing().when(service).validar(perguntaAAtualizar);
			Mockito.when(repository.save(perguntaAAtualizar)).thenReturn(perguntaAAtualizar);

			// ação
			service.atualizar(perguntaAAtualizar);

			// verificação
			Mockito.verify(repository, Mockito.never()).save(perguntaAAtualizar);
		});
	}

	@Test
	public void deveDeletarUmaPergunta() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			Pergunta perguntaADeletar = criarPergunta();
			perguntaADeletar.setId(1l);
			Mockito.when(repository.save(perguntaADeletar)).thenReturn(perguntaADeletar);

			// ação
			service.deletar(perguntaADeletar);

			// verificação
			Mockito.verify(repository).save(perguntaADeletar);
			Assertions.assertEquals(perguntaADeletar.isDeletado(), true);
		});
	}

	@Test
	public void naoDeveDeletarUmaPerguntaSemId() {
		// cenário
		Pergunta pergunta = criarPergunta();

		// ação
		Assertions.assertThrows(NullPointerException.class, () -> {
			service.deletar(pergunta);
		});

		// verificação
		Mockito.verify(repository, Mockito.never()).save(pergunta);
		Assertions.assertNotEquals(pergunta.isDeletado(), true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deveFiltrarPergunta() {

		// cenário
		var pergunta = criarPergunta();
		pergunta.setId(1l);
		var listaTag = new ArrayList<String>();
		listaTag.add("AW2");
		pergunta.setTags(listaTag);
		
		var pergunta2 = criarPergunta();
		pergunta2.setId(2l);
		pergunta2.setQtdResposta(1l);
		
		var pergunta3 = criarPergunta();
		pergunta.setId(3l);
		var listaTag2 = new ArrayList<String>();
		listaTag2.add("LP3");
		pergunta.setTags(listaTag);
		
		var lista = new ArrayList<Pergunta>();
		lista.add(pergunta);
		lista.add(pergunta2);
		lista.add(pergunta3);

		Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(Sort.class))).thenReturn(lista);

		Mockito.doNothing().when(service).totalCurtidas(lista);
		Mockito.doNothing().when(service).totalResposta(lista);
		Mockito.doNothing().when(service).totalVisualizacao(lista);
		Mockito.doNothing().when(service).populaTags(lista);

		// ação
		var resultado = service.buscar(pergunta, true, "AW2", "titulo", true);

		// verificação
		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertTrue(resultado.size() == 1);
		Assertions.assertTrue(resultado.contains(pergunta));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveFiltrarPerguntaPadrao() {

		// cenário
		var pergunta = criarPergunta();
		pergunta.setId(1l);
		var listaTag = new ArrayList<String>();
		listaTag.add("AW2");
		pergunta.setTags(listaTag);
		
		var lista = new ArrayList<Pergunta>();
		lista.add(pergunta);

		Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(Sort.class))).thenReturn(lista);

		Mockito.doNothing().when(service).totalCurtidas(lista);
		Mockito.doNothing().when(service).totalResposta(lista);
		Mockito.doNothing().when(service).totalVisualizacao(lista);
		Mockito.doNothing().when(service).populaTags(lista);

		// ação
		var resultado = service.buscar(pergunta, null, "AW2", null, false);

		// verificação
		Assertions.assertFalse(resultado.isEmpty());
	}

	@Test
	public void deveFecharPergunta() {

		// cenário
		var pergunta = criarPergunta();
		pergunta.setId(1l);
		pergunta.setRespondida(false);
		
		Mockito.when(repository.save(pergunta)).thenReturn(null);

		// ação
		var msg = service.atualizarStatus(pergunta);

		// verificação
		Assertions.assertEquals(msg, "Pergunta aberta com sucesso!");
	}
	
	@Test
	public void naoDeveFecharPergunta() {

		// cenário
		var pergunta = criarPergunta();
		pergunta.setId(1l);
		pergunta.setRespondida(true);
		
		Mockito.when(repository.save(pergunta)).thenReturn(null);

		// ação
		var msg = service.atualizarStatus(pergunta);

		// verificação
		Assertions.assertEquals(msg, "Pergunta fechada com sucesso!");
	}

	@Test
	public void deveObterPerguntaPorId() {

		// cenário
		var id = 1l;
		var pergunta = criarPergunta();
		pergunta.setId(1l);

		var lista = new ArrayList<Pergunta>();

		Mockito.when(repository.findById(1l)).thenReturn(Optional.of(pergunta));

		Mockito.doNothing().when(service).totalCurtidas(lista);
		Mockito.doNothing().when(service).totalVisualizacao(lista);
		Mockito.doNothing().when(service).totalResposta(lista);
		Mockito.doNothing().when(service).populaTags(lista);

		// ação
		Optional<Pergunta> resultado = service.obterPorId(id);

		// verificação
		Assertions.assertTrue(resultado.isPresent());
	}

	@Test
	public void deveLancarErroAoObterPerguntaSemId() {

		// cenário
		Long id = 1l;
		var lista = new ArrayList<Pergunta>();

		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		Mockito.doNothing().when(service).totalCurtidas(lista);
		Mockito.doNothing().when(service).totalVisualizacao(lista);
		Mockito.doNothing().when(service).totalResposta(lista);
		Mockito.doNothing().when(service).populaTags(lista);

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> service.obterPorId(id));
	}

	@Test
	public void devePreencherAsTagsDasPerguntas() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var pergunta = criarPergunta();
			pergunta.setId(1l);
			var listaPergunta = new ArrayList<Pergunta>();
			listaPergunta.add(pergunta);

			var tagPerg = criarTagPerg();
			tagPerg.setPergunta(pergunta);
			var tags = new ArrayList<TagPerg>();
			tags.add(tagPerg);

			var tagEncontrada = criarTag();

			Mockito.when(tagPergRepository.findByPergunta(pergunta)).thenReturn(tags);
			Mockito.when(tagRepository.findById(1l)).thenReturn(Optional.of(tagEncontrada));

			// ação
			service.populaTags(listaPergunta);

			// verificação
			Mockito.verify(tagPergRepository).findByPergunta(pergunta);
			Mockito.verify(tagRepository).findById(1l);
		});

	}

	@Test
	public void devePreencherTotalDeCurtidas() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var total = 1l;
			var pergunta = criarPergunta();
			pergunta.setId(1l);

			var listaPergunta = new ArrayList<Pergunta>();
			listaPergunta.add(pergunta);

			Mockito.when(curtePergRepository.countByPergunta(pergunta)).thenReturn(total);

			// ação
			service.totalCurtidas(listaPergunta);

			// verificação
			Mockito.verify(curtePergRepository).countByPergunta(pergunta);
		});

	}

	@Test
	public void devePreencherTotalDeRespostas() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var total = 1l;
			var pergunta = criarPergunta();
			pergunta.setId(1l);

			var listaPergunta = new ArrayList<Pergunta>();
			listaPergunta.add(pergunta);

			Mockito.when(respostaRepository.countRespostaByPergunta(pergunta)).thenReturn(total);

			// ação
			service.totalResposta(listaPergunta);

			// verificação
			Mockito.verify(respostaRepository).countRespostaByPergunta(pergunta);
		});

	}

	@Test
	public void deveTratarCorretamenteAoValidarPergunta() {

		// cenário
		var pergunta = new Pergunta();

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(pergunta);
		});

		pergunta.setTexto("");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(pergunta);
		});
		
	    char[] texto = new char[1001];
	    Arrays.fill(texto, 'a');
		pergunta.setTexto(new String(texto));
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(pergunta);
		});

		pergunta.setTitulo("");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(pergunta);
		});
		
	    char[] titulo = new char[51];
	    Arrays.fill(titulo, 'a');
		pergunta.setTitulo(new String(titulo));
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(pergunta);
		});
		
		pergunta.setTexto("TEXTO");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(pergunta);
		});
		
		pergunta.setTitulo("TITULO");
		Assertions.assertDoesNotThrow(() -> {
			service.validar(pergunta);
		});

	}

	@Test
	public void deveCurtirUmaPergunta() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var curtePerg = criarCurtePerg();
			Mockito.when(curtePergRepository.findByUsuarioAndPergunta(Mockito.any(Usuario.class),
					Mockito.any(Pergunta.class))).thenReturn(Optional.empty());

			// ação e verificação
			boolean resultado = service.curtir(curtePerg);

			Mockito.verify(curtePergRepository).save(Mockito.any(CurtePerg.class));
			Mockito.verify(curtePergRepository, Mockito.never()).deleteById(1l);
			Assertions.assertEquals(resultado, true);
		});

	}

	@Test
	public void deveDesCurtirUmaPergunta() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var curtePerg = criarCurtePerg();
			curtePerg.setId(1l);
			Mockito.when(curtePergRepository.findByUsuarioAndPergunta(Mockito.any(Usuario.class),
					Mockito.any(Pergunta.class))).thenReturn(Optional.of(curtePerg));

			// ação e verificação
			boolean resultado = service.curtir(curtePerg);

			Mockito.verify(curtePergRepository, Mockito.never()).save(curtePerg);
			Mockito.verify(curtePergRepository).deleteById(1l);
			Assertions.assertEquals(resultado, false);
		});

	}
	
	@Test
	public void deveSomarVisualizacao() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var visualizacao = criarVisualizacaoPerg();
			visualizacao.setId(1l);
			Mockito.when(visualizacaoRepository.findByUsuarioAndPergunta(Mockito.any(Usuario.class),
					Mockito.any(Pergunta.class))).thenReturn(Optional.empty());

			// ação e verificação
			boolean resultado = service.somarVisualizacao(visualizacao);

			Mockito.verify(visualizacaoRepository).save(Mockito.any(Visualizacao.class));
			Assertions.assertEquals(resultado, true);
		});

	}
	
	@Test
	public void naoDeveFazerNadaAoSomarVisualizacaoQueJaFoiVista() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var visualizacao = criarVisualizacaoPerg();
			visualizacao.setId(1l);
			Mockito.when(visualizacaoRepository.findByUsuarioAndPergunta(Mockito.any(Usuario.class),
					Mockito.any(Pergunta.class))).thenReturn(Optional.of(visualizacao));

			// ação e verificação
			boolean resultado = service.somarVisualizacao(visualizacao);

			Mockito.verify(visualizacaoRepository, Mockito.never()).save(Mockito.any(Visualizacao.class));
			Assertions.assertEquals(resultado, false);
		});

	}

	@Test
	public void deveRetonarErroCasoPerguntaNaoPertenceAoUsuario() {

		// cenário
		var pergunta = criarPergunta();
		pergunta.setId(1l);
		pergunta.getUsuario().setEmail("erro@gmail.com");

		var usuario = UsuarioServiceTest.criarUsuario();
		usuario.setId(1l);
		usuario.setEmail("teste@gmail.com");

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.verificarUsuario(pergunta, usuario);
		});

	}

	@Test
	public void deveVerificarSePerguntaPertenceAoUsuarioComSucesso() {

		// cenário
		var pergunta = criarPergunta();
		pergunta.setId(1l);
		pergunta.getUsuario().setEmail("teste@gmail.com");

		var usuario = UsuarioServiceTest.criarUsuario();
		usuario.setId(1l);
		usuario.setEmail("teste@gmail.com");

		// ação e verificação
		Assertions.assertDoesNotThrow(() -> {
			service.verificarUsuario(pergunta, usuario);
		});

	}
	
	@Test
	public void deveObterPerguntasPorUsuario() {
		//cenário
		var perguntas = new ArrayList<Pergunta>();
		perguntas.add(criarPergunta());
		
		var usuario = UsuarioServiceTest.criarUsuario();
		Mockito.when(repository.findByUsuario(usuario)).thenReturn(perguntas);
		
		//ação
		service.obterPorUsuario(usuario);
		
	}

	public static Pergunta criarPergunta() {

		return Pergunta.builder().titulo("testes no Java").texto("Como realizar testes no JUnit 5")
				.dataEmissao(LocalDateTime.now()).deletado(false).usuario(UsuarioServiceTest.criarUsuario())
				.categoria(CategoriaServiceTest.criarCategoria()).tags(new ArrayList<String>()).qtdResposta(0l).build();
	}

	public static TagPerg criarTagPerg() {
		var pergunta = criarPergunta();
		pergunta.setId(1l);
		var tag = criarTag();
		tag.setId(1l);
		return TagPerg.builder().id(1l).pergunta(pergunta).tag(tag).build();
	}

	public static CurtePerg criarCurtePerg() {
		var pergunta = criarPergunta();
		pergunta.setId(1l);
		var usuario = UsuarioServiceTest.criarUsuario();
		usuario.setId(1l);
		return CurtePerg.builder().pergunta(pergunta).usuario(usuario).build();
	}
	
	public static ImagemPerg criarImagemPerg() {
		var pergunta = criarPergunta();
		pergunta.setId(1l);
		return ImagemPerg.builder().id(1l).pergunta(pergunta).link("IMG1").build();
	}

	public static Tag criarTag() {
		return Tag.builder().nome("AW2").build();
	}
	
	 public static Visualizacao criarVisualizacaoPerg(){
		 return Visualizacao.builder().usuario(UsuarioServiceTest.criarUsuario()).pergunta(criarPergunta()).build();
	 }

}
