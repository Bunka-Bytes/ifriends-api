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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bunkabytes.ifriendsapi.api.dto.PerguntaDto;
import com.bunkabytes.ifriendsapi.api.dto.ReportDto;
import com.bunkabytes.ifriendsapi.model.entity.CurtePerg;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.model.entity.ReportaPergunta;
import com.bunkabytes.ifriendsapi.model.entity.Visualizacao;
import com.bunkabytes.ifriendsapi.model.enums.Pontuacao;
import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.service.JwtService;
import com.bunkabytes.ifriendsapi.service.PerguntaService;
import com.bunkabytes.ifriendsapi.service.RecursosService;
import com.bunkabytes.ifriendsapi.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.var;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/perguntas")
@Slf4j
@CrossOrigin(origins = "*")
public class PerguntaResource {

	private final PerguntaService service;
	private final UsuarioService usuarioService;
	private final RecursosService recursoService;
	private final JwtService jwtService;

	@GetMapping()
	@Operation(summary = "Buscando perguntas por filtros")
	public ResponseEntity<?> buscar(@RequestParam(value = "pesquisa", required = false) String pesquisa,
			@RequestParam(value = "categoria", required = false) Long categoriaId,
			@RequestParam(value = "respondida", required = false) Boolean respondida,
			@RequestParam(value = "semResposta", required = false) Boolean semResposta,
			@RequestParam(value = "tag", required = false) String tag,
			@RequestParam(value = "Order", defaultValue = "false", required = false) boolean crescente,
			@RequestParam(value = "by", required = false) String by) {
		log.info("Buscando perguntas");
		try {
			var perguntaFiltro = new Pergunta();
			if (categoriaId != null) {
				var categoria = recursoService.obterPorId(categoriaId);
				perguntaFiltro.setCategoria(categoria.get());
			}
			perguntaFiltro.setTitulo(pesquisa);
			perguntaFiltro.setRespondida(respondida);
			var perguntas = service.buscar(perguntaFiltro, semResposta, tag, by, crescente);
			return ResponseEntity.ok(perguntas);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@GetMapping("{id}")
	@Operation(summary = "Exibindo uma pergunta pelo ID")
	public ResponseEntity<?> exibir(@PathVariable("id") Long id) {
		log.info("exibindo pergunta");

		try {
			var pergunta = service.obterPorId(id);
			return ResponseEntity.ok(pergunta);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@GetMapping("/usuarios/{id}")
	@Operation(summary = "Exibindo perguntas do usuário pelo ID")
	public ResponseEntity<?> exibirPerguntasUsuario(@PathVariable("id") Long id) {
		log.info("exibindo perguntas do usuário");

		try {
			var usuario = usuarioService.obterPorId(id);
			var perguntas = service.obterPorUsuario(usuario.get());
			return ResponseEntity.ok(perguntas);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
	
	@GetMapping("/reportadas")
	@Operation(summary = "Exibindo todas perguntas reportadas")
	public ResponseEntity<?> exibirPerguntasReportadas() {
		log.info("exibindo todas perguntas reportadas");

		try {
			var perguntas = service.obterPerguntasReportadas();
			return ResponseEntity.ok(perguntas);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PostMapping("{id}/curtir")
	@Operation(summary = "Curtindo uma pergunta pelo ID")
	public ResponseEntity<?> curtirPergunta(@PathVariable("id") Long id,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();

		log.info("Mantendo curtida da pergunta");

		try {
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			var pergunta = service.obterPorId(id);

			var curtePerg = new CurtePerg();
			curtePerg.setUsuario(usuario.get());
			curtePerg.setPergunta(pergunta.get());
			var curtida = service.curtir(curtePerg);

			if (curtida)
				usuarioService.salvarPontuacao(Pontuacao.CURTIR, usuario.get());
			else
				usuarioService.salvarPontuacao(Pontuacao.DESCURTIR, usuario.get());

			return ResponseEntity.ok(curtida);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping
	@Operation(summary = "Salvando uma pergunta no banco de dados")
	public ResponseEntity<?> salvar(@RequestBody PerguntaDto dto,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		log.info("Salvando pergunta na base de dados");
		try {
			var pergunta = converter(dto);
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			pergunta.setUsuario(usuario.get());
			pergunta = service.salvar(pergunta, dto.getImagens());

			usuarioService.salvarPontuacao(Pontuacao.PERGUNTA, usuario.get());

			return new ResponseEntity<Pergunta>(pergunta, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("{id}/reportar")
	@Operation(summary = "Reportando uma pergunta")
	public ResponseEntity<?> reportar(@PathVariable("id") Long id, @RequestBody ReportDto dto,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		log.info("Salvando report da pergunta na base de dados");
		try {
			var reportaPergunta = new ReportaPergunta();
			reportaPergunta.setPergunta(service.obterPorId(id).get());
			reportaPergunta.setUsuario(usuarioService.obterPorEmail(usuarioRequisicao).get());
			reportaPergunta.setMotivo(recursoService.obterMotivosReportPorId(dto.getIdMotivo()).get());
			reportaPergunta.setDescricao(dto.getDescricao());
			service.reportar(reportaPergunta);

			return ResponseEntity.ok("Reporte enviado com sucesso, obrigado!");
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("{id}")
	@Operation(summary = "Atualizando uma pergunta pelo ID")
	public ResponseEntity<?> atualizar(@PathVariable("id") Long id, @RequestBody PerguntaDto dto,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();

		log.info("Atualizando pergunta");

		try {
			var pergunta = service.obterPorId(id);
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			service.verificarUsuario(pergunta.get(), usuario.get());
			Pergunta modificacao = converter(dto);
			modificacao.setId(id);
			return ResponseEntity.ok(service.atualizar(modificacao));

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PatchMapping("{id}/respondida")
	@Operation(summary = "Fechando pergunta que já foi respondida")
	public ResponseEntity<?> fecharPergunta(@PathVariable("id") Long id,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		log.info("Fechando pergunta");

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		try {
			var pergunta = service.obterPorId(id).get();
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			service.verificarUsuario(pergunta, usuario.get());
			return ResponseEntity.ok(service.atualizarStatus(pergunta));
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
	
	@PostMapping("{id}/visualizar")
	@Operation(summary = "Somando visualizacao da pergunta")
	public ResponseEntity<?> contarVisualizacao(@PathVariable("id") Long id, @RequestHeader(value = "Authorization", required = false) String authorization) {

		log.info("Somando visualizacao da pergunta");
		
		try {
			var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			var pergunta = service.obterPorId(id);

			var visualizacao = new Visualizacao();
			visualizacao.setUsuario(usuario.get());
			visualizacao.setPergunta(pergunta.get());
			return ResponseEntity.ok(service.somarVisualizacao(visualizacao));
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@DeleteMapping("{id}")
	@Operation(summary = "Deleção lógica da pergunta pelo ID")
	public ResponseEntity<?> deletar(@PathVariable("id") Long id,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		String usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();

		log.info("Deletando pergunta");

		try {
			var pergunta = service.obterPorId(id);
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			service.verificarUsuario(pergunta.get(), usuario.get());
			service.deletar(pergunta.get());
			return new ResponseEntity<String>("Pergunta deletada", HttpStatus.NO_CONTENT);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	private Pergunta converter(PerguntaDto dto) {
		var pergunta = new Pergunta();
		pergunta.setTitulo(dto.getTitulo());
		pergunta.setTexto(dto.getTexto());
		pergunta.setTags(dto.getTags());
		var categoria = recursoService.obterPorId(dto.getCategoria());
		pergunta.setCategoria(categoria.get());

		return pergunta;
	}

}
