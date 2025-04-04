package com.bunkabytes.ifriendsapi.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name="palavra_ruim", schema ="producao")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PalavraRuim {

	@Id
	@Column(name = "id_palavra_ruim")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "palavra")
	private String palavra;
}
