package com.bunkabytes.ifriendsapi.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bunkabytes.ifriendsapi.exception.ErroAutenticacao;
import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.repository.UsuarioRepository;
import com.bunkabytes.ifriendsapi.service.impl.UsuarioServiceImpl;

import lombok.var;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Profile("testes")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;
	PasswordEncoder encoder;

	@MockBean
	UsuarioRepository repository;

	@Test
	public void deveSalvarUmUsuario() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
			Usuario usuario = criarUsuario();
			usuario.setId(1l);

			Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

			// ação
			Usuario usuarioSalvo = service.salvarUsuario(usuario);

			// verificação
			Assertions.assertNotNull(usuarioSalvo);
			Assertions.assertEquals(usuarioSalvo.getId(), 1l);
			Assertions.assertEquals(usuarioSalvo.getNome(), "usuario");
			Assertions.assertEquals(usuarioSalvo.getEmail(), "kaiky.br34@gmail.com");
			Assertions.assertNotEquals(usuarioSalvo.getSenha(), "123");
			Assertions.assertEquals(usuarioSalvo.getCurso(), "Informática");
			Assertions.assertEquals(usuarioSalvo.getAno(), 4);
		});
	}

	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {

			// cenário
			String email = "email@email.com";
			Usuario usuario = new Usuario();
			usuario.setEmail(email);

			Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(usuario.getEmail());

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
			String nome = "kaiky";
			String email = "kaiky.br34@gmail.com";
			String senha = "senhagenerica123";

			Usuario usuario = Usuario.builder().nome(nome).email(email).senha(senha).id(1l).build();
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
		String senha = "senhagenerica123";
		Usuario usuario = Usuario.builder().nome("kaiky").email("kaiky.br34@gmail.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

		// ação e verificação
		Assertions.assertThrows(ErroAutenticacao.class, () -> service.autenticar("kaiky.br34@gmail.com", "123"));
	}

	@Test
	public void deveValidarEmail() {

		// cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

		// ação e verificação
		Assertions.assertDoesNotThrow(() -> service.validarEmail("email@aluno.ifsp.edu.br"));

	}

	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {

		// cenário
		String email = "email@aluno.ifsp.edu.br";
		Mockito.when(repository.existsByEmail(email)).thenReturn(true);

		// ação e verificações
		Assertions.assertThrows(RegraNegocioException.class, () -> service.validarEmail(email));

	}

	@Test
	public void deveLancarErroAoValidarEmailNaoInstitucional() {

		// cenário
		String email = "email@email.com";
		Mockito.when(repository.existsByEmail(email)).thenReturn(false);

		// ação
		Assertions.assertThrows(RegraNegocioException.class, () -> service.validarEmail("email@gmail.com"));

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
	public void deveObterUsuarioPorEmail() {

		// cenário
		String email = "teste@aluno.ifsp.edu.br";
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
	public void deveLancarErroAoObterUsuarioSemEmail() {

		// cenário
		String email = "teste@aluno.ifsp.edu.br";
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.empty());

		// ação e verificação
		Assertions.assertThrows(RegraNegocioException.class, () -> service.obterPorEmail(email));

	}

	@Test
	public void deveObterUsuarioPorNome() {

		// cenário
		String nome = "usuario";
		Mockito.when(repository.findByNome(nome)).thenReturn(1l);

		// ação
		service.obterPorNome(nome);

		// verificação
		Mockito.verify(repository).findByNome(nome);

	}

	public static Usuario criarUsuario() {
		return Usuario.builder().nome("usuario").email("kaiky.br34@gmail.com").senha("123").curso("Informática").ano(4)
				.imagem("sem_img.png").build();
	}
}
