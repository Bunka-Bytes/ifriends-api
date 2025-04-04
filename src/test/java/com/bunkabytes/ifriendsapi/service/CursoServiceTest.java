package com.bunkabytes.ifriendsapi.service;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.Curso;
import com.bunkabytes.ifriendsapi.model.repository.CursoRepository;
import com.bunkabytes.ifriendsapi.service.impl.RecursosServiceImpl;

import lombok.var;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Profile("teste1")
public class CursoServiceTest {
	
	@SpyBean
	RecursosServiceImpl service;
	
	@MockBean
	CursoRepository repository;
	
	@Test
	public void deveObterCursosComSucesso() {
		
		//Cenário
		var listaCursos = new ArrayList<Curso>();
		listaCursos.add(criarCurso());
		Mockito.when(repository.findAll(Mockito.any(Sort.class))).thenReturn(listaCursos);
		
		//Ação
		var resultado = service.obterCursos();
		
		//Verificação
		Assertions.assertFalse(resultado.size() == 0);
		Assertions.assertTrue(resultado.equals(listaCursos));
	}
	
	@Test
	public void deveObterCursoPelaSigla() {
		
		//Cenário
		var curso = criarCurso();
		var sigla = "INFO";
		
		Mockito.when(repository.findById(sigla)).thenReturn(Optional.of(curso));
		
		//Ação
		var resultado = service.obterPorSigla(sigla);
		
		//Verificação
		Assertions.assertFalse(resultado == null);
		Assertions.assertTrue(resultado.get().getSigla().equals(sigla));
	}
	
	@Test
	public void deveLancarErroAoObterCursoSemSigla() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
		//Cenário
		var sigla = "INFO";
		
		Mockito.when(repository.findById(sigla)).thenReturn(Optional.empty());
		
		//Ação
		service.obterPorSigla(sigla);
		
		});
	}
	
	public static Curso criarCurso() {
		return Curso.builder().sigla("INFO").nome("Informática").build();
	}
}
