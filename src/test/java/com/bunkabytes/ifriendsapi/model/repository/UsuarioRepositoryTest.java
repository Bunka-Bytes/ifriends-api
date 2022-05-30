//package com.bunkabytes.ifriendsapi.model.repository;
//
//
//import org.assertj.core.api.Assertions;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.context.annotation.Profile;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import com.bunkabytes.ifriendsapi.model.entity.Usuario;
//
//@ExtendWith(SpringExtension.class)
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = Replace.NONE)
//@Profile("testeFinal")
//public class UsuarioRepositoryTest {
//	
//	@Autowired
//	@Qualifier("usuario")
//	public UsuarioRepository repository;
//	
//	@Autowired
//	TestEntityManager entityManager;
//	
//	@Test
//	public void deveVerificarAExistenciaDeUmEmail() {
//		//cenário
//		Usuario usuario  = criarUsuario();
//		entityManager.persist(usuario);
//		
//		//ação
//		boolean result = repository.existsByEmail("kaiky.br34@gmail.com");
//		
//		//verificação
//		Assertions.assertThat(result).isTrue();
//	}
//	
//	
//	
//	@Test
//	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComEmail() {
//		//canário
//		
//		
//		//ação
//		boolean result = repository.existsByEmail("kaiky.br34@gmail.com");
//		
//		//verificação
//		Assertions.assertThat(result).isFalse();
//	}
//	
//	@Test
//	public void devePersistirUmUsuarioNaBaseDeDados(){
//		
//		//canário
//		Usuario usuario = criarUsuario();
//		
//		//ação
//		Usuario usuarioSalvo = repository.save(usuario);
//		
//		//verificação
//		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
//	}
//	
//	@Test
//	public void deveBuscarUsuarioPorEmail(){
//		
//		//canário
//		Usuario usuario = criarUsuario();
//		entityManager.persist(usuario);
//		
//		//verificação
//		Optional<Usuario> result = repository.findByEmail("kaiky.br34@gmail.com");
//		
//		Assertions.assertThat(result.isPresent()).isTrue();
//	}
//	
//	@Test
//	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase(){
//		
//		//verificação
//		Optional<Usuario> result = repository.findByEmail("kaiky.br34@gmail.com");
//		
//		Assertions.assertThat(result.isPresent()).isFalse();
//	}
//	
//	public static Usuario criarUsuario() {
//		return Usuario
//				.builder()
//				.nome("usuario")
//				.email("kaiky.br34@gmail.com")
//				.senha("bunka413")
//				.curso("Informática")
//				.ano(4)
//				.imagem("sem_img.png")
//				.build();
//	}
//}
