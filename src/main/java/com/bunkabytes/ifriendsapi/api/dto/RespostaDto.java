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
public class RespostaDto {
	
	private Long id;
	private String texto;
	private boolean aceita;
	private Long pergunta;
	private Long usuario;
}
