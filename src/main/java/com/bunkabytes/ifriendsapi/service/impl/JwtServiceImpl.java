package com.bunkabytes.ifriendsapi.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bunkabytes.ifriendsapi.model.entity.Usuario;
import com.bunkabytes.ifriendsapi.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtServiceImpl implements JwtService {
	
	@Value("${jwt.expiracao}")
	private String expiracao;
	@Value("${jwt.chave-assinatura}")
	private String chaveAssinatura;

	@Override
	public String gerarToken(Usuario usuario) {
		Long exp = Long.valueOf(expiracao);
		LocalDateTime dataHoraExpiracao = LocalDateTime.now().plusMinutes(exp);
		Instant instant = dataHoraExpiracao.atZone(ZoneId.systemDefault()).toInstant();
		java.util.Date data = Date.from(instant);
		String token = Jwts
						.builder()
						.setExpiration(data)
						.setSubject(usuario.getEmail())
						.claim("idUsuario", usuario.getId())
						.claim("nome", usuario.getNome())
						.signWith(SignatureAlgorithm.HS512, chaveAssinatura)
						.compact();
		return token;
	}

	@Override
	public Claims obterClaims(String authorization) throws ExpiredJwtException {
		String token = authorization;
		if(authorization.contains("Bearer")) {
			token = authorization.split(" ")[1];
		}
		return Jwts
				.parser()
				.setSigningKey(chaveAssinatura)
				.parseClaimsJws(token)
				.getBody();
				
	}

	@Override
	public boolean isTokenValido(String token) {
		try {
			Claims claims = obterClaims(token);
			java.util.Date dataEx = claims.getExpiration();
			
			LocalDateTime dataExpiracao = dataEx.toInstant()
						.atZone(ZoneId.systemDefault()).toLocalDateTime();
			
			boolean dataHoraAtualIsAfterDataExpiracao = LocalDateTime.now().isAfter(dataExpiracao);
			return !dataHoraAtualIsAfterDataExpiracao;
		} catch (ExpiredJwtException e) {
			return false;
		}
	}

	@Override
	public String obterLoginUsuario(String token) {
		Claims claims = obterClaims(token);
		
		return claims.getSubject();
		
	}

}
