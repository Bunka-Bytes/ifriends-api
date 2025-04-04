package com.bunkabytes.ifriendsapi.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import com.bunkabytes.ifriendsapi.api.dto.LoginDto;
import com.bunkabytes.ifriendsapi.api.dto.TokenDto;
import com.bunkabytes.ifriendsapi.api.dto.UsuarioDto;
import com.bunkabytes.ifriendsapi.exception.ErroAutenticacao;
import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.service.JwtService;
import com.bunkabytes.ifriendsapi.service.RecursosService;
import com.bunkabytes.ifriendsapi.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.var;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
@Slf4j
@CrossOrigin(origins = "*")
public class UsuarioResource {
	private final UsuarioService service;
	private final JwtService jwtService;
	private final RecursosService recursoService;

	@PostMapping("/autenticar")
	@Operation(summary = "Autenticando o usuário pelo Login")
	public ResponseEntity<?> autenticar(@RequestBody LoginDto dto) {
		log.info("Autenticando usuario");
		try {
			var usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
			var token = jwtService.gerarToken(usuarioAutenticado);
			TokenDto tokenDto = new TokenDto(usuarioAutenticado.getNome(), usuarioAutenticado.getImagem(), token);
			return ResponseEntity.ok(tokenDto);
		} catch (ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Operation(summary = "Cadastrando usuário no banco de dados")
	public ResponseEntity<?> salvar(@RequestBody UsuarioDto dto) {
		log.info("Salvando usuario na base de dados");

		try {
			var usuario = converter(dto);
			var usuarioSalvo = service.salvarUsuario(usuario);
			usuarioSalvo.setCodVerificador(service.criptografarCodigo(usuarioSalvo.getCodVerificador()));
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping()
	@Operation(summary = "Buscando usuarios por filtros")
	public ResponseEntity<?> buscar(@RequestParam(value = "pesquisa", required = false) String pesquisa) {
		log.info("Buscando usuarios");
		try {
			var usuarioFiltro = new Usuario();
			usuarioFiltro.setNome(pesquisa);
			usuarioFiltro.setApelido(pesquisa);
			var usuarios = service.buscar(usuarioFiltro);
			return ResponseEntity.ok(usuarios);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PutMapping("{id}")
	@Operation(summary = "Atualizando uma usuario pelo ID")
	public ResponseEntity<?> atualizar(@PathVariable("id") Long id, @RequestBody UsuarioDto dto,
			@RequestHeader(value = "Authorization", required = false) String authorization) {

		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();

		log.info("Atualizando usuario");

		try {
			var usuarioAAtualizar = service.obterPorId(id);
			var usuarioAtualizando = service.obterPorEmail(usuarioRequisicao);
			service.verificarUsuario(usuarioAAtualizar.get(), usuarioAtualizando.get());
			var modificacoes = converter(dto);
			modificacoes.setId(id);
			return ResponseEntity.ok(service.atualizar(modificacoes));

		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PatchMapping("{id}/banir")
	@Operation(summary = "Banir um usuário por Id (administrador)")
	public ResponseEntity<?> banirUsuario(@PathVariable("id") Long id) {

		try {
			var usuario = service.obterPorId(id);
			service.banirUsuario(usuario.get());
			return ResponseEntity.ok("Banido");
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/perguntas/curtidas")
	@Operation(summary = "Buscando perguntas curtidas do usuário")
	public ResponseEntity<?> buscarPerguntasCurtidas(
			@RequestHeader(value = "Authorization", required = false) String authorization) {
		log.info("Buscando perguntas curtidas do usuario na base de dados");
		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		try {
			var usuario = service.obterPorEmail(usuarioRequisicao);
			var perguntasCurtidas = service.obterPerguntasCurtidas(usuario.get());
			return ResponseEntity.ok(perguntasCurtidas);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/email/{codigo}/confirmacao")
	@Operation(summary = "Confirmando email do usuário")
	public ResponseEntity<?> confirmarEmail(@PathVariable("codigo") String codVerificador) {
		try {
			service.verificarEmail(codVerificador);
			return ResponseEntity.ok("E-mail verificado com sucesso!");
		} catch (ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/respostas/curtidas")
	@Operation(summary = "Buscando respostas curtidas do usuário")
	public ResponseEntity<?> buscarRespostasCurtidas(
			@RequestHeader(value = "Authorization", required = false) String authorization) {
		log.info("Buscando respostas curtidas do usuario na base de dados");
		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		try {
			var usuario = service.obterPorEmail(usuarioRequisicao);
			var respostasCurtidas = service.obterRespostasCurtidas(usuario.get());
			return ResponseEntity.ok(respostasCurtidas);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/eventos/favoritados")
	@Operation(summary = "Buscando eventos favoritados pelo usuário")
	public ResponseEntity<?> buscarEventosFavoritados(
			@RequestHeader(value = "Authorization", required = false) String authorization) {
		log.info("Buscando eventos favoritados do usuario na base de dados");
		var usuarioRequisicao = jwtService.obterClaims(authorization).getSubject();
		try {
			var usuario = service.obterPorEmail(usuarioRequisicao);
			var eventosFavoritados = service.obterEventosFavoritados(usuario.get());
			return ResponseEntity.ok(eventosFavoritados);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("{id}")
	@Operation(summary = "Buscando informações do usuário pelo id")
	public ResponseEntity<?> buscarUsuario(@PathVariable("id") Long id) {
		log.info("Buscando informações do usuario na base de dados");
		try {
			var usuario = service.obterPorId(id);
			return ResponseEntity.ok(usuario);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	public Usuario converter(UsuarioDto dto) {
		Usuario usuario = new Usuario();
		usuario.setNome(dto.getNome());
		usuario.setApelido(dto.getApelido());
		usuario.setBio(dto.getBio());
		usuario.setEmail(dto.getEmail());
		usuario.setSenha(dto.getSenha());
		usuario.setCurso(recursoService.obterPorSigla(dto.getCurso()).get());
		usuario.setAno(dto.getAno());
		usuario.setImagem(dto.getImagem());

		return usuario;
	}
}
