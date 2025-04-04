package com.bunkabytes.ifriendsapi.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.bunkabytes.ifriendsapi.model.entity.ImagemEvento;

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
public class EventoDto {
	
	private String nome;
	private String local;
	private LocalDateTime dataEvento;
	private String descricao;
	private Boolean presencial;
	private String link;
	private Long categoria;
	private List<ImagemEvento> imagens;
	private List<String> tags;
}
