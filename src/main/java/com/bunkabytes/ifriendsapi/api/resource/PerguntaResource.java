package com.bunkabytes.ifriendsapi.api.resource;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bunkabytes.ifriendsapi.api.dto.PerguntaDto;
import com.bunkabytes.ifriendsapi.model.entity.Categoria;
import com.bunkabytes.ifriendsapi.model.entity.CurtePerg;
import com.bunkabytes.ifriendsapi.model.entity.Pergunta;
import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.service.CategoriaService;
import com.bunkabytes.ifriendsapi.service.JwtService;
import com.bunkabytes.ifriendsapi.service.PerguntaService;
import com.bunkabytes.ifriendsapi.service.UsuarioService;

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
	private final CategoriaService categoriaService;
	private final JwtService jwtService;

	@GetMapping()
	public ResponseEntity buscar(@RequestParam(value = "titulo", required = false) String titulo,
			@RequestParam(value = "texto", required = false) String texto,
			@RequestParam(value = "respondida", required = false) boolean respondida,
			@RequestParam(value = "usuario", required = false) String nomeUsuario,
			@RequestParam(value = "categoria", required = false) Long idCategoria) {
		log.info("Buscando perguntas");
		Pergunta perguntaFiltro = new Pergunta();
		perguntaFiltro.setTitulo(titulo);
		perguntaFiltro.setTexto(texto);
		perguntaFiltro.setRespondida(respondida);

		if (nomeUsuario != null) {
			var usuario = usuarioService.obterPorNome(nomeUsuario);
			if (!usuario.isPresent()) {
				return ResponseEntity.badRequest().body("Não foi possivel encontrar o usuário");
			} else {
				perguntaFiltro.setUsuario(usuario.get());
			}
		}
		if (idCategoria != null) {
			var categoria = categoriaService.obterCategoriaPorId(idCategoria);
			if (!categoria.isPresent()) {
				return ResponseEntity.badRequest().body("Não foi possivel encontrar a categoria");
			} else {
				perguntaFiltro.setCategoria(categoria.get());
			}
		}

		var perguntas = service.buscar(perguntaFiltro);

		return ResponseEntity.ok(perguntas);
	}

	@GetMapping("{id}")
	public ResponseEntity exibir(@PathVariable("id") Long id) {
		log.info("exibindo pergunta");

		try {
			var pergunta = service.obterPorId(id);
			return ResponseEntity.ok(pergunta);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PostMapping("/curtir/{idPergunta}")
	public ResponseEntity curtirPergunta(@PathVariable("idPergunta") Long idPergunta,
			@RequestHeader("Authorization") String authorization) {

		String usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();

		log.info("Mantendo curtida da pergunta");

		try {
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			var pergunta = service.obterPorId(idPergunta);
			CurtePerg curtePerg = new CurtePerg();
			curtePerg.setUsuario(usuario.get());
			curtePerg.setPergunta(pergunta.get());
			boolean curtida = service.curtir(curtePerg);
			return ResponseEntity.ok(curtida);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity salvar(@RequestBody PerguntaDto dto, @RequestHeader("Authorization") String authorization) {

		String usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		log.info("Salvando pergunta na base de dados");
		try {
			Pergunta pergunta = converter(dto);
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			pergunta.setUsuario(usuario.get());
			pergunta = service.salvar(pergunta);
			return new ResponseEntity(pergunta, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody PerguntaDto dto,
			@RequestHeader("Authorization") String authorization) {

		String usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();

		log.info("Atualizando pergunta");

		try {
			var pergunta = service.obterPorId(id);
			var usuario = usuarioService.obterPorEmail(usuarioRequisicao);
			service.verificarUsuario(pergunta.get(), usuarioRequisicao);
			Pergunta modificacao = converter(dto);
			modificacao.setUsuario(usuario.get());
			modificacao.setId(id);
			return ResponseEntity.ok(service.atualizar(modificacao));

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id, @RequestHeader("Authorization") String authorization) {

		String usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();

		log.info("Deletando pergunta");

		try {
			var pergunta = service.obterPorId(id);
			service.verificarUsuario(pergunta.get(), usuarioRequisicao);
			service.deletar(pergunta.get());
			return new ResponseEntity(HttpStatus.NO_CONTENT);

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	private Pergunta converter(PerguntaDto dto) {
		Pergunta pergunta = new Pergunta();
		pergunta.setTitulo(dto.getTitulo());
		pergunta.setTexto(dto.getTexto());
		pergunta.setTag(dto.getTag());

		Categoria categoria = categoriaService.obterCategoriaPorId(dto.getCategoria())
				.orElseThrow(() -> new RegraNegocioException("Categoria não encontrada para o Id informado."));

		pergunta.setCategoria(categoria);

		return pergunta;
	}

}
