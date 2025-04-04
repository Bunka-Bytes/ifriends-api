package com.bunkabytes.ifriendsapi.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bunkabytes.ifriendsapi.api.dto.ReportDto;
import com.bunkabytes.ifriendsapi.api.dto.RespostaDto;
import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.CurteResp;
import com.bunkabytes.ifriendsapi.model.entity.ReportaResposta;
import com.bunkabytes.ifriendsapi.model.entity.Resposta;
import com.bunkabytes.ifriendsapi.model.enums.Pontuacao;
import com.bunkabytes.ifriendsapi.service.JwtService;
import com.bunkabytes.ifriendsapi.service.PerguntaService;
import com.bunkabytes.ifriendsapi.service.RecursosService;
import com.bunkabytes.ifriendsapi.service.RespostaService;
import com.bunkabytes.ifriendsapi.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
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
	private final RecursosService recursoService;

	@GetMapping("/perguntas/{id}/respostas")
	@Operation(summary = "Buscando resposta pelo ID da pergunta")
	public ResponseEntity<?> buscar(@PathVariable("id") Long idPergunta) {
		log.info("Buscando perguntas");
		var respostas = new Resposta();

		try {
			var pergunta = perguntaService.obterPorId(idPergunta);
			respostas.setPergunta(pergunta.get());
			var respostasEncontradas = service.buscar(respostas);
			return ResponseEntity.ok(respostasEncontradas);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/respostas")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Operation(summary = "Salvando resposta no banco de dados")
	public ResponseEntity salvar(@RequestBody RespostaDto dto,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		log.info("Salvando resposta no banco de dados");

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		try {
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			var respostaASalvar = converter(dto);
			respostaASalvar.setUsuario(usuario.get());
			respostaASalvar = service.salvar(respostaASalvar, dto.getImagens());
			usuarioService.salvarPontuacao(Pontuacao.RESPOSTA, usuario.get());
			return new ResponseEntity(respostaASalvar, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("respostas/{id}/reportar")
	@Operation(summary = "Reportando uma resposta")
	public ResponseEntity<?> reportar(@PathVariable("id") Long id, @RequestBody ReportDto dto,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		log.info("Salvando report da resposta na base de dados");
		try {
			var reportaResposta = new ReportaResposta();
			reportaResposta.setResposta(service.obterPorId(id).get());
			reportaResposta.setUsuario(usuarioService.obterPorEmail(usuarioRequisicao).get());
			reportaResposta.setMotivo(recursoService.obterMotivosReportPorId(dto.getIdMotivo()).get());
			reportaResposta.setDescricao(dto.getDescricao());
			service.reportar(reportaResposta);

			return ResponseEntity.ok("Reporte enviado com sucesso, obrigado!");
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/respostas/{id}")
	@Operation(summary = "Exibindo resposta pelo ID")
	public ResponseEntity<?> exibir(@PathVariable("id") Long id) {
		log.info("exibindo resposta");

		try {
			var resposta = service.obterPorId(id);
			return ResponseEntity.ok(resposta);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@GetMapping("/respostas/usuarios/{id}")
	@Operation(summary = "Exibindo respostas do usuário pelo ID")
	public ResponseEntity<?> exibirRespostasUsuario(@PathVariable("id") Long id) {
		log.info("exibindo respostas do usuário");

		try {
			var usuario = usuarioService.obterPorId(id);
			var respostas = service.obterPorUsuario(usuario.get());
			return ResponseEntity.ok(respostas);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PostMapping("/respostas/{id}/curtir")
	@Operation(summary = "Curtindo uma resposta pelo ID")
	public ResponseEntity<?> curtirResposta(@PathVariable("id") Long id,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		log.info("Mantendo curtida na resposta");

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();

		try {
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			var resposta = service.obterPorId(id);

			var curteResp = new CurteResp();
			curteResp.setUsuario(usuario.get());
			curteResp.setResposta(resposta.get());
			boolean curtida = service.curtir(curteResp);

			if (curtida)
				usuarioService.salvarPontuacao(Pontuacao.CURTIR, usuario.get());
			else
				usuarioService.salvarPontuacao(Pontuacao.DESCURTIR, usuario.get());

			return ResponseEntity.ok(curtida);

		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PutMapping("/respostas/{id}")
	@Operation(summary = "Atualizando resposta pelo ID")
	public ResponseEntity<?> atualizar(@PathVariable("id") Long id, @RequestBody RespostaDto dto,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		log.info("Atualizando resposta");

		var assinaturaToken = jwtService.obterClaims(authorization).getSubject();
		try {
			var usuarioRequisitando = usuarioService.obterPorEmail(assinaturaToken);
			var resposta = service.obterPorId(id);
			service.verificarUsuario(resposta.get(), usuarioRequisitando.get());
			var modificacoes = converter(dto);
			modificacoes.setId(id);
			return ResponseEntity.ok(service.atualizar(modificacoes));

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PatchMapping("/respostas/{id}/aceitar")
	@Operation(summary = "Aceitando resposta")
	public ResponseEntity<?> aceitarResposta(@PathVariable("id") Long id,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		log.info("Aceitando resposta");

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		try {
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			var resposta = service.obterPorId(id).get();
			if (resposta.getUsuario().equals(usuario.get()))
				return ResponseEntity.badRequest().body("Não é possivel aceitar a própria resposta");
			perguntaService.verificarUsuario(resposta.getPergunta(), usuario.get());
			var aceita = service.aceitarResposta(resposta);

			if (aceita)
				usuarioService.salvarPontuacao(Pontuacao.ACEITAR, resposta.getUsuario());
			else
				usuarioService.salvarPontuacao(Pontuacao.DESACEITAR, resposta.getUsuario());
				

			return ResponseEntity.ok(aceita);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@DeleteMapping("/respostas/{id}")
	@SuppressWarnings("rawtypes")
	@Operation(summary = "Deleção lógica da resposta pelo ID")
	public ResponseEntity<?> deletar(@PathVariable("id") Long id,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		log.info("Deletando resposta do usuario");

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		try {
			var resposta = service.obterPorId(id);
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			service.verificarUsuario(resposta.get(), usuario.get());
			service.deletar(resposta.get());
			return new ResponseEntity(HttpStatus.NO_CONTENT);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	private Resposta converter(RespostaDto dto) {
		var resposta = new Resposta();
		resposta.setTexto(dto.getTexto());

		var pergunta = perguntaService.obterPorId(dto.getPergunta())
				.orElseThrow(() -> new RegraNegocioException("Pergunta não encontrada para o Id informado."));

		resposta.setPergunta(pergunta);

		return resposta;
	}
}
