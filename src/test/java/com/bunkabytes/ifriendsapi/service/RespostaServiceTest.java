package com.bunkabytes.ifriendsapi.service;

import java.time.LocalDateTime;
import java.util.ArrayList;

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

import com.bunkabytes.ifriendsapi.model.entity.CurteResp;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.Resposta;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.repository.CurteRespRepository;
import com.bunkabytes.ifriendsapi.model.repository.RespostaRepository;

import com.bunkabytes.ifriendsapi.service.impl.RespostaServiceImpl;

import lombok.var;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Profile("testes")
public class RespostaServiceTest {

	@SpyBean
	RespostaServiceImpl service;

	@MockBean
	RespostaRepository repository;
	@MockBean
	CurteRespRepository curteRespRepository;

	@Test
	public void deveSalvarUmaResposta() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			Resposta respostaASalva = criarResposta();
			Mockito.doNothing().when(service).validar(respostaASalva);

			Resposta respostaSalva = criarResposta();
			respostaSalva.setId(1l);
			Mockito.when(repository.save(respostaASalva)).thenReturn(respostaSalva);

			// ação
			Resposta resposta = service.salvar(respostaASalva);

			// verificação
			Assertions.assertEquals(resposta.getId(), respostaSalva.getId());
			Assertions.assertEquals(resposta.isAceita(), false);

		});
	}

	@Test
	public void naoDeveSalvarUmaRespostaQuandoHouverErroDeValidacao() {

		// cenário
		Resposta respostaASalva = criarResposta();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(respostaASalva);

		Resposta respostaSalva = criarResposta();
		respostaSalva.setId(1l);
		Mockito.when(repository.save(respostaASalva)).thenReturn(respostaSalva);

		// ação
		Assertions.assertThrows(RegraNegocioException.class, () -> service.salvar(respostaASalva));

		// verificação
		Mockito.verify(repository, Mockito.never()).save(respostaASalva);

	}

	@Test
	public void deveObterRespostaPorId() {

		// cenário
		var resposta = criarResposta();
		Long id = 1l;
		resposta.setId(1l);

		Mockito.when(repository.findById(1l)).thenReturn(Optional.of(resposta));

		// ação
		var resultado = service.obterPorId(id);

		// verificação
		Assertions.assertEquals(resultado.get().getId(), 1l);
		Assertions.assertTrue(resultado.isPresent());
	}

	@Test
	public void deveLancarErroAoObterRespostaSemId() {
		// cenário
		Long id = 1l;
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.obterPorId(id);
		});

	}

	@Test
	public void deveAtualizarUmaRespostaComSucesso() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			Resposta respostaAAtualizar = criarResposta();
			respostaAAtualizar.setId(1l);
			Resposta modificacoes = new Resposta();
			modificacoes.setId(1l);
			modificacoes.setTexto("textoAtt");

			Mockito.doNothing().when(service).validar(respostaAAtualizar);
			Mockito.when(repository.findById(modificacoes.getId())).thenReturn(Optional.of(respostaAAtualizar));
			Mockito.when(repository.save(respostaAAtualizar)).thenReturn(respostaAAtualizar);

			// ação
			service.atualizar(modificacoes);

			// verificação
			Mockito.verify(repository, Mockito.times(1)).save(respostaAAtualizar);
			Assertions.assertEquals(respostaAAtualizar.getTexto(), "textoAtt");

		});
	}

	@Test
	public void naoDeveAtualizarUmaRespostaSemId() {
		Assertions.assertThrows(NullPointerException.class, () -> {
			// cenário
			Resposta respostaAAtualizar = criarResposta();

			Mockito.doNothing().when(service).validar(respostaAAtualizar);
			Mockito.when(repository.save(respostaAAtualizar)).thenReturn(respostaAAtualizar);

			// ação
			service.atualizar(respostaAAtualizar);

			// verificação
			Mockito.verify(repository, Mockito.never()).save(respostaAAtualizar);

		});
	}

	@Test
	public void deveDeletarUmaResposta() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			Resposta respostaADeletar = criarResposta();
			respostaADeletar.setId(1l);

			Mockito.when(repository.save(respostaADeletar)).thenReturn(respostaADeletar);

			// ação
			service.deletar(respostaADeletar);

			// verificação
			Mockito.verify(repository).save(respostaADeletar);
			Assertions.assertEquals(respostaADeletar.isDeletado(), true);
		});
	}

	@Test
	public void naoDeveDeletarUmaRespostaSemId() {
		Assertions.assertThrows(NullPointerException.class, () -> {
			// cenário
			Resposta respostaADeletar = criarResposta();

			Mockito.when(repository.save(respostaADeletar)).thenReturn(respostaADeletar);

			// ação
			service.deletar(respostaADeletar);

			// verificação
			Mockito.verify(repository, Mockito.never()).save(respostaADeletar);
			Assertions.assertEquals(respostaADeletar.isDeletado(), false);
		});
	}

	@Test
	public void deveFiltrarResposta() {

		// cenário
		var resposta = criarResposta();
		resposta.setId(1l);

		var listaRespostas = new ArrayList<Resposta>();
		listaRespostas.add(resposta);
		Mockito.when(repository.findByPergunta(Mockito.any(Pergunta.class))).thenReturn(listaRespostas);

		Mockito.doNothing().when(service).totalCurtidas(listaRespostas);

		// ação
		var resultado = service.buscar(resposta);

		// verificação
		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertTrue(resultado.contains(resposta));
	}

	@Test
	public void deveTratarCorretamenteUmaResposta() {
		// cenário
		Resposta resposta = new Resposta();

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(resposta);
		});
		resposta.setTexto("");

		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(resposta);
		});
		resposta.setTexto("texto");

		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(resposta);
		});
		resposta.setPergunta(PerguntaServiceTest.criarPergunta());

		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(resposta);
		});
		resposta.getPergunta().setId(1l);
		;

		Assertions.assertDoesNotThrow(() -> {
			service.validar(resposta);
		});

	}

	@Test
	public void deveCurtirUmaResposta() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var curteResp = criarCurteResp();
			Mockito.when(curteRespRepository.findByUsuarioAndResposta(Mockito.any(Usuario.class),
					Mockito.any(Resposta.class))).thenReturn(Optional.empty());

			// ação e verificação
			boolean resultado = service.curtir(curteResp);

			Mockito.verify(curteRespRepository).save(curteResp);
			Mockito.verify(curteRespRepository, Mockito.never()).deleteById(1l);
			Assertions.assertEquals(resultado, true);
		});

	}

	@Test
	public void deveDesCurtirUmaResposta() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var curteResp = criarCurteResp();
			curteResp.setId(1l);
			Mockito.when(curteRespRepository.findByUsuarioAndResposta(Mockito.any(Usuario.class),
					Mockito.any(Resposta.class))).thenReturn(Optional.of(curteResp));

			// ação e verificação
			boolean resultado = service.curtir(curteResp);

			Mockito.verify(curteRespRepository, Mockito.never()).save(curteResp);
			Mockito.verify(curteRespRepository).deleteById(1l);
			Assertions.assertEquals(resultado, false);
		});

	}

	@Test
	public void devePreencherTotalDeCurtidas() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			Long total = 1l;
			var resposta = criarResposta();
			resposta.setId(1l);

			var listaResposta = new ArrayList<Resposta>();
			listaResposta.add(resposta);

			Mockito.when(curteRespRepository.countByResposta(resposta)).thenReturn(total);

			// ação e verificação
			service.totalCurtidas(listaResposta);

			Mockito.verify(curteRespRepository).countByResposta(resposta);
		});

	}

	@Test
	public void deveVerificarSeRespostaPertenceAoUsuarioComSucesso() {

		// cenário
		var resposta = criarResposta();
		resposta.setId(1l);
		resposta.getUsuario().setEmail("teste@gmail.com");
		var usuarioEmail = "teste@gmail.com";

		// ação e verificação
		Assertions.assertDoesNotThrow(() -> {
			service.verificarUsuario(resposta, usuarioEmail);
		});

	}

	@Test
	public void deveRetonarErroCasoRespostaNaoPertenceAoUsuario() {

		// cenário
		var resposta = criarResposta();
		resposta.setId(1l);
		resposta.getUsuario().setEmail("erro@gmail.com");
		var usuarioEmail = "teste@gmail.com";

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.verificarUsuario(resposta, usuarioEmail);
		});

	}

	public static Resposta criarResposta() {
		return Resposta.builder().texto("texto").usuario(UsuarioServiceTest.criarUsuario()).aceita(false)
				.deletado(false).pergunta(PerguntaServiceTest.criarPergunta()).dataEmissao(LocalDateTime.now()).build();
	}

	public static CurteResp criarCurteResp() {
		var resposta = criarResposta();
		resposta.setId(1l);
		var usuario = UsuarioServiceTest.criarUsuario();
		usuario.setId(1l);
		return CurteResp.builder().resposta(resposta).usuario(usuario).build();
	}

}
