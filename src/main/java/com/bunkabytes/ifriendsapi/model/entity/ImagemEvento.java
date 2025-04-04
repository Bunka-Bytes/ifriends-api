package com.bunkabytes.ifriendsapi.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name="Imagem_evento", schema ="producao")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagemEvento {
	
	@Id
	@Column(name = "id_imagem_evento") 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "link_imagem")
	private String link;
	
	@ManyToOne
	@JoinColumn(name = "id_evento")
	@JsonIgnore
	private Evento evento;
	

}
