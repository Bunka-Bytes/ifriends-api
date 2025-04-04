package com.bunkabytes.ifriendsapi.api.dto;

import java.util.List;

import com.bunkabytes.ifriendsapi.model.entity.ImagemResp;

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
	
	private String texto;
	private Long pergunta;
	private List<ImagemResp> imagens;
}
