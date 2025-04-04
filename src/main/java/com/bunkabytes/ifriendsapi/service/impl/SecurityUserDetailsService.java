package com.bunkabytes.ifriendsapi.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SecurityUserDetailsService implements UserDetailsService {

	private UsuarioRepository usuarioRepository;

	public SecurityUserDetailsService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;

	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario usuarioEncontrado = usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Email não cadastrado"));

		if (usuarioEncontrado.isBanido()) {
			log.info("BANIDO");
			return User.builder().username(usuarioEncontrado.getEmail()).password(usuarioEncontrado.getSenha())
					.roles("BANIDO").build();
		} else if (usuarioEncontrado.isAdmin()) {
			log.info("ADMIN");
			return User.builder().username(usuarioEncontrado.getEmail()).password(usuarioEncontrado.getSenha())
					.roles("ADMIN").build();
		} else if (usuarioEncontrado.getCodVerificador() == null) {
			log.info("USER");
			return User.builder().username(usuarioEncontrado.getEmail()).password(usuarioEncontrado.getSenha())
					.roles("USER").build();
		} else {
			log.info("UNVERIFIED");
			return User.builder().username(usuarioEncontrado.getEmail()).password(usuarioEncontrado.getSenha())
					.roles("UNVERIFIED").build();
		}

	}

}
