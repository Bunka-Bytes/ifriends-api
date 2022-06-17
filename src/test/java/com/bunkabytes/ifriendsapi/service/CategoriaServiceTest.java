package com.bunkabytes.ifriendsapi.service;

import java.util.ArrayList;
import java.util.List;
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

import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.Categoria;
import com.bunkabytes.ifriendsapi.model.repository.CategoriaRepository;
import com.bunkabytes.ifriendsapi.service.impl.CategoriaServiceImpl;

import lombok.var;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Profile("testes")
public class CategoriaServiceTest {

	@SpyBean
	CategoriaServiceImpl service;
	@MockBean
	CategoriaRepository repository;

	@Test
	public void deveBuscarCategoriaPorId() {

		// CENÁRIO
		var categoria = criarCategoria();
		Long id = 1l;

		Mockito.when(repository.findById(id)).thenReturn(Optional.of(categoria));

		// AÇÃO E VERIFICAÇÃO
		Assertions.assertDoesNotThrow(() -> {
			var categoriaEncontrada = service.obterPorId(id);
			Assertions.assertEquals(categoriaEncontrada.get().getId(), id);
		});

	}

	@Test
	public void deveLancarErroAoBuscarCategoriaSemId() {

		// CENÁRIO
		Long id = 1l;
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		// AÇÃO E VERIFICAÇÃO
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			var categoriaEncontrada = service.obterPorId(id);
			Assertions.assertNotEquals(categoriaEncontrada.get().getId(), id);
		});

	}

	@Test
	public void deveBuscarCategorias() {

		// CENÁRIO
		List<Categoria> listaCategorias = new ArrayList<>();
		listaCategorias.add(criarCategoria());
		Mockito.when(repository.findAll()).thenReturn(listaCategorias);

		// AÇÃO
		var categorias = service.obterCategorias();

		// VERIFICAÇÃO
		Mockito.verify(repository).findAll();
		Assertions.assertEquals(categorias.get(0).getNome(), "matemática");

	}

	public static Categoria criarCategoria() {
		return Categoria.builder().id(1l).nome("matemática").build();
	}

}
