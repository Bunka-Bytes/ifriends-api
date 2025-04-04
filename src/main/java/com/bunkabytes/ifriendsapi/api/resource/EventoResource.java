package com.bunkabytes.ifriendsapi.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bunkabytes.ifriendsapi.api.dto.EventoDto;
import com.bunkabytes.ifriendsapi.api.dto.ReportDto;
import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.Evento;
import com.bunkabytes.ifriendsapi.model.entity.FavoritaEvento;
import com.bunkabytes.ifriendsapi.model.entity.ReportaEvento;
import com.bunkabytes.ifriendsapi.model.enums.Pontuacao;
import com.bunkabytes.ifriendsapi.service.EventoService;
import com.bunkabytes.ifriendsapi.service.JwtService;
import com.bunkabytes.ifriendsapi.service.RecursosService;
import com.bunkabytes.ifriendsapi.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.var;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/eventos")
@Slf4j
@CrossOrigin(origins = "*")
public class EventoResource {

	private final EventoService service;
	private final JwtService jwtService;
	private final RecursosService recursoService;
	private final UsuarioService usuarioService;

	@PostMapping
	@Operation(summary = "Salvando uma evento no banco de dados")
	public ResponseEntity<?> salvar(@RequestBody EventoDto dto,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		log.info("Salvando evento na base de dados");
		try {
			var evento = converter(dto);
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			evento.setUsuario(usuario.get());
			evento = service.salvar(evento, dto.getImagens());

			usuarioService.salvarPontuacao(Pontuacao.EVENTO, usuario.get());

			return new ResponseEntity<Evento>(evento, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("{id}/reportar")
	@Operation(summary = "Reportando um evento")
	public ResponseEntity<?> reportar(@PathVariable("id") Long id, @RequestBody ReportDto dto,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		log.info("Salvando report do evento na base de dados");
		try {
			var reportaEvento = new ReportaEvento();
			reportaEvento.setEvento(service.obterPorId(id).get());
			reportaEvento.setUsuario(usuarioService.obterPorEmail(usuarioRequisicao).get());
			reportaEvento.setMotivo(recursoService.obterMotivosReportPorId(dto.getIdMotivo()).get());
			reportaEvento.setDescricao(dto.getDescricao());
			service.reportar(reportaEvento);

			return ResponseEntity.ok("Reporte enviado com sucesso, obrigado!");
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("{id}/favoritar")
	@Operation(summary = "Favoritando um evento pelo ID")
	public ResponseEntity<?> favoritarEvento(@PathVariable("id") Long id,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();

		log.info("Mantendo favoritação do evento");

		try {
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			var evento = service.obterPorId(id);

			var favoritaEvento = new FavoritaEvento();
			favoritaEvento.setUsuario(usuario.get());
			favoritaEvento.setEvento(evento.get());
			boolean favorita = service.favoritar(favoritaEvento);

			if (favorita)
				usuarioService.salvarPontuacao(Pontuacao.CURTIR, usuario.get());
			else
				usuarioService.salvarPontuacao(Pontuacao.DESCURTIR, usuario.get());

			return ResponseEntity.ok(favorita);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("{id}")
	@Operation(summary = "Atualizando uma evento pelo ID")
	public ResponseEntity<?> atualizar(@PathVariable("id") Long id, @RequestBody EventoDto dto,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();

		log.info("Atualizando evento");

		try {
			var evento = service.obterPorId(id);
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			service.verificarUsuario(evento.get(), usuario.get());
			var modificacao = converter(dto);
			modificacao.setId(id);
			return ResponseEntity.ok(service.atualizar(modificacao));

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@GetMapping()
	@Operation(summary = "Buscando eventos por filtros")
	public ResponseEntity<?> buscar(@RequestParam(value = "pesquisa", required = false) String pesquisa,
			@RequestParam(value = "categoria", required = false) Long categoriaId,
			@RequestParam(value = "tag", required = false) String tag) {
		log.info("Buscando eventos");
		try {
			var eventoFiltro = new Evento();
			if (categoriaId != null) {
				var categoria = recursoService.obterPorId(categoriaId);
				eventoFiltro.setCategoria(categoria.get());
			}
			eventoFiltro.setNome(pesquisa);
			var eventos = service.buscar(eventoFiltro, tag);
			return ResponseEntity.ok(eventos);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@GetMapping("{id}")
	@Operation(summary = "Exibindo uma evento pelo ID")
	public ResponseEntity<?> exibir(@PathVariable("id") Long id) {
		log.info("exibindo evento");

		try {
			var evento = service.obterPorId(id);
			return ResponseEntity.ok(evento);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}
	
	@GetMapping("/usuarios/{id}")
	@Operation(summary = "Exibindo eventos do usuário pelo ID")
	public ResponseEntity<?> exibirEventosUsuario(@PathVariable("id") Long id) {
		log.info("exibindo eventos do usuário");

		try {
			var usuario = usuarioService.obterPorId(id);
			var eventos = service.obterPorUsuario(usuario.get());
			return ResponseEntity.ok(eventos);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	private Evento converter(EventoDto dto) {
		var evento = new Evento();
		evento.setNome(dto.getNome());
		evento.setLocal(dto.getLocal());
		evento.setDataEvento(dto.getDataEvento());
		evento.setDescricao(dto.getDescricao());
		evento.setPresencial(dto.getPresencial());
		evento.setLink(dto.getLink());
		evento.setTags(dto.getTags());
		evento.setCategoria(recursoService.obterPorId(dto.getCategoria()).get());

		return evento;
	}
}
