package com.bunkabytes.ifriendsapi.service.impl;


import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.model.repository.UsuarioRepository;



@Service
public class SecurityUserDetailsService implements UserDetailsService{
	
	
	private UsuarioRepository usuarioRepository;

	public SecurityUserDetailsService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
		
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario usuarioEncontrado = usuarioRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email não cadastrado"));
		
		if(usuarioEncontrado.getEmail().equals("bunkabytes@gmail.com")) {
			return User.builder()
					.username(usuarioEncontrado.getEmail())
					.password(usuarioEncontrado.getSenha())
					.roles("ADMIN")
					.build();
		}
		else {
			return User.builder()
					.username(usuarioEncontrado.getEmail())
					.password(usuarioEncontrado.getSenha())
					.roles("USER")
					.build();
		}

	}

}
