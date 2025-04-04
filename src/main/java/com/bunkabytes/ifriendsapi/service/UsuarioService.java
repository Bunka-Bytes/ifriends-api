package com.bunkabytes.ifriendsapi.service;

import java.util.List;
import java.util.Optional;

import com.bunkabytes.ifriendsapi.model.entity.CurtePerg;
import com.bunkabytes.ifriendsapi.model.entity.CurteResp;
import com.bunkabytes.ifriendsapi.model.entity.FavoritaEvento;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.enums.Pontuacao;

public interface UsuarioService {
	
	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	Usuario atualizar (Usuario usuario);
	
	void validar(Usuario usuario);
	
	void verificarEmail(String codVerificador);
	
	String criptografarCodigo(String codigo);
	
	Optional<Usuario> obterPorId(Long id);
	
	Long obterPorNome(String nome);
	
	Optional<Usuario> obterPorEmail(String email);
	
	List<CurtePerg> obterPerguntasCurtidas(Usuario usuario);
	
	List<CurteResp> obterRespostasCurtidas(Usuario usuario);
	
	List<FavoritaEvento> obterEventosFavoritados(Usuario usuario);
	
	List<Usuario> buscar(Usuario usuarioFiltro);
	
	void salvarPontuacao(Pontuacao pontuacao, Usuario usuario);
	
	void verificarUsuario(Usuario usuarioAAtualizar, Usuario usuarioAtualizando);
	
	void banirUsuario(Usuario usuario);

}
