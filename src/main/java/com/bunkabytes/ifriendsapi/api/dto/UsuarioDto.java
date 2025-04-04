package com.bunkabytes.ifriendsapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {

	private String email;
	private String nome;
	private String apelido;
	private String bio;
	private String senha;
	private String curso;
	private String imagem;
	private Integer ano;
}
