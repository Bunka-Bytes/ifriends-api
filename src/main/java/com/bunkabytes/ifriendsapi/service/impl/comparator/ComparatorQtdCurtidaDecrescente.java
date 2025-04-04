package com.bunkabytes.ifriendsapi.service.impl.comparator;

import java.util.Comparator;

import com.bunkabytes.ifriendsapi.model.entity.Pergunta;

public class ComparatorQtdCurtidaDecrescente implements Comparator<Pergunta>{

	@Override
	public int compare(Pergunta pergunta1, Pergunta pergunta2) {
		if (pergunta1.getQtdCurtida().compareTo(pergunta2.getQtdCurtida()) > 0) { 
			return -1;
		}
		return 1;
	}

}
