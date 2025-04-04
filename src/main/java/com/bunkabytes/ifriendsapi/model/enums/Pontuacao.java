package com.bunkabytes.ifriendsapi.model.enums;

public enum Pontuacao {

	PERGUNTA(5),
	RESPOSTA(5),
	EVENTO(15),
	CURTIR(2),
	DESCURTIR(-2),
	ACEITAR(8),
	DESACEITAR(-8);
	
	private Integer pontos;
	
	Pontuacao(Integer pontos) {
		this.pontos = pontos;
	}
	
	public Integer getPontos() {
		return pontos;
	}

}
