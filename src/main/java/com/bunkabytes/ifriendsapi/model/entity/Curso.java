package com.bunkabytes.ifriendsapi.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name="Curso", schema ="producao")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {
	
	@Id
	@Column(name = "sigla_curso")
	private String sigla;
	
	@Column(name = "nome_curso")
	private String nome;
	
	@ManyToOne
	@JoinColumn(name = "id_tipo_curso")
	private TipoCurso tipoCurso;
}
