package com.bunkabytes.ifriendsapi.api.dto;


import java.util.List;

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
public class PerguntaDto {
	
	private String titulo;
	private String texto;
	private List<String> tags;
	private Long categoria;
}
