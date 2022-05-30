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

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Profile("testeFinal")
public class UsuarioServiceTest {
	@SpyBean
	UsuarioServiceImpl service;
	PasswordEncoder encoder;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test
	public void deveSalvarUmUsuario() {
		Assertions.assertDoesNotThrow(() -> {
		//cenário
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder()
				.id(1l)
				.nome("kaiky")
				.email("email@email.com")
				.senha("bunka413")
				.curso("informática")
				.ano(4)
				.build();
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//ação
		Usuario usuarioSalvo = service.salvarUsuario(usuario);
		
		//verificação
		Assertions.assertNotNull(usuarioSalvo); 
		Assertions.assertEquals(usuarioSalvo.getId(), 1l);
		Assertions.assertEquals(usuarioSalvo.getNome(), "kaiky");
		Assertions.assertEquals(usuarioSalvo.getEmail(), "email@email.com");
		Assertions.assertNotEquals(usuarioSalvo.getSenha(), "bunka413");
		Assertions.assertEquals(usuarioSalvo.getCurso(), "informática");
		Assertions.assertEquals(usuarioSalvo.getAno(), 4);
		});
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			
		//cenário
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(usuario.getEmail());
		
		//ação
		service.salvarUsuario(usuario);
		
		//verificação - Eu espero que ele não tenha chamado o método de salvar meu usuario
		Mockito.verify(repository, Mockito.never()).save(usuario);
		
		});
	}

	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		Assertions.assertDoesNotThrow(()-> {
			
		//cenário
		String nome = "kaiky";
		String email = "kaiky.br34@gmail.com";
		String senha = "senhagenerica123";
		
		
		Usuario usuario = Usuario.builder().nome(nome).email(email).senha(senha).id(1l).build();
		service.criptografarSenha(usuario);
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//ação
		Usuario result = service.autenticar(email, senha);
		
		//verificação
		Assertions.assertNotNull(result);
	});
	}
	
	@Test
	public void deveLancarErroQuandoNaoHouverUsuarioCadastradoComOEmailInformado() {
		//cenário
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//ação
		Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.autenticar("kaiky.br34@gmail.com", "senhagenerica123"));
		
		//verificação
		org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não existe.");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {

		//cenário
		String senha = "senhagenerica123";
		Usuario usuario = Usuario.builder().nome("kaiky").email("kaiky.br34@gmail.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//ação
		Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.autenticar("kaiky.br34@gmail.com", "123"));
		org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida.");
	}
	
	@Test
	public void deveValidarEmail() {
		Assertions.assertDoesNotThrow(()-> {
		//cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		
		//ação
		service.validarEmail("email@email.com");
	});
	}
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
		//cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//ação
		service.validarEmail("email@email.com");
		});	
	}
}
