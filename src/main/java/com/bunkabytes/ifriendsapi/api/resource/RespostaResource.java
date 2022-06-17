package com.bunkabytes.ifriendsapi.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bunkabytes.ifriendsapi.api.dto.RespostaDto;
import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.CurteResp;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.Resposta;
import com.bunkabytes.ifriendsapi.service.JwtService;
import com.bunkabytes.ifriendsapi.service.PerguntaService;
import com.bunkabytes.ifriendsapi.service.RespostaService;
import com.bunkabytes.ifriendsapi.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.var;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
@CrossOrigin(origins = "*")
public class RespostaResource {

	private final RespostaService service;
	private final PerguntaService perguntaService;
	private final UsuarioService usuarioService;
	private final JwtService jwtService;

	@GetMapping("/perguntas/{id}/respostas")
	public ResponseEntity<?> buscar(@PathVariable("id") Long idPergunta) {
		log.info("Buscando perguntas");
		Resposta respostas = new Resposta();

		try {
			Optional<Pergunta> pergunta = perguntaService.obterPorId(idPergunta);
			respostas.setPergunta(pergunta.get());
			List<Resposta> respostasEncontradas = service.buscar(respostas);
			return ResponseEntity.ok(respostasEncontradas);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/respostas")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ResponseEntity salvar(@RequestBody RespostaDto dto,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		log.info("Salvando perguntas no banco de dados");

		String usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		try {
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			var respostaASalvar = converter(dto);
			respostaASalvar.setUsuario(usuario.get());
			respostaASalvar = service.salvar(respostaASalvar);
			return new ResponseEntity(respostaASalvar, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/respostas/{id}")
	public ResponseEntity<?> exibir(@PathVariable("id") Long id) {
		log.info("exibindo resposta");

		try {
			var resposta = service.obterPorId(id);
			return ResponseEntity.ok(resposta);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PostMapping("/respostas/{id}/curtir")
	public ResponseEntity<?> curtirResposta(@PathVariable("id") Long id,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		log.info("Mantendo curtida na resposta");

		String usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();

		try {
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			var resposta = service.obterPorId(id);

			CurteResp curteResp = new CurteResp();
			curteResp.setUsuario(usuario.get());
			curteResp.setResposta(resposta.get());
			boolean curtida = service.curtir(curteResp);
			return ResponseEntity.ok(curtida);

		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PutMapping("/respostas/{id}")
	public ResponseEntity<?> atualizar(@PathVariable("id") Long id, @RequestBody RespostaDto dto,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		log.info("Atualizando resposta");

		String usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		try {
			usuarioService.obterPorEmail(usuarioRequisicao);
			var resposta = service.obterPorId(id);
			service.verificarUsuario(resposta.get(), usuarioRequisicao);
			Resposta modificacoes = converter(dto);
			modificacoes.setId(id);
			return ResponseEntity.ok(service.atualizar(modificacoes));

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@DeleteMapping("/respostas/{id}")
	@SuppressWarnings("rawtypes")
	public ResponseEntity<?> deletar(@PathVariable("id") Long id,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		log.info("Deletando resposta do usuario");

		String usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		try {
			var resposta = service.obterPorId(id);
			service.verificarUsuario(resposta.get(), usuarioRequisicao);
			service.deletar(resposta.get());
			return new ResponseEntity(HttpStatus.NO_CONTENT);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	private Resposta converter(RespostaDto dto) {
		Resposta resposta = new Resposta();
		resposta.setTexto(dto.getTexto());

		Pergunta pergunta = perguntaService.obterPorId(dto.getPergunta())
				.orElseThrow(() -> new RegraNegocioException("Pergunta não encontrada para o Id informado."));

		resposta.setPergunta(pergunta);

		return resposta;
	}
}
