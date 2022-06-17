package com.bunkabytes.ifriendsapi.service.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bunkabytes.ifriendsapi.exception.ErroAutenticacao;
import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.repository.UsuarioRepository;
import com.bunkabytes.ifriendsapi.service.UsuarioService;

import lombok.var;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private UsuarioRepository repository;
	private PasswordEncoder encoder;

	public UsuarioServiceImpl(UsuarioRepository repository, PasswordEncoder encoder) {
		super();
		this.repository = repository;
		this.encoder = encoder;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		var usuario = repository.findByEmail(email);

		if (!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não existe.");
		}

		boolean senhasBatem = encoder.matches(senha, usuario.get().getSenha());

		if (!senhasBatem) {
			throw new ErroAutenticacao("Senha inválida.");
		}
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		criptografarSenha(usuario);
		usuario.setReputacao(0);
		return repository.save(usuario);
	}

	public void criptografarSenha(Usuario usuario) {
		String senha = usuario.getSenha();
		String senhaCripto = encoder.encode(senha);
		usuario.setSenha(senhaCripto);
	}

	@Override
	public void validarEmail(String email) {
		
		if (!email.contains("@aluno.ifsp.edu.br")) {
			throw new RegraNegocioException("Email não institucional.");
		}

		if (repository.existsByEmail(email)) {
			throw new RegraNegocioException("Já existe um usuario cadastrado com este email.");
		}

	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		// TODO Auto-generated method stub
		return repository.findById(id);
	}

	@Override
	public Optional<Usuario>  obterPorEmail(String email) {
		var usuario = repository.findByEmail(email);
		if (!usuario.isPresent()) {
			throw new RegraNegocioException("Usuário não encontrado.");
		}
		return usuario;
 
	}

	@Override
	public Long obterPorNome(String nome) {
		return repository.findByNome(nome);
	}
}
