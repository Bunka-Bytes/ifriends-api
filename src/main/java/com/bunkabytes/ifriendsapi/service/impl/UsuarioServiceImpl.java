package com.bunkabytes.ifriendsapi.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bunkabytes.ifriendsapi.exception.ErroAutenticacao;
import com.bunkabytes.ifriendsapi.exception.RegraNegocioException;
import com.bunkabytes.ifriendsapi.model.entity.CurtePerg;
import com.bunkabytes.ifriendsapi.model.entity.CurteResp;
import com.bunkabytes.ifriendsapi.model.entity.Dominio;
import com.bunkabytes.ifriendsapi.model.entity.FavoritaEvento;
import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.enums.Pontuacao;
import com.bunkabytes.ifriendsapi.model.repository.CurtePergRepository;
import com.bunkabytes.ifriendsapi.model.repository.CurteRespRepository;
import com.bunkabytes.ifriendsapi.model.repository.DominioRepository;
import com.bunkabytes.ifriendsapi.model.repository.FavoritaEventoRepository;
import com.bunkabytes.ifriendsapi.model.repository.UsuarioRepository;
import com.bunkabytes.ifriendsapi.service.UsuarioService;

import lombok.var;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UsuarioRepository repository;
	@Autowired
	private CurtePergRepository curtePergRepository;
	@Autowired
	private CurteRespRepository curteRespRepository;
	@Autowired
	private DominioRepository dominioRepository;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private FavoritaEventoRepository favoritaEventoRepository;
	
	@Override
	public List<Usuario> buscar(Usuario usuarioFiltro) {
		var example = Example.of(usuarioFiltro, ExampleMatcher
				.matchingAny()
				.withIgnoreNullValues()
				.withIgnoreCase()
				.withIgnorePaths("admin")
				.withIgnorePaths("banido")
				.withStringMatcher(StringMatcher.CONTAINING));
		var usuarios = repository.findAll(example, Sort.by("nome").ascending());
		
		return usuarios;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		var usuario = repository.findByEmail(email);

		if (!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não existe.");
		}
		
		if (usuario.get().getCodVerificador() != null) {
			throw new ErroAutenticacao("Necessário confirmar e-mail para entrar no sistema.");
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
		//validando usuario já existente
		if (repository.existsByEmail(usuario.getEmail())) {
			throw new RegraNegocioException("Já existe um usuario cadastrado com este email.");
		}
		validar(usuario);
		if (usuario.getSenha().length() < 6 || usuario.getSenha().length() > 12)
			throw new RegraNegocioException("Senha deve ser entre 6 e 12 caracteres.");
		
		criptografarSenha(usuario);
		usuario.setReputacao(0);
		usuario.setCodVerificador(UUID.randomUUID().toString());
		return repository.save(usuario);
	}

	public void criptografarSenha(Usuario usuario) {
		var senha = usuario.getSenha();
		var senhaCripto = encoder.encode(senha);
		usuario.setSenha(senhaCripto);
	}
	
	@Override
	@Transactional
	public Usuario atualizar(Usuario modificacoes) {
		Objects.requireNonNull(modificacoes.getId());
		var usuarioAAtualizar = repository.findById(modificacoes.getId()).get();
		usuarioAAtualizar.setApelido(modificacoes.getApelido());
		usuarioAAtualizar.setNome(modificacoes.getNome());
		usuarioAAtualizar.setBio(modificacoes.getBio());
		usuarioAAtualizar.setImagem(modificacoes.getImagem());
		usuarioAAtualizar.setAno(modificacoes.getAno());
		validar(usuarioAAtualizar);
		return repository.save(usuarioAAtualizar);
	}


	@Override
	public void validar(Usuario usuario) {
		//validando variaveis vazias
		if (usuario.getEmail() == null)
			throw new RegraNegocioException("Email é obrigatório.");
		
		if (usuario.getEmail().split("@")[0].trim().equals("") ) 
			throw new RegraNegocioException("Informe um email válido.");
		
		if (usuario.getNome() == null || usuario.getNome().trim().equals(""))
			throw new RegraNegocioException("Informe um nome válido.");
		
		if (usuario.getApelido() == null || usuario.getApelido().trim().equals("")) 
			throw new RegraNegocioException("Informe um apelido válido.");
	
		if (usuario.getSenha() == null || usuario.getSenha().trim().equals(""))
			throw new RegraNegocioException("Informe uma senha válida.");
		
		if (usuario.getAno() == null)
			throw new RegraNegocioException("Informe um Ano válido.");
		
		//validando dominios 
		var contemDominioInstitucional = false;
		for (Dominio dominioInstitucional : dominioRepository.findAll()) {
			if (usuario.getEmail().contains(dominioInstitucional.getDominio()))
				contemDominioInstitucional = true;
		}
		if(!contemDominioInstitucional)
			throw new RegraNegocioException("Apenas email institucional.");
		
		//Validando tamanho dos campos
		if (usuario.getEmail().length() > 100)
			throw new RegraNegocioException("Email deve ter menos que 100 caracteres");
		
		if (usuario.getNome().length() > 60)
			throw new RegraNegocioException("Nome deve ter menos que 60 caracteres");
		
		if (usuario.getApelido().length() > 50) 
			throw new RegraNegocioException("Apelido deve ter menos que 50 caracteres.");
		
		if (usuario.getAno() > 8 || usuario.getAno() < 0)
			throw new RegraNegocioException("Ano deve ser entre 0 e 8");
		
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		var usuario = repository.findById(id);
		if (!usuario.isPresent())
			throw new RegraNegocioException("Usuario não existe na base de dados.");
		
		return usuario;
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

	@Override
	public List<CurtePerg> obterPerguntasCurtidas(Usuario usuario) {
		var curtidas = curtePergRepository.findByUsuario(usuario);
		return curtidas;
	}
	
	@Override
	public List<CurteResp> obterRespostasCurtidas(Usuario usuario) {
		var curtidas = curteRespRepository.findByUsuario(usuario);
		return curtidas;
	}
	
	@Override
	public List<FavoritaEvento> obterEventosFavoritados(Usuario usuario) {
		var favoritados = favoritaEventoRepository.findByUsuario(usuario);
		return favoritados;
	}

	@Override
	public void salvarPontuacao(Pontuacao pontuacao, Usuario usuario) {
		var total = usuario.getReputacao() + pontuacao.getPontos();
		usuario.setReputacao(total);
		repository.save(usuario);
	}

	@Override
	public void verificarEmail(String codVerificador) {
		var usuarioAVerificar = repository.findByCodVerificador(codVerificador);
		if (!usuarioAVerificar.isPresent()) 
			throw new ErroAutenticacao("Código inválido.");
		usuarioAVerificar.get().setCodVerificador(null);
		repository.save(usuarioAVerificar.get());
	}
	
	@Override
	@Transactional
	public void banirUsuario(Usuario usuario) {
		usuario.setBanido(true);
		repository.save(usuario);
	}

	@Override
	public String criptografarCodigo(String codigo) {
		
		StringBuilder codigoCriptografado = new StringBuilder(codigo);
		codigo = codigoCriptografado.reverse().toString();
		return codigo.replaceAll("a", "A").replaceAll("e", "E").replaceAll("i", "I").replaceAll("o", "O").replaceAll("u", "U");
	}

	@Override
	public void verificarUsuario(Usuario usuarioAAtualizar, Usuario usuarioAtualizando) {
		if (!usuarioAAtualizar.getEmail().equals(usuarioAtualizando.getEmail()) && !usuarioAtualizando.isAdmin()) {
			throw new RegraNegocioException("Cadastro não pertence ao usuário.");
		}
		
	}

}
