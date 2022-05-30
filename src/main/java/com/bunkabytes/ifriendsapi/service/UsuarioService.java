package com.bunkabytes.ifriendsapi.service;

import java.util.Optional;

import com.bunkabytes.ifriendsapi.model.entity.Usuario;

public interface UsuarioService {
	
	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
	
	Optional<Usuario> obterPorId(Long id);
	
	Optional<Usuario> obterPorNome(String nome);
	
	Optional<Usuario> obterPorEmail(String email);
}
