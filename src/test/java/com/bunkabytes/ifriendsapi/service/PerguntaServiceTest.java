package com.bunkabytes.ifriendsapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;

import com.bunkabytes.ifriendsapi.model.entity.CurtePerg;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.Tag;
import com.bunkabytes.ifriendsapi.model.entity.TagPerg;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.repository.CurtePergRepository;
import com.bunkabytes.ifriendsapi.model.repository.PerguntaRepository;
import com.bunkabytes.ifriendsapi.model.repository.RespostaRepository;
import com.bunkabytes.ifriendsapi.model.repository.TagPergRepository;
import com.bunkabytes.ifriendsapi.model.repository.TagRepository;
import com.bunkabytes.ifriendsapi.service.impl.PerguntaServiceImpl;

import lombok.var;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Profile("testes")
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

	@Test
	public void deveSalvarUmaPergunta() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			Pergunta perguntaASalvar = criarPergunta();
			Mockito.doNothing().when(service).validar(perguntaASalvar);
			Mockito.doNothing().when(service).salvarTag(perguntaASalvar);

			Pergunta perguntaSalva = criarPergunta();
			perguntaSalva.setId(1l);
			Mockito.when(repository.save(perguntaASalvar)).thenReturn(perguntaSalva);

			// ação
			Pergunta pergunta = service.salvar(perguntaASalvar);

			// verificação
			Assertions.assertEquals(pergunta.getId(), perguntaSalva.getId());
			Assertions.assertEquals(pergunta.isRespondida(), false);
		});
	}

	@Test
	public void naoDeveSalvarUmaPerguntaQuandoHouverErroDeValidacao() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			// cenário
			Pergunta perguntaASalvar = criarPergunta();
			Mockito.doThrow(RegraNegocioException.class).when(service).validar(perguntaASalvar);
			Mockito.doNothing().when(service).salvarTag(perguntaASalvar);

			Pergunta perguntaSalva = criarPergunta();
			perguntaSalva.setId(1l);
			Mockito.when(repository.save(perguntaASalvar)).thenReturn(perguntaSalva);

			// ação
			service.salvar(perguntaASalvar);

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

	@Test
	public void deveFiltrarPergunta() {

		// cenário
		Pergunta pergunta = criarPergunta();
		pergunta.setId(1l);

		var lista = new ArrayList<Pergunta>();
		lista.add(pergunta);

		String pesquisa = "pesquisa";
		Mockito.when(repository.findAllPesquisa(Mockito.anyString())).thenReturn(lista);

		Mockito.doNothing().when(service).totalCurtidas(lista);
		Mockito.doNothing().when(service).totalResposta(lista);
		Mockito.doNothing().when(service).populaTags(lista);

		// ação
		List<Pergunta> resultado = service.buscar(pesquisa);

		// verificação
		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertTrue(resultado.contains(pergunta));
	}

	@Test
	public void deveAtualizarStatusPergunta() {

		// cenário
		Pergunta pergunta = criarPergunta();
		pergunta.setId(1l);
		pergunta.setRespondida(false);

		boolean respondida = true;
		Mockito.doReturn(pergunta).when(service).atualizar(pergunta);

		// ação
		service.atualizarStatus(pergunta, respondida);

		// verificação
		Assertions.assertEquals(pergunta.isRespondida(), respondida);
		Mockito.verify(service).atualizar(pergunta);
	}

	@Test
	public void deveObterPerguntaPorId() {

		// cenário
		Long id = 1l;
		Pergunta pergunta = criarPergunta();
		pergunta.setId(1l);

		var lista = new ArrayList<Pergunta>();

		Mockito.when(repository.findById(1l)).thenReturn(Optional.of(pergunta));

		Mockito.doNothing().when(service).totalCurtidas(lista);
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
		Pergunta pergunta = criarPergunta();
		pergunta.setId(1l);

		var lista = new ArrayList<Pergunta>();

		Mockito.when(repository.findById(1l)).thenReturn(Optional.empty());

		Mockito.doNothing().when(service).totalCurtidas(lista);
		Mockito.doNothing().when(service).totalResposta(lista);
		Mockito.doNothing().when(service).populaTags(lista);

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> service.obterPorId(id));
	}

	@Test
	public void devePreencherAsTagsDasPerguntas() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			Pergunta pergunta = criarPergunta();
			pergunta.setId(1l);
			var listaPergunta = new ArrayList<Pergunta>();
			listaPergunta.add(pergunta);

			TagPerg tagPerg = criarTagPerg();
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
			Long total = 1l;
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
			Long total = 1l;
			Pergunta pergunta = criarPergunta();
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
	public void deveLancarTrataCorretamenteAoValidarPergunta() {

		// cenário
		Pergunta pergunta = new Pergunta();

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(pergunta);
		});

		pergunta.setTexto("");

		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(pergunta);
		});

		pergunta.setTexto("texto");

		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(pergunta);
		});

		pergunta.setTitulo("");

		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(pergunta);
		});
		pergunta.setTitulo("Titulo");

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
	public void deveRetonarErroCasoPerguntaNaoPertenceAoUsuario() {

		// cenário
		var pergunta = criarPergunta();
		pergunta.setId(1l);
		pergunta.getUsuario().setEmail("erro@gmail.com");
		var usuarioEmail = "teste@gmail.com";

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.verificarUsuario(pergunta, usuarioEmail);
		});

	}

	@Test
	public void deveVerificarSePerguntaPertenceAoUsuarioComSucesso() {

		// cenário
		var pergunta = criarPergunta();
		pergunta.setId(1l);
		pergunta.getUsuario().setEmail("teste@gmail.com");
		var usuarioEmail = "teste@gmail.com";

		// ação e verificação
		Assertions.assertDoesNotThrow(() -> {
			service.verificarUsuario(pergunta, usuarioEmail);
		});

	}

	@Test
	public void deveGravarVisualizacoesDaPergunta() {

		// cenário
		var id = 1l;

		// ação
		var result = service.gravarVisualizacao(id);

		// verificação
		Assertions.assertEquals(result, null);

	}

	public static Pergunta criarPergunta() {

		return Pergunta.builder().titulo("testes no Java").texto("Como realizar testes no JUnit 5")
				.dataEmissao(LocalDateTime.now()).deletado(false).usuario(UsuarioServiceTest.criarUsuario())
				.categoria(CategoriaServiceTest.criarCategoria()).tags(new ArrayList<String>()).build();
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

	public static Tag criarTag() {
		return Tag.builder().nome("AW2").build();
	}

}
