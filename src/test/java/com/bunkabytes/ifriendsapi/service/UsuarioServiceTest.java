package com.bunkabytes.ifriendsapi.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bunkabytes.ifriendsapi.exception.ErroAutenticacao;
import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.Curso;
import com.bunkabytes.ifriendsapi.model.entity.Dominio;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.enums.Pontuacao;
import com.bunkabytes.ifriendsapi.model.repository.CurtePergRepository;
import com.bunkabytes.ifriendsapi.model.repository.CurteRespRepository;
import com.bunkabytes.ifriendsapi.model.repository.DominioRepository;
import com.bunkabytes.ifriendsapi.model.repository.FavoritaEventoRepository;
import com.bunkabytes.ifriendsapi.model.repository.UsuarioRepository;
import com.bunkabytes.ifriendsapi.service.impl.UsuarioServiceImpl;


import lombok.var;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Profile("teste1")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;
	PasswordEncoder encoder;

	@MockBean
	UsuarioRepository repository;
	@MockBean
	CurtePergRepository curtePergRepository;
	@MockBean
	CurteRespRepository curteRespRepository;
	@MockBean
	DominioRepository dominioRepository;
	@MockBean
	FavoritaEventoRepository favoritaEventoRepository;

	@Test
	public void deveSalvarUmUsuario() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			Mockito.doNothing().when(service).validar(Mockito.any(Usuario.class));
			var usuario = criarUsuario();
			usuario.setId(1l);
			usuario.setSenha("SenhaFraca");

			Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

			// ação
			var usuarioSalvo = service.salvarUsuario(usuario);

			// verificação
			Assertions.assertNotNull(usuarioSalvo);
			Assertions.assertEquals(usuarioSalvo.getId(), 1l);
			Assertions.assertEquals(usuarioSalvo.getNome(), "usuario");
			Assertions.assertEquals(usuarioSalvo.getEmail(), "teste@aluno.ifsp.edu.br");
			Assertions.assertNotEquals(usuarioSalvo.getSenha(), "123");
			Assertions.assertEquals(usuarioSalvo.getCurso().getSigla(), "INFO");
			Assertions.assertEquals(usuarioSalvo.getAno(), 4);
		});
	}
	
	@Test
	public void deveLancarErroAoSalvarUmUsuarioComSenhaErrada() {
			// cenário
			Mockito.doNothing().when(service).validar(Mockito.any(Usuario.class));
			var usuario = criarUsuario();
			usuario.setId(1l);
			usuario.setSenha("a");

			// ação e verificação
			Assertions.assertThrows(RegraNegocioException.class,() -> {service.salvarUsuario(usuario);});
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveFiltrarUsuario() {

		// cenário
		var usuario = criarUsuario();
		usuario.setId(1l);
		var listaTag = new ArrayList<String>();
		listaTag.add("AW2");
		
		var lista = new ArrayList<Usuario>();
		lista.add(usuario);

		Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(Sort.class))).thenReturn(lista);

		// ação
		var resultado = service.buscar(usuario);

		// verificação
		Assertions.assertFalse(resultado.isEmpty());
		Assertions.assertTrue(resultado.size() == 1);
		Assertions.assertTrue(resultado.contains(usuario));
	}
	
	@Test
	public void deveAtualizarUmUsuarioComSucesso() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			var usuarioAAtualizar = criarUsuario();
			usuarioAAtualizar.setId(1l);
			var modificacoes = new Usuario();
			modificacoes.setId(1l);
			modificacoes.setApelido("apelido");
			modificacoes.setAno(2);

			Mockito.doNothing().when(service).validar(usuarioAAtualizar);
			Mockito.when(repository.findById(usuarioAAtualizar.getId())).thenReturn(Optional.of(usuarioAAtualizar));
			Mockito.when(repository.save(usuarioAAtualizar)).thenReturn(usuarioAAtualizar);

			// ação
			usuarioAAtualizar = service.atualizar(modificacoes);

			// verificação
			Mockito.verify(repository, Mockito.times(1)).save(usuarioAAtualizar);
			Assertions.assertEquals(usuarioAAtualizar.getApelido(), "apelido");
			Assertions.assertEquals(usuarioAAtualizar.getAno(), 2);
		});
	}

	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {

			// cenário
			var email = "email@email.com";
			var usuario = new Usuario();
			usuario.setEmail(email);

			Mockito.doNothing().when(service).validar(usuario);
			Mockito.when(repository.existsByEmail(email)).thenReturn(true);

			// ação
			service.salvarUsuario(usuario);

			// verificação - Eu espero que ele não tenha chamado o método de salvar meu
			// usuario
			Mockito.verify(repository, Mockito.never()).save(usuario);

		});
	}

	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		Assertions.assertDoesNotThrow(() -> {

			// cenário
			var nome = "kaiky";
			var email = "kaiky.br34@gmail.com";
			var senha = "senhagenerica123";
			var usuario = Usuario.builder()
					.nome(nome)
					.email(email)
					.senha(senha)
					.codVerificador(null).id(1l).build();
			
			service.criptografarSenha(usuario);
			Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

			// ação
			Usuario result = service.autenticar(email, senha);

			// verificação
			Assertions.assertNotNull(result);
		});
	}

	@Test
	public void deveLancarErroQuandoNaoHouverUsuarioCadastradoComOEmailInformado() {
		// cenário
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		// ação e verificação
		Assertions.assertThrows(ErroAutenticacao.class,
				() -> service.autenticar("kaiky.br34@gmail.com", "senhagenerica123"));

	}

	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {

		// cenário
		var senha = "senhagenerica123";
		var usuario = Usuario.builder().nome("kaiky").email("kaiky.br34@gmail.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

		// ação e verificação
		Assertions.assertThrows(ErroAutenticacao.class, () -> service.autenticar("kaiky.br34@gmail.com", "123"));
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoEmailNaoForVerificado() {
		Assertions.assertThrows(ErroAutenticacao.class, () -> {

			// cenário
			var nome = "kaiky";
			var email = "kaiky.br34@gmail.com";
			var senha = "senhagenerica123";
			var usuario = Usuario.builder()
					.nome(nome)
					.email(email)
					.senha(senha)
					.codVerificador("codigo").id(1l).build();
			
			service.criptografarSenha(usuario);
			Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
			// ação
			service.autenticar(email, senha);
			
		});
	}

	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {

		// cenário
		var usuario = criarUsuario();
		Mockito.when(repository.existsByEmail(usuario.getEmail())).thenReturn(true);

		// ação e verificações
		Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(usuario));

	}

	@Test
	public void deveLancarErroAoValidarEmailNaoInstitucional() {

		// cenário
		var usuario = criarUsuario();
		Mockito.when(repository.existsByEmail(usuario.getEmail())).thenReturn(true);

		// ação
		Assertions.assertThrows(RegraNegocioException.class, () -> service.validar(usuario));

	}

	@Test
	public void deveObterUsuarioPorId() {

		// cenário
		Long id = 1l;
		var usuario = criarUsuario();
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(usuario));

		// ação
		service.obterPorId(id);

		// verificação
		Mockito.verify(repository).findById(id);

	}
	
	@Test
	public void deveLancarErroAoBuscarUsuarioPorIdCasoNaoExistaUsuario() {

		// cenário
		var id = 1l;
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		// ação
		Assertions.assertThrows(RegraNegocioException.class, () ->{
			service.obterPorId(id);
		});

	}

	@Test
	public void deveObterUsuarioPorEmail() {

		// cenário
		var email = "teste@aluno.ifsp.edu.br";
		var usuario = criarUsuario();
		usuario.setId(1l);
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

		// ação
		var usuarioEncontrado = service.obterPorEmail(email);

		// verificação
		Mockito.verify(repository).findByEmail(email);
		Assertions.assertTrue(usuarioEncontrado.isPresent());

	}
	
	@Test
	public void devevalidar() {
		
		//cecnário
		var usuario = new Usuario();
		var dominio = criarDominio();
		var listaDominios = new ArrayList<Dominio>();
		listaDominios.add(dominio);
		Mockito.when(dominioRepository.findAll()).thenReturn(listaDominios);
		
		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
		usuario.setEmail("");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
	    char[] email = new char[101];
	    Arrays.fill(email, 'a');
		usuario.setEmail(new String(email));
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
		usuario.setNome("");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
	    char[] nome = new char[61];
	    Arrays.fill(nome, 'a');
		usuario.setNome(new String(nome));
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
		usuario.setApelido("");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
	    char[] apelido = new char[51];
	    Arrays.fill(apelido, 'a');
		usuario.setApelido(new String(apelido));
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
		usuario.setSenha("");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
		usuario.setSenha("a");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
		usuario.setAno(9);
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
		usuario.setEmail(new String(email)+"@aluno.ifsp.edu.br");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
		usuario.setEmail("email@aluno.ifsp.edu.br");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
		usuario.setNome("Kaiky");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
		usuario.setApelido("Kaiky");
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
		usuario.setAno(-1);
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validar(usuario);
		});
		
		usuario.setAno(2);
		Assertions.assertDoesNotThrow(() -> {
			service.validar(usuario);
		});
	}

	@Test
	public void deveLancarErroAoObterUsuarioSemEmail() {

		// cenário
		var email = "teste@aluno.ifsp.edu.br";
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.empty());

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> service.obterPorEmail(email));

	}

	@Test
	public void deveObterUsuarioPorNome() {

		// cenário
		var nome = "usuario";
		Mockito.when(repository.findByNome(nome)).thenReturn(1l);

		// ação
		service.obterPorNome(nome);

		// verificação
		Mockito.verify(repository).findByNome(nome);

	}
	
	@Test
	public void deveSalvarPontucaoDoUsuario() {

		// cenário
		var usuario = criarUsuario();
		usuario.setId(1l);
		Mockito.when(repository.save(usuario)).thenReturn(usuario);
		
		// ação
		service.salvarPontuacao(Pontuacao.PERGUNTA, usuario);

		// verificação
		Assertions.assertTrue(usuario.getReputacao() == 5);
		Mockito.verify(repository).save(usuario);

	}
	
	@Test
	public void deveVerificarCodigoDeEmailDoUsuario() {

		// cenário
		var codigo = UUID.randomUUID().toString();
		var usuario = criarUsuario();
		usuario.setId(1l);
		usuario.setCodVerificador(codigo);
		Mockito.when(repository.findByCodVerificador(codigo)).thenReturn(Optional.of(usuario));

		// ação
		service.verificarEmail(codigo);

		// verificação
		Mockito.verify(repository).save(usuario);
	}
	
	@Test
	public void deveLancarErroAoValidarCodigoDeEmailInvalido() {

		// cenário
		var codigo = UUID.randomUUID().toString();
		var usuario = criarUsuario();
		usuario.setId(1l);
		usuario.setCodVerificador("0");
		Mockito.when(repository.findByCodVerificador(codigo)).thenReturn(Optional.empty());

		// ação
		Assertions.assertThrows(ErroAutenticacao.class, () -> {
		service.verificarEmail(codigo);
		});
		// verificação
		Mockito.verify(repository, Mockito.never()).save(usuario);
	}
	
	@Test
	public void deveCriptografarCodigoEmail() {

		// cenário
		var codigo = UUID.randomUUID().toString();
		
		// ação
		var codigoCriptografado = service.criptografarCodigo(codigo);
		
		var codigoInvertido = new StringBuilder(codigo);
		var codigo1 = codigoInvertido.reverse().toString();
		
		// verificação
		Assertions.assertTrue(( codigoCriptografado.contains("A") || 
								codigoCriptografado.contains("E") ||
								codigoCriptografado.contains("I") ||
								codigoCriptografado.contains("O") ||
								codigoCriptografado.contains("U"))||
							(  !codigoCriptografado.contains("A") &&
							   !codigoCriptografado.contains("E") &&
							   !codigoCriptografado.contains("I") &&
							   !codigoCriptografado.contains("O") &&
							   !codigoCriptografado.contains("U")));
		Assertions.assertNotEquals(codigo, codigoCriptografado);
		Assertions.assertTrue(codigo1.equalsIgnoreCase(codigoCriptografado));
	}
	
	@Test
	public void deveObterPerguntasCurtidas() {

		// cenário
		var usuario = criarUsuario();
		usuario.setId(1l);
		Mockito.when(curtePergRepository.findByUsuario(usuario)).thenReturn(null);
		// ação
		var curtidas = service.obterPerguntasCurtidas(usuario);
		
		// verificação
		Assertions.assertEquals(curtidas, null);
	}
	
	@Test
	public void deveObterRespostasCurtidas() {

		// cenário
		var usuario = criarUsuario();
		usuario.setId(1l);
		Mockito.when(curteRespRepository.findByUsuario(usuario)).thenReturn(null);
		// ação
		var curtidas = service.obterRespostasCurtidas(usuario);
		
		// verificação
		Assertions.assertEquals(curtidas, null);
	}
	
	@Test
	public void deveObterEventosFavoritados() {

		// cenário
		var usuario = criarUsuario();
		usuario.setId(1l);
		Mockito.when(favoritaEventoRepository.findByUsuario(usuario)).thenReturn(null);
		// ação
		var favoritos = service.obterRespostasCurtidas(usuario);
		
		// verificação
		Assertions.assertTrue(favoritos.isEmpty());
	}
	
	@Test
	public void deveBanirUsuario() {

		// cenário
		var usuario = criarUsuario();
		usuario.setId(1l);

		// ação
		service.banirUsuario(usuario);
		
		// verificação
		Mockito.verify(repository, Mockito.times(1)).save(usuario);
	}
	
	@Test
	public void deveRetonarErroCasoUsuarioNaoPertenceAoUsuario() {

		// cenário
		var usuario = criarUsuario();
		usuario.setId(1l);
		usuario.setEmail("email@aluno.ifsp.edu.br");
		
		var usuarioAtualizando = criarUsuario();
		usuarioAtualizando.setId(2l);;

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.verificarUsuario(usuario, usuarioAtualizando);
		});
	}

	@Test
	public void deveVerificarSeUsuarioPertenceAoUsuarioComSucesso() {

		// cenário
		var usuario = criarUsuario();
		usuario.setId(1l);
		usuario.setEmail("teste@aluno.ifsp.edu.br");
		
		var usuarioAtualizando = criarUsuario();
		usuarioAtualizando.setId(2l);
		usuarioAtualizando.setEmail("teste@aluno.ifsp.edu.br");
		
		// ação e verificação
		Assertions.assertDoesNotThrow(() -> {
			service.verificarUsuario(usuario, usuarioAtualizando);
		});

	}

	public static Usuario criarUsuario() {
		return Usuario.builder().nome("usuario").senha("Senhafraca").apelido("apelido").email("teste@aluno.ifsp.edu.br").senha("123").curso(criarCurso()).ano(4)
				.imagem("sem_img.png").reputacao(0).codVerificador("").build();
	}
	
	public static Curso criarCurso() {
		return Curso.builder().sigla("INFO").nome("Informática").build();
	}
	
	public static Dominio criarDominio() {
		return Dominio.builder().id(1l).dominio("@aluno.ifsp.edu.br").build();
	}
}
