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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bunkabytes.ifriendsapi.api.dto.RespostaDto;
import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.CurteResp;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.Resposta;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.service.PerguntaService;
import com.bunkabytes.ifriendsapi.service.RespostaService;
import com.bunkabytes.ifriendsapi.service.UsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/respostas")
@Slf4j
@CrossOrigin(origins = "*")
public class RespostaResource {

	private final PerguntaService perguntaService;
	private final UsuarioService usuarioService;
	private final RespostaService service;

	@GetMapping("{id}")
	public ResponseEntity buscar(@PathVariable("id") Long idPergunta) {
		log.info("Buscando perguntas");
		Resposta respostas = new Resposta();

		Optional<Pergunta> pergunta = perguntaService.obterPorId(idPergunta);
		if (!pergunta.isPresent()) {
			return ResponseEntity.badRequest().body("Não foi possivel encontrar a pergunta");
		} else {
			respostas.setPergunta(pergunta.get());
		}

		List<Resposta> respostasEncontradas = service.buscar(respostas);
		return ResponseEntity.ok(respostasEncontradas);
	}

	@PostMapping
	public ResponseEntity salvar(@RequestBody RespostaDto dto) {
		log.info("Salvando perguntas no banco de dados");
		try {
			Resposta entidade = converter(dto, null);
			entidade = service.salvar(entidade);
			return new ResponseEntity(entidade, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/curtir/{idUsuario}/{idResposta}")
	public ResponseEntity curtirResposta(@PathVariable("idResposta") Long idResposta,
			@PathVariable("idUsuario") Long idUsuario) {
		log.info("Mantendo curtida na resposta");
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		if (!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Não foi possivel encontrar o usuário");
		} else {
			Optional<Resposta> resposta = service.obterPorId(idResposta);
			if (!resposta.isPresent()) {
				return ResponseEntity.badRequest().body("Não foi possivel encontrar a resposta");
			} else {
				CurteResp curteResp = new CurteResp();
				curteResp.setUsuario(usuario.get());
				curteResp.setResposta(resposta.get());
				boolean curtida = service.curtir(curteResp);
				return ResponseEntity.ok(curtida);
			}
		}

	}

	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody RespostaDto dto) {
		log.info("Atualizando resposta");
		return service.obterPorId(id).map(entity -> {

			try {
				Resposta resposta = converter(dto, null);
				resposta.setId(id);
				service.atualizar(resposta);
				return ResponseEntity.ok(resposta);

			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}

		}).orElseGet(() -> new ResponseEntity("Pergunta não encontrada na base de dados.", HttpStatus.BAD_REQUEST));
	}

	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		log.info("Deletando resposta do usuario");
		return service.obterPorId(id).map(entity -> {

			try {
				service.deletar(entity);
				return new ResponseEntity(HttpStatus.NO_CONTENT);

			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}

		}).orElseGet(() -> new ResponseEntity("Pergunta não encontrada na base de dados.", HttpStatus.BAD_REQUEST));
	}

	private Resposta converter(RespostaDto dto, String authorization) {
		Resposta resposta = new Resposta();
		resposta.setId(dto.getId());
		resposta.setTexto(dto.getTexto());
		resposta.setAceita(dto.isAceita());

		Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o Id informado."));

		resposta.setUsuario(usuario);

		Pergunta pergunta = perguntaService.obterPorId(dto.getPergunta())
				.orElseThrow(() -> new RegraNegocioException("Pergunta não encontrada para o Id informado."));

		resposta.setPergunta(pergunta);

		return resposta;
	}
}
