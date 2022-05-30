package com.bunkabytes.ifriendsapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TokenDto {
	
	private String nome;
	private String token;
}
